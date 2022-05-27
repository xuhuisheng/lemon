package com.mossle.client.notification;

import com.mossle.core.util.BaseDTO;

public interface NotificationClient {
    BaseDTO send(String requestId, String catalog, String to,
            String templateCode, String data, int priority, String config);
}
