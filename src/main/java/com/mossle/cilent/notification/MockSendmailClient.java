package com.mossle.client.notification;

import java.util.Map;

import com.mossle.core.util.BaseDTO;

public class MockSendmailClient implements SendmailClient {
    public BaseDTO sendMail(String to, String templateCode,
            Map<String, Object> parameter) {
        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);
        baseDto.setMessage("success");

        return baseDto;
    }
}
