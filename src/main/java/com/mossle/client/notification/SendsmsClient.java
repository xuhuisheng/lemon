package com.mossle.client.notification;

import java.util.Map;

import com.mossle.core.util.BaseDTO;

public interface SendsmsClient {
    BaseDTO sendSms(String to, String templateCode,
            Map<String, Object> parameter);
}
