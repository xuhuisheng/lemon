package com.mossle.user.authenticate;

import org.apache.commons.lang3.StringUtils;

public class SimpleStrategy {
    public void validateParam(AuthenticationParam param) {
        if (param == null) {
            throw new RuntimeException("param required");
        }

        if (StringUtils.isBlank(param.getUsername())) {
            throw new RuntimeException("username required");
        }

        if (StringUtils.isBlank(param.getPassword())) {
            throw new RuntimeException("password required");
        }
    }

    public String normalizeUsername(AuthenticationParam param) {
        String username = param.getUsername();

        return username.trim().toLowerCase();
    }
}
