package com.mossle.internal.mail.client;

import java.io.*;

import java.net.*;

import java.util.*;

import com.mossle.core.mapper.JsonMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

public class HttpMailClient implements MailClient {
    private static Logger logger = LoggerFactory
            .getLogger(HttpMailClient.class);
    private String baseUrl;
    private String configCode;
    private JsonMapper jsonMapper = new JsonMapper();

    public void sendMail(String to, String templateCode,
            Map<String, Object> parameter) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(baseUrl)
                .openConnection();
        conn.setDoOutput(true);
        conn.setDoInput(true);

        String queryString = "to=" + to + "&configCode=" + configCode
                + "&templateCode=" + templateCode + "&data="
                + jsonMapper.toJson(parameter);
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
        logger.info("mail result : {}", text);
    }

    @Value("${mail.baseUrl}")
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Value("${mail.configCode}")
    public void setConfigCode(String configCode) {
        this.configCode = configCode;
    }
}
