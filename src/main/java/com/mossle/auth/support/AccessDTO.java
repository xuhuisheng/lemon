package com.mossle.auth.support;

public class AccessDTO {
    private Long id;
    private String value;
    private String perm;

    public AccessDTO() {
    }

    public AccessDTO(String value, String perm) {
        this.value = value;
        this.perm = perm;
    }

    public AccessDTO(Long id, String value, String perm) {
        this.id = id;
        this.value = value;
        this.perm = perm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPerm() {
        return perm;
    }

    public void setPerm(String perm) {
        this.perm = perm;
    }
}
