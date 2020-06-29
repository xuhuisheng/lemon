package com.mossle.client.oss;

import java.io.InputStream;

import java.util.Date;

public interface OssClient {
    InputStream getObject(String bucketName, String objectName)
            throws Exception;

    String putObject(String bucketName, String objectName,
            InputStream inputStream) throws Exception;

    String deleteObject(String bucketName, String objectName) throws Exception;

    String generatePresignedUri(String bucketName, String objectName,
            Date expiration, String httpMethod, String contentType,
            String accessKey, String secretKey);
}
