package com.mossle.disk.support;

public class DiskAclException extends RuntimeException {
    private Long id;
    private String type;
    private String userId;
    private int mask;
    private String action;

    public DiskAclException(Long id, String type, String userId, int mask,
            String action) {
        this.id = id;
        this.type = type;
        this.userId = userId;
        this.mask = mask;
        this.action = action;
    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getUserId() {
        return userId;
    }

    public int getMask() {
        return mask;
    }

    public String getAction() {
        return action;
    }
}
