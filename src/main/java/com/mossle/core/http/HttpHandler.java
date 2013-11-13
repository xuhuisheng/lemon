package com.mossle.core.http;

import java.io.IOException;

import java.util.Map;

public interface HttpHandler {
    String readText(String url) throws IOException;

    String readText(String url, Map<String, Object> params) throws IOException;

    String readText(String url, String encoding, Map<String, Object> params)
            throws IOException;
}
