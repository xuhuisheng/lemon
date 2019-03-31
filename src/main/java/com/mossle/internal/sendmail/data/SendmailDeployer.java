package com.mossle.internal.sendmail.data;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.core.csv.CsvProcessor;

import com.mossle.internal.sendmail.persistence.manager.SendmailAppManager;
import com.mossle.internal.sendmail.persistence.manager.SendmailConfigManager;
import com.mossle.internal.sendmail.persistence.manager.SendmailTemplateManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendmailDeployer {
    private static Logger logger = LoggerFactory
            .getLogger(SendmailDeployer.class);
    private SendmailConfigManager sendmailConfigManager;
    private SendmailTemplateManager sendmailTemplateManager;
    private SendmailAppManager sendmailAppManager;
    private String dataFilePath = "data/sendmail-config.csv";
    private String templateDataFilePath = "data/sendmail-template.csv";
    private String appDataFilePath = "data/sendmail-app.csv";
    private String dataFileEncoding = "GB2312";
    private String defaultTenantId = "1";
    private boolean enable = true;
    private SendmailConfigCallback sendmailConfigCallback;
    private SendmailTemplateCallback sendmailTemplateCallback;
    private SendmailAppCallback sendmailAppCallback;

    public void init() {
        sendmailConfigCallback = new SendmailConfigCallback();
        sendmailConfigCallback.setSendmailConfigManager(sendmailConfigManager);
        sendmailTemplateCallback = new SendmailTemplateCallback();
        sendmailTemplateCallback
                .setSendmailTemplateManager(sendmailTemplateManager);
        sendmailAppCallback = new SendmailAppCallback();
        sendmailAppCallback.setSendmailAppManager(sendmailAppManager);
    }

    @PostConstruct
    public void process() throws Exception {
        if (!enable) {
            logger.info("skip init {}", SendmailDeployer.class);

            return;
        }

        this.init();

        new CsvProcessor().process(dataFilePath, dataFileEncoding,
                sendmailConfigCallback);

        new CsvProcessor().process(templateDataFilePath, dataFileEncoding,
                sendmailTemplateCallback);

        new CsvProcessor().process(appDataFilePath, dataFileEncoding,
                sendmailAppCallback);
    }

    @Resource
    public void setSendmailConfigManager(
            SendmailConfigManager sendmailConfigManager) {
        this.sendmailConfigManager = sendmailConfigManager;
    }

    @Resource
    public void setSendmailTemplateManager(
            SendmailTemplateManager sendmailTemplateManager) {
        this.sendmailTemplateManager = sendmailTemplateManager;
    }

    @Resource
    public void setSendmailAppManager(SendmailAppManager sendmailAppManager) {
        this.sendmailAppManager = sendmailAppManager;
    }
}
