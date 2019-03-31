package com.mossle.spi.rpc;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class MockRpcAuthHelper implements RpcAuthHelper {
    public RpcAuthResult authenticate(HttpServletRequest request,
            AccessSecretHelper accessSecretHelper) {
        RpcAuthResult rpcAuthResult = new RpcAuthResult();
        rpcAuthResult.setSuccess(true);
        rpcAuthResult.setAccessKey("default");

        return rpcAuthResult;
    }

    public String generateAuthorization(RequestConfig requestConfig) {
        return requestConfig.getAccessKey();
    }

    // public String calculateSignature(RequestConfig requestConfig) {
    // return requestConfig.getAccessKey();
    // }
    public RpcAuthResult authenticateStore(HttpServletRequest request,
            AccessSecretHelper accessSecretHelper) {
        RpcAuthResult rpcAuthResult = new RpcAuthResult();
        rpcAuthResult.setSuccess(true);
        rpcAuthResult.setAccessKey("default");

        return rpcAuthResult;
    }

    public String generateAuthorizationStore(RequestConfig requestConfig) {
        return requestConfig.getAccessKey();
    }
}
