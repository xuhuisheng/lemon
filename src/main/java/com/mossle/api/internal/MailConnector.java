package com.mossle.api.internal;

public interface MailConnector {
    void send(String to, String subject, String content);

    void send(MailDTO mailDto);
}
