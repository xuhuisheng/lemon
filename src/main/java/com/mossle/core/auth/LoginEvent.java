package com.mossle.core.auth;

import org.springframework.context.ApplicationEvent;

public class LoginEvent extends ApplicationEvent {
    private String userId;
    private String sessionId;
    private String result;
    private String type;
    private String tenantId;

    public LoginEvent(Object source, String userId, String sessionId) {
        this(source, userId, sessionId, "success", "default", "1");
    }

    public LoginEvent(Object source, String userId, String sessionId,
            String result) {
        this(source, userId, sessionId, result, "default", "1");
    }

    public LoginEvent(Object source, String userId, String sessionId,
            String result, String type, String tenantId) {
        super(source);
        this.userId = userId;
        this.sessionId = sessionId;
        this.result = result;
        this.type = type;
        this.tenantId = tenantId;
    }

    public String getUserId() {
        return userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getResult() {
        return result;
    }

    public String getType() {
        return type;
    }

    public String getTenantId() {
        return tenantId;
    }
}
