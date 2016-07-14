package com.mossle.core.auth;

import org.springframework.context.ApplicationEvent;

public class LogoutEvent extends ApplicationEvent {
    private String userId;
    private String sessionId;
    private String tenantId;

    public LogoutEvent(Object source, String userId, String sessionId,
            String tenantId) {
        super(source);
        this.userId = userId;
        this.sessionId = sessionId;
        this.tenantId = tenantId;
    }

    public String getUserId() {
        return userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getTenantId() {
        return tenantId;
    }
}
