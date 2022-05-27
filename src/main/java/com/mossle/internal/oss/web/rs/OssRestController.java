package com.mossle.internal.oss.web.rs;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.util.BaseDTO;

import com.mossle.internal.oss.persistence.domain.OssAccess;
import com.mossle.internal.oss.persistence.manager.OssAccessManager;
import com.mossle.internal.oss.service.OssService;
import com.mossle.internal.oss.support.ImageProcessor;
import com.mossle.internal.oss.support.OssConfigDTO;
import com.mossle.internal.oss.support.OssDTO;

import com.mossle.spi.rpc.RpcAuthHelper;
import com.mossle.spi.rpc.RpcAuthResult;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;

@RestController
@RequestMapping("oss/rs/**")
public class OssRestController {
    private static Logger logger = LoggerFactory
            .getLogger(OssRestController.class);
    public static final String PREFIX = "/oss/rs";
    private JsonMapper jsonMapper = new JsonMapper();
    private OssAccessManager ossAccessManager;
    private RpcAuthHelper rpcAuthHelper;
    private OssService ossService;
    private MultipartResolver multipartResolver;
    private ImageProcessor imageProcessor = new ImageProcessor();

    /**
     * 上传文件，指定文件名.
     */
    @RequestMapping(method = RequestMethod.PUT)
    public void putObject(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.info("put");

        OssAccess ossAccess = this.checkAuthorization(request, response);

        if (ossAccess == null) {
            return;
        }

        try {
            OssConfigDTO ossConfigDto = this.processConfig(request);
            logger.debug("ossConfigDto : {}", ossConfigDto);
            logger.debug("ossService : {}", ossService);

            // String objectName = ossConfigDto.getObjectName(request);
            OssDTO ossDto = ossService.putObject(request.getInputStream(),
                    ossConfigDto.getBucketName(), ossConfigDto.getObjectName());

            BaseDTO baseDto = new BaseDTO();
            baseDto.setCode(200);
            // baseDto.setData(storeDto.getKey());
            baseDto.setData(ossConfigDto.getObjectName());

            response.setContentType("application/json");
            response.getWriter().write(jsonMapper.toJson(baseDto));
        } catch (Exception ex) {
            this.sendError(response, ex);
        }
    }

    /**
     * 上传文件，自动生成文件名.
     */
    @RequestMapping(method = RequestMethod.POST)
    public void postObject(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        if (multipartResolver.isMultipart(request)) {
            this.postObjectMultipart(request, response);
        } else {
            this.postObjectRaw(request, response);
        }
    }

    /**
     * 上传文件，自动生成文件名. client模式，非multipart模式
     */
    public void postObjectRaw(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.info("post");

        OssAccess ossAccess = this.checkAuthorization(request, response);

        if (ossAccess == null) {
            return;
        }

        try {
            OssConfigDTO ossConfigDto = this.processBucket(request);
            OssDTO ossDto = ossService.postObject(request.getInputStream(),
                    ossConfigDto.getBucketName());

            BaseDTO baseDto = new BaseDTO();
            baseDto.setCode(200);
            baseDto.setData(ossDto.getObjectName());

            response.setContentType("application/json");
            response.getWriter().write(jsonMapper.toJson(baseDto));
        } catch (Exception ex) {
            this.sendError(response, ex);
        }
    }

