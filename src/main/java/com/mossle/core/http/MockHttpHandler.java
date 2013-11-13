package com.mossle.core.http;

import java.io.IOException;

import java.util.Map;

public class MockHttpHandler implements HttpHandler {
    private String content;

    public String readText(String url) {
        return content;
    }

    public String readText(String url, Map<String, Object> parameterMap)
            throws IOException {
        return content;
    }

    public String readText(String url, String encoding,
            Map<String, Object> parameterMap) throws IOException {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
