package com.mossle.party.support;

import java.util.HashSet;
import java.util.Set;

public class ManagerInfo {
    // party struct id
    private Long id;
    private String type;
    private int priority;
    private Set<String> userIds = new HashSet<String>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Set<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(Set<String> userIds) {
        this.userIds = userIds;
    }

    //
    public boolean isMultiple() {
        return (!userIds.isEmpty());
    }

    public void addUserId(String userId) {
        userIds.add(userId);
    }

    public boolean containsUserId(String userId) {
        return userIds.contains(userId);
    }
}
