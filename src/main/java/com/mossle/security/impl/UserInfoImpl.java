package com.mossle.security.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mossle.security.api.UserInfo;

public class UserInfoImpl implements UserInfo {
    private String id;
    private String username;
    private String displayName;
    private String password;
    private String tenantId;
    private List<String> authorities;
    private List<String> attributes;
    private Map<String, Object> extra = new HashMap<String, Object>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }

    public Object getExtraItem(String key) {
        return extra.get(key);
    }

    public void putExtraItem(String key, Object value) {
        extra.put(key, value);
    }
}
