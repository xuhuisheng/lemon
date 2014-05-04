package com.mossle.security.impl;

import com.mossle.security.api.UserAuth;
import com.mossle.security.api.UserRealm;

import org.springframework.jdbc.core.JdbcTemplate;

public class DatabaseUserRealm implements UserRealm {
    private JdbcTemplate jdbcTemplate;

    public UserAuth login(String username, String password, String scopeId) {
        return null;
    }

    public UserAuth findByUsername(String username, String scopeId) {
        return null;
    }

    public UserAuth findByRef(String ref, String scopeId) {
        return null;
    }

    public UserAuth findById(String id, String scopeId) {
        return null;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
