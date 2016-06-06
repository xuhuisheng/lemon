package com.mossle.internal.sendsms.support;

public class SmsServerInfo {
    private String host;
    private String appId;
    private String username;
    private String password;
    private String mobileFieldName;
    private String messageFieldName;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobileFieldName() {
        return mobileFieldName;
    }

    public void setMobileFieldName(String mobileFieldName) {
        this.mobileFieldName = mobileFieldName;
    }

    public String getMessageFieldName() {
        return messageFieldName;
    }

    public void setMessageFieldName(String messageFieldName) {
        this.messageFieldName = messageFieldName;
    }
}
