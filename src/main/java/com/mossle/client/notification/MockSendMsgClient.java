package com.mossle.client.notification;

import com.mossle.core.util.BaseDTO;

public class MockSendMsgClient implements SendMsgClient {
    public BaseDTO sendMsg(String from, String to, String content) {
        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);
        baseDto.setMessage("success");

        return baseDto;
    }
}
