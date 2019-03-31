package com.mossle.internal.sendmail.service;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.client.store.StoreClient;

import com.mossle.core.mail.MailHelper;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.template.TemplateService;
import com.mossle.core.util.BaseDTO;

import com.mossle.internal.sendmail.persistence.domain.SendmailApp;
import com.mossle.internal.sendmail.persistence.domain.SendmailConfig;
import com.mossle.internal.sendmail.persistence.domain.SendmailQueue;
import com.mossle.internal.sendmail.persistence.domain.SendmailTemplate;
import com.mossle.internal.sendmail.persistence.manager.SendmailAppManager;
import com.mossle.internal.sendmail.persistence.manager.SendmailConfigManager;
import com.mossle.internal.sendmail.persistence.manager.SendmailHistoryManager;
import com.mossle.internal.sendmail.persistence.manager.SendmailQueueManager;
import com.mossle.internal.sendmail.persistence.manager.SendmailTemplateManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SendmailService {
    private static Logger logger = LoggerFactory
            .getLogger(SendmailService.class);
    private SendmailConfigManager sendmailConfigManager;
    private SendmailQueueManager sendmailQueueManager;
    private SendmailHistoryManager sendmailHistoryManager;
    private SendmailTemplateManager sendmailTemplateManager;
    private StoreClient storeClient;
    private BeanMapper beanMapper = new BeanMapper();
    private MailHelper mailHelper;
    private TemplateService templateService;
    private JsonMapper jsonMapper = new JsonMapper();
    private SendmailAppManager sendmailAppManager;
    private String defaultTenantId = "1";

    public BaseDTO send(String to, String templateCode,
            Map<String, Object> parameter, String appId) {
        BaseDTO baseDto = new BaseDTO();

        try {
            SendmailApp sendmailApp = sendmailAppManager.findUniqueBy("appId",
                    appId);

            if (sendmailApp == null) {
                logger.info("cannot find app : {}", appId);
                baseDto.setCode(404);

                return baseDto;
            }

            String configCode = sendmailApp.getConfigCode();
            String data = jsonMapper.toJson(parameter);

            SendmailConfig sendmailConfig = sendmailConfigManager.findUnique(
                    "from SendmailConfig where name=? and tenantId=?",
                    configCode, defaultTenantId);
            SendmailTemplate sendmailTemplate = sendmailTemplateManager
                    .findUnique(
                            "from SendmailTemplate where name=? and tenantId=?",
                            templateCode, defaultTenantId);
            SendmailQueue sendmailQueue = new SendmailQueue();
            sendmailQueue.setReceiver(to);
            sendmailQueue.setData(data);
            sendmailQueue.setSendmailTemplate(sendmailTemplate);
            sendmailQueue.setSendmailConfig(sendmailConfig);
            sendmailQueue.setSendmailApp(sendmailApp);
            sendmailQueue.setTenantId(defaultTenantId);
            sendmailQueue.setCreateTime(new Date());
            sendmailQueueManager.save(sendmailQueue);
            baseDto.setCode(200);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            baseDto.setCode(500);
            baseDto.setMessage(ex.getMessage());
        }

        return baseDto;
    }

    public String findAccessSecret(String accessKey) {
        SendmailApp sendmailApp = sendmailAppManager.findUniqueBy("appId",
                accessKey);

        if (sendmailApp == null) {
            return null;
        }

        return sendmailApp.getAppKey();
    }

    // ~
    @Resource
    public void setSendmailConfigManager(
            SendmailConfigManager sendmailConfigManager) {
        this.sendmailConfigManager = sendmailConfigManager;
    }

    @Resource
    public void setSendmailQueueManager(
            SendmailQueueManager sendmailQueueManager) {
        this.sendmailQueueManager = sendmailQueueManager;
    }

    @Resource
    public void setSendmailHistoryManager(
            SendmailHistoryManager sendmailHistoryManager) {
        this.sendmailHistoryManager = sendmailHistoryManager;
    }

    @Resource
    public void setSendmailTemplateManager(
            SendmailTemplateManager sendmailTemplateManager) {
        this.sendmailTemplateManager = sendmailTemplateManager;
    }

    @Resource
    public void setStoreClient(StoreClient storeClient) {
        this.storeClient = storeClient;
    }

    @Resource
    public void setMailHelper(MailHelper mailHelper) {
        this.mailHelper = mailHelper;
    }

    @Resource
    public void setTemplateService(TemplateService templateService) {
        this.templateService = templateService;
    }

    @Resource
    public void setSendmailAppManager(SendmailAppManager sendmailAppManager) {
        this.sendmailAppManager = sendmailAppManager;
    }
}
