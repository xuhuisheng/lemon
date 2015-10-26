package com.mossle.spi.user;

public interface AccountCredentialConnector {
    String findPassword(String username, String tenantId);
}
