package com.mossle.internal.mail.client;

import java.util.Map;

public interface MailClient {
    void sendMail(String to, String templateCode, Map<String, Object> parameters)
            throws Exception;
}
