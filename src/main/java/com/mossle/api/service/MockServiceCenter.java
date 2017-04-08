package com.mossle.api.service;

public class MockServiceCenter implements ServiceCenter {
    public <T> T findClient(Class<T> clz) {
        return null;
    }
}