    /**
     * 上传文件，自动生成文件名. form模式，multipart模式
     */
    public void postObjectMultipart(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.info("post multipart");
        logger.debug("request : {}", request.getClass());

        MultipartHttpServletRequest req = (MultipartHttpServletRequest) request;

        try {
            // req = multipartResolver.resolveMultipart(request);
            logger.debug("file map : {}", req.getFileMap());
            logger.debug("param map : {}", req.getParameterMap());

            OssAccess ossAccess = this.checkAuthorization(req, response);

            if (ossAccess == null) {
                return;
            }

            OssConfigDTO ossConfigDto = this.processBucket(req);
            logger.debug("file map : {}", req.getFileMap());

            MultipartFile multipartFile = req.getFile("file");

            if (multipartFile == null) {
                logger.info("cannot find multipart file");

                return;
            }

            OssDTO ossDto = ossService.postObject(
                    multipartFile.getInputStream(),
                    ossConfigDto.getBucketName());

            Map<String, Object> data = new HashMap<String, Object>();
            data.put("objectName", ossDto.getObjectName());
            data.put("fileName", multipartFile.getOriginalFilename());
            data.put("fileSize", multipartFile.getSize());
            data.put("contentType", multipartFile.getContentType());

            BaseDTO baseDto = new BaseDTO();
            baseDto.setCode(200);
            baseDto.setData(data);

            response.setContentType("application/json");
            response.getWriter().write(jsonMapper.toJson(baseDto));
        } catch (Exception ex) {
            this.sendError(response, ex);
        }
    }

    /**
     * 下载文件.
     */
    @RequestMapping(method = RequestMethod.GET)
    public void getObject(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.info("get");

        OssAccess ossAccess = this.checkAuthorization(request, response);

        if (ossAccess == null) {
            return;
        }

        OssConfigDTO ossConfigDto = this.processConfig(request);
        String bucketName = ossConfigDto.getBucketName();
        String objectName = ossConfigDto.getObjectName();

        OssDTO ossDto = ossService.getObject(bucketName, objectName);

        if (ossDto == null) {
            logger.info("cannot find {} {}", bucketName, objectName);
            response.sendError(HttpServletResponse.SC_NOT_FOUND);

            return;
        }

        String xOssProcess = request.getParameter("x-oss-process");

        if (StringUtils.isBlank(xOssProcess)) {
            IOUtils.copy(ossDto.getInputStream(), response.getOutputStream());
        } else {
            imageProcessor.process(ossDto.getInputStream(),
                    response.getOutputStream(), xOssProcess);
        }
    }

    /**
     * 判断文件是否存在.
     */
    @RequestMapping(method = RequestMethod.HEAD)
    public void doesObjectExist(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.info("head");

        OssAccess ossAccess = this.checkAuthorization(request, response);

        if (ossAccess == null) {
            return;
        }

        try {
            OssConfigDTO ossConfigDto = this.processConfig(request);
            OssDTO ossDto = ossService.doesObjectExist(
                    ossConfigDto.getBucketName(), ossConfigDto.getObjectName());

            BaseDTO baseDto = new BaseDTO();
            baseDto.setCode(200);
            baseDto.setData(ossDto != null);

            response.setContentType("application/json");
            response.getWriter().write(jsonMapper.toJson(baseDto));
        } catch (Exception ex) {
            this.sendError(response, ex);
        }
    }

    /**
     * 删除文件.
     */
    @RequestMapping(method = RequestMethod.DELETE)
    public void deleteObject(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.info("delete");

        OssAccess ossAccess = this.checkAuthorization(request, response);

        if (ossAccess == null) {
            return;
        }

        try {
            OssConfigDTO ossConfigDto = this.processConfig(request);
            OssDTO ossDto = ossService.deleteObject(
                    ossConfigDto.getBucketName(), ossConfigDto.getObjectName());
            BaseDTO baseDto = new BaseDTO();
            baseDto.setCode(200);

            response.setContentType("application/json");
            response.getWriter().write(jsonMapper.toJson(baseDto));
        } catch (Exception ex) {
            this.sendError(response, ex);
        }
    }

