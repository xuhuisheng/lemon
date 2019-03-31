package com.mossle.client.key;

import com.mossle.core.util.BaseDTO;

public class MockKeyClient implements KeyClient {
    public BaseDTO encrypt(String text) {
        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);
        baseDto.setMessage("success");
        baseDto.setData(text);

        return baseDto;
    }

    public BaseDTO decrypt(String text) {
        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);
        baseDto.setMessage("success");
        baseDto.setData(text);

        return baseDto;
    }
}
