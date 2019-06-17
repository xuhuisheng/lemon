package com.mossle.client.notification;

import java.util.Map;

import com.mossle.core.util.BaseDTO;

public interface SendmsgClient {
    BaseDTO sendSms(String to, String templateCode,
            Map<String, Object> parameter);
}
