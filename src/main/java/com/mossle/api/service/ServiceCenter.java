package com.mossle.api.service;

public interface ServiceCenter {
    <T> T findClient(Class<T> clz);
}
