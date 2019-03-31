package com.mossle.internal.store.web.rs;

import java.io.InputStream;

import javax.activation.DataSource;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.auth.CustomPasswordEncoder;
import com.mossle.api.store.StoreConnector;
import com.mossle.api.store.StoreDTO;
import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserCache;
import com.mossle.api.user.UserDTO;

import com.mossle.core.export.Exportor;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.spring.MessageHelper;
import com.mossle.core.store.InputStreamDataSource;
import com.mossle.core.util.BaseDTO;

import com.mossle.internal.store.persistence.domain.StoreApp;
import com.mossle.internal.store.persistence.manager.StoreAppManager;

import com.mossle.spi.rpc.RpcAuthHelper;
import com.mossle.spi.rpc.RpcAuthResult;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.MediaType;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequestMapping("store/rs/**")
public class StoreRestController {
    private static Logger logger = LoggerFactory
            .getLogger(StoreRestController.class);
    private StoreConnector storeConnector;
    private JsonMapper jsonMapper = new JsonMapper();
    private StoreAppManager storeAppManager;
    private RpcAuthHelper rpcAuthHelper;

    @RequestMapping(method = RequestMethod.PUT)
    public void putObject(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.info("put");

        StoreApp storeApp = this.checkAuthorization(request, response);

        if (storeApp == null) {
            return;
        }

        try {
            String objectName = this.findObjectName(request);
            String model = storeApp.getModelCode();
            String key = objectName;
            DataSource dataSource = new InputStreamDataSource(objectName,
                    request.getInputStream());
            String tenantId = "1";
            StoreDTO storeDto = storeConnector.saveStore(model, key,
                    dataSource, tenantId);

            BaseDTO baseDto = new BaseDTO();
            baseDto.setCode(200);
            baseDto.setData(storeDto.getKey());

            response.setContentType("application/json");
            response.getWriter().write(jsonMapper.toJson(baseDto));
        } catch (Exception ex) {
            this.sendError(response, ex);
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public void postObject(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.info("post");

        StoreApp storeApp = this.checkAuthorization(request, response);

        if (storeApp == null) {
            return;
        }

        try {
            String model = storeApp.getModelCode();
            DataSource dataSource = new InputStreamDataSource(
                    request.getInputStream());
            String tenantId = "1";
            StoreDTO storeDto = storeConnector.saveStore(model, dataSource,
                    tenantId);

            BaseDTO baseDto = new BaseDTO();
            baseDto.setCode(200);
            baseDto.setData(storeDto.getKey());

            response.setContentType("application/json");
            response.getWriter().write(jsonMapper.toJson(baseDto));
        } catch (Exception ex) {
            this.sendError(response, ex);
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public void getObject(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.info("get");

        StoreApp storeApp = this.checkAuthorization(request, response);

        if (storeApp == null) {
            return;
        }

        String objectName = this.findObjectName(request);
        String model = storeApp.getModelCode();
        String key = objectName;
        String tenantId = "1";
        StoreDTO storeDto = storeConnector.getStore(model, key, tenantId);

        IOUtils.copy(storeDto.getDataSource().getInputStream(),
                response.getOutputStream());
    }

    @RequestMapping(method = RequestMethod.HEAD)
    public void headObject(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.info("head");

        StoreApp storeApp = this.checkAuthorization(request, response);

        if (storeApp == null) {
            return;
        }

        try {
            String objectName = this.findObjectName(request);
            String model = storeApp.getModelCode();
            String key = objectName;
            String tenantId = "1";
            StoreDTO storeDto = storeConnector.getStore(model, key, tenantId);

            BaseDTO baseDto = new BaseDTO();
            baseDto.setCode(200);
            baseDto.setData(storeDto != null);

            response.setContentType("application/json");
            response.getWriter().write(jsonMapper.toJson(baseDto));
        } catch (Exception ex) {
            this.sendError(response, ex);
        }
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public void deleteObject(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.info("delete");

        StoreApp storeApp = this.checkAuthorization(request, response);

        if (storeApp == null) {
            return;
        }

        try {
            String objectName = this.findObjectName(request);
            String model = storeApp.getModelCode();
            String key = objectName;
            String tenantId = "1";
            storeConnector.removeStore(model, key, tenantId);

            BaseDTO baseDto = new BaseDTO();
            baseDto.setCode(200);

            response.setContentType("application/json");
            response.getWriter().write(jsonMapper.toJson(baseDto));
        } catch (Exception ex) {
            this.sendError(response, ex);
        }
    }

    // ~ ======================================================================
    public String findObjectName(HttpServletRequest request) {
        // logger.info("{}", request.getRequestURL());
        logger.info("{}", request.getRequestURI());

        String prefix = request.getContextPath() + "/store/rs/";
        String objectName = request.getRequestURI().substring(prefix.length());

        logger.info("prefix : {}", prefix);
        logger.info("objectName : {}", objectName);

        return objectName;
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

    public StoreApp checkAuthorization(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        try {
            logger.info("method : {}", request.getMethod());
            logger.info("date : {}", request.getHeader("date"));
            logger.info("path : {}", request.getRequestURL());
            logger.info("path : {}", request.getRequestURI());
            logger.info(
                    "path : {}",
                    request.getRequestURI().substring(
                            request.getContextPath().length()));
            logger.info("x-request-id : {}", request.getHeader("x-request-id"));
            logger.info("content-type : {}", request.getHeader("content-type"));
            logger.info("content-md5 : {}", request.getHeader("content-md5"));

            String authorization = request.getHeader("Authorization");

            // AuthResult authResult = AuthResult
            // .parseAuthorization(authorization);
            RpcAuthResult rpcAuthResult = rpcAuthHelper.authenticateStore(
                    request, new AccessSecretHelperImpl(storeAppManager));

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

            return storeAppManager.findUniqueBy("appId",
                    rpcAuthResult.getAccessKey());
        } catch (Exception ex) {
            this.sendError(response, ex);

            return null;
        }
    }

    @Resource
    public void setStoreConnector(StoreConnector storeConnector) {
        this.storeConnector = storeConnector;
    }

    @Resource
    public void setStoreAppManager(StoreAppManager storeAppManager) {
        this.storeAppManager = storeAppManager;
    }

    @Resource
    public void setRpcAuthHelper(RpcAuthHelper rpcAuthHelper) {
        this.rpcAuthHelper = rpcAuthHelper;
    }
}
