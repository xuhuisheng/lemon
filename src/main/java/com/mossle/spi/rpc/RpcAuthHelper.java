package com.mossle.spi.rpc;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface RpcAuthHelper {
    RpcAuthResult authenticate(HttpServletRequest request,
            AccessSecretHelper accessSecretHelper);

    String generateAuthorization(RequestConfig requestConfig);

    // String calculateSignature(RequestConfig requestConfig);
    RpcAuthResult authenticateStore(HttpServletRequest request,
            AccessSecretHelper accessSecretHelper);

    String generateAuthorizationStore(RequestConfig requestConfig);

    String generatePresignedUri(String bucketName, String objectName,
            Date expiration, String httpMethod, String contentType,
            String accessKey, String secretKey);
}
