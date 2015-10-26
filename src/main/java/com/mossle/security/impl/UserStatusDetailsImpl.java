package com.mossle.security.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mossle.security.spi.UserStatusDetails;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class UserStatusDetailsImpl extends User implements UserStatusDetails {
    private String id;
    private String displayName;
    private String tenantId;
    private List<String> attributes = new ArrayList<String>();
    private Map<String, Collection<String>> attributeMap = new HashMap<String, Collection<String>>();

    public UserStatusDetailsImpl(String username, String password,
            boolean enabled, Collection<GrantedAuthority> authSet) {
        super(username, password, enabled, true, true, true, authSet);
    }

    public Collection<String> getAttributes() {
        return attributes;
    }

    public Collection<String> getAttributes(String type) {
        return attributeMap.get(type);
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;

        for (String attribute : attributes) {
            String[] array = attribute.split("_");
            String type = array[0];
            String value = array[1];
            Collection<String> collection = attributeMap.get(type);

            if (collection == null) {
                collection = new ArrayList<String>();
                attributeMap.put(type, collection);
            }

            collection.add(value);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
