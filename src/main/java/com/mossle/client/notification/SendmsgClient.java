package com.mossle.client.notification;

import com.mossle.core.util.BaseDTO;

public interface SendMsgClient {
    BaseDTO sendMsg(String from, String to, String content);
}
