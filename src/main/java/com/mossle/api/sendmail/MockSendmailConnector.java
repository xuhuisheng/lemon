package com.mossle.api.sendmail;

public class MockSendmailConnector implements SendmailConnector {
    public void send(String to, String subject, String content, String tenantId) {
    }

    public void send(SendmailDTO sendmailDto, String tenantId) {
    }
}
