package com.mossle.user.authenticate;

public class AuthenticationParam {
    private String username;
    private String password;
    private String credentialType;
    private String param2faType;
    private String param2faValue;
    private String application;

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

    public String getCredentialType() {
        return credentialType;
    }

    public void setCredentialType(String credentialType) {
        this.credentialType = credentialType;
    }

    public String getParam2faType() {
        return param2faType;
    }

    public void setParam2faType(String param2faType) {
        this.param2faType = param2faType;
    }

    public String getParam2faValue() {
        return param2faValue;
    }

    public void setParam2faValue(String param2faValue) {
        this.param2faValue = param2faValue;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }
}
