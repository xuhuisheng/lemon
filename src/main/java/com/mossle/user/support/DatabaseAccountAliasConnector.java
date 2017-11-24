package com.mossle.user.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.query.PropertyFilterUtils;

import com.mossle.spi.user.AccountAliasConnector;

import com.mossle.user.persistence.domain.AccountAlias;
import com.mossle.user.persistence.domain.AccountInfo;
import com.mossle.user.persistence.manager.AccountAliasManager;
import com.mossle.user.persistence.manager.AccountInfoManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.EmptyResultDataAccessException;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.util.Assert;

public class DatabaseAccountAliasConnector implements AccountAliasConnector {
    private static Logger logger = LoggerFactory
            .getLogger(DatabaseAccountAliasConnector.class);
    private JdbcTemplate jdbcTemplate;
    private Map<String, String> aliasMap = new HashMap<String, String>();
    private String sql = "SELECT ai.USERNAME FROM ACCOUNT_ALIAS aa, ACCOUNT_INFO ai WHERE aa.ACCOUNT_ID=ai.ID WHERE aa.NAME=?";
    private AccountAliasManager accountAliasManager;
    private AccountInfoManager accountInfoManager;

    public String findUsernameByAlias(String alias) {
        Assert.hasText(alias, "alias should not be null");

        try {
            String username = jdbcTemplate.queryForObject(sql, String.class,
                    alias);

            return username;
        } catch (EmptyResultDataAccessException ex) {
            logger.debug(ex.getMessage(), ex);
            logger.info("user[{}] is not exists.", alias);

            return alias.trim().toLowerCase();
        }
    }

    public void updateAlias(String username, String type, String alias) {
        AccountInfo accountInfo = accountInfoManager.findUniqueBy("username",
                username);

        if (accountInfo == null) {
            return;
        }

        AccountAlias accountAlias = accountAliasManager.findUnique(
                "from AccountAlias where type=? and accountInfo=?", type,
                accountInfo);

        if (accountAlias == null) {
            accountAlias = new AccountAlias();
            accountAlias.setAccountInfo(accountInfo);
            accountAlias.setType(type);
            accountAlias.setName(alias);
        }

        accountAlias.setName(alias);
        accountAliasManager.save(accountAlias);
    }

    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Resource
    public void setAccountInfoManager(AccountInfoManager accountInfoManager) {
        this.accountInfoManager = accountInfoManager;
    }

    @Resource
    public void setAccountAliasManager(AccountAliasManager accountAliasManager) {
        this.accountAliasManager = accountAliasManager;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
