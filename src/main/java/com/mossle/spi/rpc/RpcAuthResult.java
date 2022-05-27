package com.mossle.spi.rpc;

public class RpcAuthResult {
    private boolean success;
    private String accessKey;
    private String expire;
    private String accessSignature;
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getExpire() {
        return expire;
    }

    public void setExpire(String expire) {
        this.expire = expire;
    }

    public String getAccessSignature() {
        return accessSignature;
    }

    public void setAccessSignature(String accessSignature) {
        this.accessSignature = accessSignature;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
