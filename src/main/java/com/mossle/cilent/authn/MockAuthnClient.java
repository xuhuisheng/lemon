package com.mossle.client.authn;

import com.mossle.api.user.AccountStatus;

import com.mossle.core.util.BaseDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockAuthnClient implements AuthnClient {
    private static Logger logger = LoggerFactory
            .getLogger(MockAuthnClient.class);

    public BaseDTO authenticate(String username, String password,
            String tenantId) {
        logger.info("process : {}", username);

        String processedUsername = this.processUsername(username);
        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);
        baseDto.setMessage("success");
        baseDto.setData(processedUsername);

        return baseDto;
    }

    public String processUsername(String username) {
        if (username == null) {
            throw new IllegalStateException("username cannot be null");
        }

        return username.trim().toLowerCase();
    }
}
