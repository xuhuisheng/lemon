package com.mossle.api.sendmail;

public interface SendmailConnector {
    void send(String to, String subject, String content, String tenantId);

    void send(SendmailDTO sendmailDto, String tenantId);
}
