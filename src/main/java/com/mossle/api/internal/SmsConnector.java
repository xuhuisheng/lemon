package com.mossle.api.internal;

public interface SmsConnector {
    void send(String to, String content);
}
