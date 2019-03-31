package com.mossle.internal.sendmail.data;

import java.util.List;

import com.mossle.core.csv.CsvCallback;

import com.mossle.internal.sendmail.persistence.domain.SendmailTemplate;
import com.mossle.internal.sendmail.persistence.manager.SendmailTemplateManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendmailTemplateCallback implements CsvCallback {
    private static Logger logger = LoggerFactory
            .getLogger(SendmailTemplateCallback.class);
    private SendmailTemplateManager sendmailTemplateManager;
    private String defaultTenantId = "1";

    public void process(List<String> list, int lineNo) throws Exception {
        String name = list.get(0);
        String sender = list.get(1);
        String receiver = list.get(2);
        String subject = list.get(3);
        String content = list.get(4);
        String manual = list.get(5);

        name = name.toLowerCase();

        SendmailTemplate sendmailTemplate = sendmailTemplateManager
                .findUniqueBy("name", name);

        if (sendmailTemplate != null) {
            return;
        }

        sendmailTemplate = new SendmailTemplate();
        sendmailTemplate.setName(name);
        sendmailTemplate.setSender(sender);
        sendmailTemplate.setReceiver(receiver);
        sendmailTemplate.setSubject(subject);
        sendmailTemplate.setContent(content);
        sendmailTemplate.setManual("manual".equals(manual) ? 0 : 1);
        sendmailTemplate.setTenantId(defaultTenantId);
        sendmailTemplateManager.save(sendmailTemplate);
    }

    public void setSendmailTemplateManager(
            SendmailTemplateManager sendmailTemplateManager) {
        this.sendmailTemplateManager = sendmailTemplateManager;
    }
}
