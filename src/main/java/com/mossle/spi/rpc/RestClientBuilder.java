package com.mossle.spi.rpc;

import java.io.InputStream;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.util.BaseDTO;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestClientBuilder implements InvocationHandler {
    private static Logger logger = LoggerFactory
            .getLogger(RestClientBuilder.class);
    private JsonMapper jsonMapper = new JsonMapper();
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
