package com.mossle.spi.rpc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestClientBuilder implements InvocationHandler {
    private static Logger logger = LoggerFactory
            .getLogger(RestClientBuilder.class);
    private String baseUrl;
    private String accessKey;
    private String accessSecret;
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private RpcAuthHelper rpcAuthHelper;

    public RestClientBuilder setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;

        return this;
    }

    public RestClientBuilder setAccessKey(String accessKey) {
        this.accessKey = accessKey;

        return this;
    }

    public RestClientBuilder setAccessSecret(String accessSecret) {
        this.accessSecret = accessSecret;

        return this;
    }

    public RestClientBuilder setRpcAuthHelper(RpcAuthHelper rpcAuthHelper) {
        this.rpcAuthHelper = rpcAuthHelper;

        return this;
    }

    public <T> T build(Class<T> interfaceClass) {
        logger.debug("debug : {}", interfaceClass);

        T instance = (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class[] { interfaceClass }, this);

        return instance;
    }

    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        RestClientWorker restClientWorker = new RestClientWorker(baseUrl,
                accessKey, accessSecret, rpcAuthHelper, method, args);
        executorService.execute(restClientWorker);

        return null;
    }
}
