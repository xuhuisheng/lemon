package com.mossle.user.support;

import javax.annotation.Resource;

import com.mossle.spi.user.AccountCredentialConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

public class DatabaseAccountCredentialConnector implements
        AccountCredentialConnector {
    private static Logger logger = LoggerFactory
            .getLogger(DatabaseAccountCredentialConnector.class);
    private JdbcTemplate jdbcTemplate;
    private String sqlFindPassword = "SELECT AC.PASSWORD AS PASSWORD"
            + " FROM ACCOUNT_CREDENTIAL AC,ACCOUNT_INFO AI"
            + " WHERE AC.ACCOUNT_ID=AI.ID AND CATALOG='default' AND AI.USERNAME=? and AI.TENANT_ID=?";

    public String findPassword(String username, String tenantId) {
        if (username == null) {
            logger.info("username is null");

            return null;
        }

        username = username.toLowerCase();

        String password = null;

        try {
            password = jdbcTemplate.queryForObject(sqlFindPassword,
                    String.class, username, tenantId);
        } catch (Exception ex) {
            logger.info(ex.getMessage());
            logger.info("cannot find password : {}, {}", username, tenantId);
        }

        return password;
    }

    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
