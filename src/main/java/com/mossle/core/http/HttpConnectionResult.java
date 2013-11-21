package com.mossle.core.http;

public class HttpConnectionResult {
    private boolean success;
    private String content;

    public HttpConnectionResult(boolean success, String content) {
        this.success = success;
        this.content = content;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getContent() {
        return content;
    }
}
