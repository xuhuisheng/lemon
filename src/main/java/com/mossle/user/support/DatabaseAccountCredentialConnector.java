package com.mossle.user.support;

import javax.annotation.Resource;

import com.mossle.api.userauth.UserAuthConnector;
import com.mossle.api.userauth.UserAuthDTO;

import com.mossle.spi.user.AccountCredentialConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.util.Assert;

public class DatabaseAccountCredentialConnector implements
        AccountCredentialConnector {
    private static Logger logger = LoggerFactory
            .getLogger(DatabaseAccountCredentialConnector.class);
    private JdbcTemplate jdbcTemplate;
    private String sqlFindPassword = "select ac.password as password"
            + " from ACCOUNT_CREDENTIAL ac,ACCOUNT_INFO ai"
            + " where ac.account_id=ai.id and catalog='default' and ai.username=?";

    public String findPassword(String username) {
        String password = jdbcTemplate.queryForObject(sqlFindPassword,
                String.class, username);

        return password;
    }

    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
