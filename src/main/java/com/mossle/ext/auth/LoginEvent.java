package com.mossle.ext.auth;

import org.springframework.context.ApplicationEvent;

public class LoginEvent extends ApplicationEvent {
    private String userId;
    private String sessionId;

    public LoginEvent(Object source, String userId, String sessionId) {
        super(source);
        this.userId = userId;
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public String getSessionId() {
        return sessionId;
    }
}
