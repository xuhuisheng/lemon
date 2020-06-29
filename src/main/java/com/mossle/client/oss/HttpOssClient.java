package com.mossle.client.oss;

import java.io.File;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.mossle.core.mapper.JsonMapper;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

public class HttpOssClient implements OssClient {
    private static Logger logger = LoggerFactory.getLogger(HttpOssClient.class);
    private JsonMapper jsonMapper = new JsonMapper();
    private String baseUrl;

    public InputStream getObject(String bucketName, String objectName)
            throws Exception {
        String url = baseUrl + "/" + bucketName + "/" + objectName;
        logger.info("url : {}", url);

        String contentType = "application/octet-stream";
        String authorization = "default:"
                + sign("GET", url, contentType, null, "default");

        HttpURLConnection conn = (HttpURLConnection) new URL(url)
                .openConnection();
        conn.setRequestMethod("GET");
        conn.addRequestProperty("Content-Type", contentType);
        conn.addRequestProperty("Authorization", authorization);

        if (conn.getResponseCode() == 200) {
            return conn.getInputStream();
        } else {
            logger.info("result error : {} {}", conn.getResponseCode(),
                    conn.getResponseMessage());

            return null;
        }
    }

    public String putObject(String bucketName, String objectName,
            InputStream inputStream) throws Exception {
        String url = baseUrl + "/" + bucketName + "/" + objectName;
        logger.info("url : {}", url);

        String contentType = "application/octet-stream";
        String authorization = "default:"
                + sign("PUT", url, contentType, null, "default");

        HttpURLConnection conn = (HttpURLConnection) new URL(url)
                .openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("PUT");
        conn.addRequestProperty("Content-Type", contentType);
        conn.addRequestProperty("Authorization", authorization);
        IOUtils.copy(inputStream, conn.getOutputStream());

        if (conn.getResponseCode() == 200) {
            String result = IOUtils.toString(conn.getInputStream());
            logger.info("result : {}", result);

            return objectName;
        } else {
            logger.info("result error : {} {}", conn.getResponseCode(),
                    conn.getResponseMessage());

            return null;
        }
    }

    public String deleteObject(String bucketName, String objectName)
            throws Exception {
        String url = baseUrl + "/" + bucketName + "/" + objectName;
        logger.info("url : {}", url);

        String contentType = "application/octet-stream";
        String authorization = "default:"
                + sign("DELETE", url, contentType, null, "default");

        HttpURLConnection conn = (HttpURLConnection) new URL(url)
                .openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("DELETE");
        conn.addRequestProperty("Content-Type", contentType);
        conn.addRequestProperty("Authorization", authorization);

        if (conn.getResponseCode() == 200) {
            String result = IOUtils.toString(conn.getInputStream());
            logger.info("result : {}", result);

            return result;
        } else {
            logger.info("result error : {} {}", conn.getResponseCode(),
                    conn.getResponseMessage());

            return null;
        }
    }

    public String generatePresignedUri(String bucketName, String objectName,
            Date expiration, String httpMethod, String contentType,
            String accessKey, String secretKey) {
        if ((objectName != null) && !objectName.isEmpty()) {
            objectName = "/" + objectName;
        } else {
            objectName = "";
        }

        String url = baseUrl + "/" + bucketName + objectName + "?AccessKey="
                + "" + "&Expires=" + expiration.getTime();

        String signature = this.sign(httpMethod, url, contentType, expiration,
                secretKey);
        url += ("&Signature=" + signature);

        return url;
    }

    public String sign(String httpMethod, String url, String contentType,
            Date expiration, String secretKey) {
        StringBuilder buff = new StringBuilder();
        buff.append(httpMethod).append("\n");

        if (StringUtils.isNotBlank(contentType)) {
            buff.append(contentType).append("\n");
        } else {
            buff.append("").append("\n");
        }

        if (expiration != null) {
            buff.append(Long.toString(expiration.getTime())).append("");
        } else {
            buff.append("").append("\n");
        }

        buff.append(url).append("\n");
        buff.append(secretKey).append("\n");

        String signature = DigestUtils.md5Hex(buff.toString());

        return signature;
    }

    @Value("${oss.ossClient.url}")
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
