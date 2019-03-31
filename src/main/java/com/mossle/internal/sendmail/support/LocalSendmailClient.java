package com.mossle.internal.sendmail.support;

import java.util.Map;

import javax.annotation.Resource;

import com.mossle.client.notification.SendmailClient;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.util.BaseDTO;

import com.mossle.internal.sendmail.persistence.manager.SendmailAppManager;
import com.mossle.internal.sendmail.service.SendmailDataService;
import com.mossle.internal.sendmail.service.SendmailService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalSendmailClient implements SendmailClient {
    private static Logger logger = LoggerFactory
            .getLogger(LocalSendmailClient.class);
    private SendmailDataService sendmailDataService;
    private String tenantId = "1";
    private JsonMapper jsonMapper = new JsonMapper();
    private String appId = "lemon";
    private SendmailAppManager sendmailAppManager;
    private SendmailService sendmailService;

    public BaseDTO sendMail(String to, String templateCode,
            Map<String, Object> parameter) {
        BaseDTO baseDto = new BaseDTO();

        try {
            baseDto = sendmailService.send(to, templateCode, parameter, appId);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            baseDto.setCode(500);
            baseDto.setMessage(ex.getMessage());
        }

        return baseDto;
    }

    @Resource
    public void setSendmailDataService(SendmailDataService sendmailDataService) {
        this.sendmailDataService = sendmailDataService;
    }

    @Resource
    public void setSendmailAppManager(SendmailAppManager sendmailAppManager) {
        this.sendmailAppManager = sendmailAppManager;
    }

    @Resource
    public void setSendmailService(SendmailService sendmailService) {
        this.sendmailService = sendmailService;
    }
}
