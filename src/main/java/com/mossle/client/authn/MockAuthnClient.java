package com.mossle.client.authn;

import com.mossle.api.user.AccountStatus;

import com.mossle.spi.device.DeviceDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockAuthnClient implements AuthnClient {
    private static Logger logger = LoggerFactory
            .getLogger(MockAuthnClient.class);

    public String authenticate(String username, String password, String tenantId) {
        logger.info("process : {}", username);

        String processedUsername = this.processUsername(username);

        return AccountStatus.SUCCESS;
    }

    public String processUsername(String username) {
        if (username == null) {
            throw new IllegalStateException("username cannot be null");
        }

        return username.trim().toLowerCase();
    }

    public DeviceDTO findDevice(String code) {
        return null;
    }

    public void saveDevice(DeviceDTO deviceDto) {
    }
}
