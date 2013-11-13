package com.mossle.security.impl;

import com.mossle.security.spi.UserStatusFetcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.EmptyResultDataAccessException;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class DatabaseUserStatusFetcher implements UserStatusFetcher {
    private static Logger logger = LoggerFactory
            .getLogger(DatabaseUserStatusFetcher.class);
    private JdbcTemplate jdbcTemplate;
    private String sql = "select status from AUTH_USER_STATUS where username=? and repoCode='0'";

    public int getUserStatus(String username) throws UsernameNotFoundException {
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, username);
        } catch (EmptyResultDataAccessException ex) {
            logger.info("cannot find user : [{}]", username);
            throw new UsernameNotFoundException(username, ex);
        }
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
