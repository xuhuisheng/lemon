package com.mossle.internal.sendmail.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.internal.sendmail.persistence.domain.SendmailConfig;
import com.mossle.internal.sendmail.persistence.domain.SendmailTemplate;
import com.mossle.internal.sendmail.persistence.manager.SendmailConfigManager;
import com.mossle.internal.sendmail.persistence.manager.SendmailTemplateManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendmailDeployer {
    private static Logger logger = LoggerFactory
            .getLogger(SendmailDeployer.class);
    private SendmailConfigManager sendmailConfigManager;
    private SendmailTemplateManager sendmailTemplateManager;
    private String dataFilePath = "data/sendmail-config.csv";
    private String templateDataFilePath = "data/sendmail-template.csv";
    private String dataFileEncoding = "GB2312";
    private String defaultTenantId = "1";
    private boolean enable = true;
    private SendmailConfigCallback sendmailConfigCallback;
    private SendmailTemplateCallback sendmailTemplateCallback;

    public void init() {
        sendmailConfigCallback = new SendmailConfigCallback();
        sendmailConfigCallback.setSendmailConfigManager(sendmailConfigManager);
        sendmailTemplateCallback = new SendmailTemplateCallback();
        sendmailTemplateCallback
                .setSendmailTemplateManager(sendmailTemplateManager);
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
}
