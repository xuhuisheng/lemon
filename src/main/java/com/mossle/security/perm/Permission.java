package com.mossle.security.perm;

public class Permission {
    private String resource;
    private String operation;

    public Permission(String text) {
        int index = text.indexOf(':');

        if (index != -1) {
            String[] array = text.split(":");
            resource = array[0];
            operation = array[1];
        } else {
            resource = text;
            operation = "*";
        }
    }

    public String getResource() {
        return resource;
    }

    public String getOperation() {
        return operation;
    }
}
