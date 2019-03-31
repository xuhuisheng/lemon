package com.mossle.core.http;

import java.io.IOException;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.Collections;
import java.util.Map;

import com.mossle.core.util.IoUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpHandlerImpl implements HttpHandler {
    private static Logger logger = LoggerFactory
            .getLogger(HttpHandlerImpl.class);

    public String readText(String url) throws IOException {
        return readText(url, "UTF-8", Collections.EMPTY_MAP);
    }

    public String readText(String url, Map<String, Object> parameterMap)
            throws IOException {
        return readText(url, "UTF-8", parameterMap);
    }

    public String readText(String url, String encoding,
            Map<String, Object> parameterMap) throws IOException {
        StringBuilder buff = new StringBuilder(url);

        for (Map.Entry<String, Object> entry : parameterMap.entrySet()) {
            appendParameter(buff, entry.getKey(), entry.getValue());
        }

        HttpURLConnection conn = (HttpURLConnection) new URL(buff.toString())
                .openConnection();

        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream is = conn.getInputStream();

            return IoUtils.readString(is, encoding);
        } else {
            InputStream is = conn.getErrorStream();
            String text = IoUtils.readString(is, encoding);
            logger.error("error : {} {}", conn.getResponseCode(), text);
            throw new IllegalStateException(conn.getResponseMessage());
        }
    }

    public String doPost(String url, Map<String, Object> params)
            throws Exception {
        logger.info("post url : {} {}", url, params);

        StringBuilder buff = new StringBuilder();

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            appendParameter(buff, entry.getKey(), entry.getValue());
        }

        if (buff.length() > 0) {
            buff.deleteCharAt(0);
        }

        String parameter = buff.toString();
        logger.info("parameter : {}", parameter);

        HttpURLConnection conn = (HttpURLConnection) new URL(url)
                .openConnection();
        conn.setDoOutput(true);
        conn.getOutputStream().write(buff.toString().getBytes("UTF-8"));

        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream is = conn.getInputStream();

            return IoUtils.readString(is, "UTF-8");
        } else {
            InputStream is = conn.getErrorStream();
            String text = IoUtils.readString(is, "UTF-8");
            logger.error("error : {} {}", conn.getResponseCode(), text);
            throw new IllegalStateException(conn.getResponseMessage());
        }
    }

    private void appendParameter(StringBuilder buff, String name, Object value) {
        if ((name == null) || (value == null)) {
            return;
        }

        if (buff.indexOf("?") == -1) {
            buff.append("?");
        } else {
            buff.append("&");
        }

        buff.append(name).append("=").append(value.toString());
    }
}
