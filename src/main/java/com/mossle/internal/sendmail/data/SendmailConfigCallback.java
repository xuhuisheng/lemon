package com.mossle.internal.sendmail.data;

import java.util.List;

import com.mossle.core.csv.CsvCallback;

import com.mossle.internal.sendmail.persistence.domain.SendmailConfig;
import com.mossle.internal.sendmail.persistence.manager.SendmailConfigManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendmailConfigCallback implements CsvCallback {
    private static Logger logger = LoggerFactory
            .getLogger(SendmailConfigCallback.class);
    private SendmailConfigManager sendmailConfigManager;
    private String defaultTenantId = "1";

    public void process(List<String> list, int lineNo) throws Exception {
        String name = list.get(0);
        String host = list.get(1);
        String port = list.get(2);
        String username = list.get(3);
        String password = list.get(4);
        String smtpAuth = list.get(5);
        String smtpStarttls = list.get(6);
        String smtpSsl = list.get(7);
        String status = list.get(8);
        String defaultFrom = list.get(9);

        name = name.toLowerCase();

        SendmailConfig sendmailConfig = sendmailConfigManager.findUniqueBy(
                "name", name);

        if (sendmailConfig != null) {
            return;
        }

        sendmailConfig = new SendmailConfig();
        sendmailConfig.setName(name);
        sendmailConfig.setHost(host);
        sendmailConfig.setPort(Integer.parseInt(port));
        sendmailConfig.setUsername(username);
        sendmailConfig.setPassword(password);
        sendmailConfig.setSmtpAuth("true".equals(smtpAuth) ? 1 : 0);
        sendmailConfig.setSmtpStarttls("true".equals(smtpStarttls) ? 1 : 0);
        sendmailConfig.setSmtpSsl("true".equals(smtpSsl) ? 1 : 0);
        sendmailConfig.setStatus(status);
        sendmailConfig.setDefaultFrom(defaultFrom);
        sendmailConfig.setTenantId(defaultTenantId);
        sendmailConfigManager.save(sendmailConfig);
    }

    public void setSendmailConfigManager(
            SendmailConfigManager sendmailConfigManager) {
        this.sendmailConfigManager = sendmailConfigManager;
    }
}
