package com.mossle.security.impl;

import com.mossle.security.api.UserAuth;
import com.mossle.security.api.UserRealm;

import org.springframework.jdbc.core.JdbcTemplate;

public class DatabaseUserRealm implements UserRealm {
    private JdbcTemplate jdbcTemplate;

    public UserAuth login(String username, String password, String tenantId) {
        return null;
    }

    public UserAuth findByUsername(String username, String tenantId) {
        return null;
    }

    public UserAuth findByRef(String ref, String tenantId) {
        return null;
    }

    public UserAuth findById(String id, String tenantId) {
        return null;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
