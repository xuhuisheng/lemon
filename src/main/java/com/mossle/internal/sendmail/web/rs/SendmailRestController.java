package com.mossle.internal.sendmail.web.rs;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;

import com.mossle.client.notification.SendmailClient;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.util.BaseDTO;

import com.mossle.internal.sendmail.persistence.manager.SendmailAppManager;
import com.mossle.internal.sendmail.service.SendmailService;

import com.mossle.spi.rpc.RpcAuthHelper;
import com.mossle.spi.rpc.RpcAuthResult;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("sendmail/rs")
public class SendmailRestController {
    private static Logger logger = LoggerFactory
            .getLogger(SendmailRestController.class);
    private SendmailService sendmailService;
    private RpcAuthHelper rpcAuthHelper;
    private SendmailClient sendmailClient;
    private SendmailAppManager sendmailAppManager;
    private JsonMapper jsonMapper = new JsonMapper();

    @RequestMapping("send")
    public BaseDTO sendMail(@RequestParam("to") String to,
            @RequestParam("templateCode") String templateCode,
            @RequestParam("data") String data, @RequestBody String requestBody,
            HttpServletRequest request) throws Exception {
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

        BaseDTO baseDto = new BaseDTO();

        try {
            // String payload = IOUtils.toString(request.getInputStream(),
            // request.getCharacterEncoding());
            // logger.info("payload : {}", payload);
            // String t = IOUtils.toString(is, "UTF-8");
            logger.info("requestBody : {}", requestBody);

            String authorization = request.getHeader("Authorization");

            // AuthResult authResult = AuthResult
            // .parseAuthorization(authorization);
            RpcAuthResult rpcAuthResult = rpcAuthHelper.authenticate(request,
                    new AccessSecretHelperImpl(sendmailAppManager));

            if (!rpcAuthResult.isSuccess()) {
                logger.warn("access denied : {}", authorization);
                baseDto.setCode(403);
                baseDto.setMessage("access denied");

                return baseDto;
            }

            // String accessSecret = sendmailService.findAccessSecret(authResult
            // .getAccessKey());
            String contentMd5 = request.getHeader("Content-MD5");

            // authResult.processSignature(accessSecret, payload);

            // if (!authResult.isSuccess()) {
            // logger.warn("access denied : {}", authorization);
            // baseDto.setCode(403);
            // baseDto.setMessage("access denied");
            // return baseDto;
            // }
            Map<String, Object> parameter = jsonMapper
                    .fromJson(data, Map.class);
            baseDto = sendmailService.send(to, templateCode, parameter,
                    rpcAuthResult.getAccessKey());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            baseDto.setCode(500);
            baseDto.setMessage(ex.getMessage());
        }

        return baseDto;
    }

    @RequestMapping("test")
    public void test() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", "name");
        map.put("list", Collections.singletonList("item"));

        String to = "lingo@mossle.com";
        String templateCode = "template";
        String date = jsonMapper.toJson(map);

        BaseDTO result = sendmailClient.sendMail(to, templateCode, map);
    }

    @Resource
    public void setSendmailService(SendmailService sendmailService) {
        this.sendmailService = sendmailService;
    }

    @Resource
    public void setRpcAuthHelper(RpcAuthHelper rpcAuthHelper) {
        this.rpcAuthHelper = rpcAuthHelper;
    }

    @Resource
    public void setSendmailClient(SendmailClient sendmailClient) {
        this.sendmailClient = sendmailClient;
    }

    @Resource
    public void setSendmailAppManager(SendmailAppManager sendmailAppManager) {
        this.sendmailAppManager = sendmailAppManager;
    }
}