    // ~ ======================================================================
    public OssConfigDTO processConfig(HttpServletRequest request) {
        // logger.info("{}", request.getRequestURL());
        logger.info("{}", request.getRequestURI());

        String prefix = request.getContextPath() + PREFIX;
        String suffix = request.getRequestURI().substring(prefix.length() + 1);

        if (StringUtils.isBlank(suffix)) {
            logger.info("suffix cannot blank");

            return null;
        }

        if (suffix.indexOf("/") == -1) {
            logger.info("suffix not contains slash : {}", suffix);

            return null;
        }

        logger.debug("prefix : {}", prefix);
        logger.debug("suffix : {}", suffix);

        int index = suffix.indexOf("/");
        String bucketName = suffix.substring(0, index);
        String objectName = suffix.substring(index + 1);

        if (StringUtils.isBlank(bucketName)) {
            logger.info("bucketName cannot blank");

            return null;
        }

        if (StringUtils.isBlank(objectName)) {
            logger.info("objectName cannot blank");

            return null;
        }

        OssConfigDTO ossConfigDto = new OssConfigDTO();
        ossConfigDto.setBucketName(bucketName);
        ossConfigDto.setObjectName(objectName);

        return ossConfigDto;
    }

    public OssConfigDTO processBucket(HttpServletRequest request) {
        // logger.info("{}", request.getRequestURL());
        logger.info("{}", request.getRequestURI());

        String prefix = request.getContextPath() + PREFIX;
        String suffix = request.getRequestURI().substring(prefix.length() + 1);

        if (StringUtils.isBlank(suffix)) {
            logger.info("suffix cannot blank");

            return null;
        }

        logger.debug("prefix : {}", prefix);
        logger.debug("suffix : {}", suffix);

        int index = suffix.indexOf("/");

        String bucketName = suffix;

        if (index > -1) {
            bucketName = suffix.substring(0, index);
        }

        if (StringUtils.isBlank(bucketName)) {
            logger.info("bucketName cannot blank");

            return null;
        }

        OssConfigDTO ossConfigDto = new OssConfigDTO();
        ossConfigDto.setBucketName(bucketName);

        return ossConfigDto;
    }

    public void sendError(HttpServletResponse response, Exception ex)
            throws Exception {
        logger.error(ex.getMessage(), ex);

        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(500);
        baseDto.setData(ex.getMessage());

        response.setContentType("application/json");
        response.getWriter().write(jsonMapper.toJson(baseDto));
    }

    public OssAccess checkAuthorization(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        try {
            logger.debug("method : {}", request.getMethod());
            logger.debug("date : {}", request.getHeader("date"));
            logger.debug("path : {}", request.getRequestURL());
            logger.debug("path : {}", request.getRequestURI());
            logger.debug(
                    "path : {}",
                    request.getRequestURI().substring(
                            request.getContextPath().length()));
            logger.debug("x-request-id : {}", request.getHeader("x-request-id"));
            logger.debug("content-type : {}", request.getHeader("content-type"));
            logger.debug("content-md5 : {}", request.getHeader("content-md5"));

            String authorization = request.getHeader("Authorization");

            // AuthResult authResult = AuthResult
            // .parseAuthorization(authorization);
            RpcAuthResult rpcAuthResult = rpcAuthHelper.authenticateStore(
                    request, new AccessSecretHelperImpl(ossAccessManager));

            if (!rpcAuthResult.isSuccess()) {
                logger.warn("access denied : {}", authorization);

                // BaseDTO baseDto = new BaseDTO();
                // baseDto.setCode(403);
                // baseDto.setMessage("access denied");

                // response.setContentType("application/json");
                // response.getWriter().write(jsonMapper.toJson(baseDto));
                response.sendError(HttpServletResponse.SC_FORBIDDEN);

                return null;
            }

            return ossAccessManager.findUniqueBy("accessKey",
                    rpcAuthResult.getAccessKey());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            this.sendError(response, ex);

            return null;
        }
    }

    @Resource
    public void setOssAccessManager(OssAccessManager ossAccessManager) {
        this.ossAccessManager = ossAccessManager;
    }

    @Resource
    public void setRpcAuthHelper(RpcAuthHelper rpcAuthHelper) {
        this.rpcAuthHelper = rpcAuthHelper;
    }

    @Resource
    public void setOssService(OssService ossService) {
        this.ossService = ossService;
    }

    @Resource
    public void setMultipartResolver(MultipartResolver multipartResolver) {
        this.multipartResolver = multipartResolver;
    }
}
