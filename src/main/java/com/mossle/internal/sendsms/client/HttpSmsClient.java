package com.mossle.internal.sendsms.client;

import java.io.*;

import java.net.*;

import java.util.*;

import com.mossle.core.mapper.JsonMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

public class HttpSmsClient implements SmsClient {
    private static Logger logger = LoggerFactory.getLogger(HttpSmsClient.class);
    private String baseUrl;
    private String configCode;
    private JsonMapper jsonMapper = new JsonMapper();

    public void sendSms(String mobile, String message) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(baseUrl)
                .openConnection();
        conn.setDoOutput(true);
        conn.setDoInput(true);

        String queryString = "mobile=" + mobile + "&message=" + message
                + "&configCode=" + configCode;
        conn.getOutputStream().write(queryString.getBytes("UTF-8"));
        conn.getOutputStream().flush();

        InputStream is = conn.getInputStream();
        int len = -1;
        byte[] b = new byte[1024];

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        while ((len = is.read(b, 0, 1024)) != -1) {
            baos.write(b, 0, len);
        }

        is.close();

        String text = new String(baos.toByteArray(), "UTF-8");
        logger.info("sms result : {}", text);
    }

    @Value("${sms.baseUrl}")
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Value("${sms.configCode}")
    public void setConfigCode(String configCode) {
        this.configCode = configCode;
    }
}
