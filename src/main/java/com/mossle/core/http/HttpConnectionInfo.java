package com.mossle.core.http;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpConnectionInfo {
    private static Logger logger = LoggerFactory
            .getLogger(HttpConnectionInfo.class);
    public static final int DEFAULT_BUFFER_SIZE = 1024;
    private String url;
    private String processedMethod;
    private Map<String, String> processedQueryParams;
    private Map<String, String> processedFormParams;
    private Map<String, String> processedHeadParams;

    public HttpConnectionInfo(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public HttpConnectionResult process(String method,
            Map<String, String> queryParams, Map<String, String> formParams,
            Map<String, String> headParams) {
        this.processedMethod = method;
        this.processedQueryParams = queryParams;
        this.processedFormParams = formParams;
        this.processedHeadParams = headParams;

        HttpConnectionResult httpConnectionResult = null;

        try {
            String clientUrl = this.appendQueryParams(queryParams);
            HttpURLConnection conn = (HttpURLConnection) new URL(clientUrl)
                    .openConnection();
            conn.setRequestMethod(method);
            this.appendHeadParams(conn, headParams);

            if (!formParams.isEmpty()) {
                conn.setDoOutput(true);

                String data = constructFormParams(formParams);
                conn.getOutputStream().write(data.getBytes("UTF-8"));
            }

            InputStream is = conn.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] b = new byte[DEFAULT_BUFFER_SIZE];
            int len = 0;

            while ((len = is.read(b, 0, DEFAULT_BUFFER_SIZE)) != -1) {
                baos.write(b, 0, len);
            }

            is.close();
            baos.flush();
            baos.close();

            String content = new String(baos.toByteArray(), "UTF-8");
            httpConnectionResult = new HttpConnectionResult(true, content);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            httpConnectionResult = new HttpConnectionResult(false, null);
        }

        return httpConnectionResult;
    }

    private String appendQueryParams(Map<String, String> queryParams) {
        StringBuilder buff = new StringBuilder(url);

        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (notContains(url, key)) {
                if (buff.indexOf("?") != -1) {
                    buff.append("&");
                } else {
                    buff.append("?");
                }

                buff.append(key).append("=").append(value);
            }
        }

        return buff.toString();
    }

    private boolean notContains(String buff, String key) {
        return (buff.indexOf("?" + key + "=") == -1)
                && (buff.indexOf("&" + key + "=") == -1);
    }

    private void appendHeadParams(HttpURLConnection conn,
            Map<String, String> headParams) {
        for (Map.Entry<String, String> entry : headParams.entrySet()) {
            conn.addRequestProperty(entry.getKey(), entry.getValue());
        }
    }

    private String constructFormParams(Map<String, String> formParams) {
        StringBuilder buff = new StringBuilder();

        for (Map.Entry<String, String> entry : formParams.entrySet()) {
            buff.append("&").append(entry.getKey()).append("=")
                    .append(entry.getValue());
        }

        buff.deleteCharAt(0);

        return buff.toString();
    }

    public boolean check() {
        return process(processedMethod, processedQueryParams,
                processedFormParams, processedHeadParams).isSuccess();
    }
}
