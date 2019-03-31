package com.mossle.client.notification;

import java.util.Map;

import com.mossle.core.util.BaseDTO;

public class MockSendsmsClient implements SendsmsClient {
    public BaseDTO sendSms(String to, String templateCode,
            Map<String, Object> parameter) {
        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);
        baseDto.setMessage("success");

        return baseDto;
    }
}
