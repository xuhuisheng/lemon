package com.mossle.core.http;

import java.io.IOException;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.Collections;
import java.util.Map;

import com.mossle.core.util.IoUtils;

public class HttpHandlerImpl implements HttpHandler {
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
        InputStream is = conn.getInputStream();

        return IoUtils.readString(is, encoding);
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
