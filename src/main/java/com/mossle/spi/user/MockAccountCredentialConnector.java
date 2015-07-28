package com.mossle.spi.user;

public class MockAccountCredentialConnector implements
        AccountCredentialConnector {
    public String findPassword(String username) {
        return username;
    }
}
