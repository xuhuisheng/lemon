package com.mossle.api.internal;

public class MockMailConnector implements MailConnector {
    public void send(String to, String subject, String content) {
    }

    public void send(MailDTO mailDto) {
    }
}
