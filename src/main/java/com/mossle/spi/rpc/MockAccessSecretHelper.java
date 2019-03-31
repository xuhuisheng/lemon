package com.mossle.spi.rpc;

public class MockAccessSecretHelper implements AccessSecretHelper {
    public String findAccessSecret(String accessKey) {
        return accessKey;
    }
}
