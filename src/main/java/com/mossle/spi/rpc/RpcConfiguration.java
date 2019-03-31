package com.mossle.spi.rpc;

public class RpcConfiguration {
    private String baseUrl;
    private String accessKey;
    private String accessSecret;
    private RpcAuthHelper rpcAuthHelper;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getAccessSecret() {
        return accessSecret;
    }

    public void setAccessSecret(String accessSecret) {
        this.accessSecret = accessSecret;
    }

    public RpcAuthHelper getRpcAuthHelper() {
        return rpcAuthHelper;
    }

    public void setRpcAuthHelper(RpcAuthHelper rpcAuthHelper) {
        this.rpcAuthHelper = rpcAuthHelper;
    }
}
