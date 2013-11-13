package com.mossle.security.impl;

import com.mossle.security.spi.UserStatusUpdater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = Exception.class)
public class DatabaseUserStatusUpdater implements UserStatusUpdater {
    private static Logger logger = LoggerFactory
            .getLogger(DatabaseUserStatusUpdater.class);
    private JdbcTemplate jdbcTemplate;
    private String selectSql = "select count(username) from AUTH_USER_STATUS where username=? and repo_code='0'";
    private String createSql = "insert into AUTH_USER_STATUS(username,status,repo_code) values(?,?,'0')";
    private String updateSql = "update AUTH_USER_STATUS set status=? where username=? and repo_code='0'";
    private String removeSql = "delete from AUTH_USER_STATUS where username=? and repo_code='0'";

    public void updateUser(String username, int status) {
        int count = jdbcTemplate.queryForObject(selectSql, Integer.class,
                username);

        if (count == 0) {
            logger.debug("insert : {}", username);
            jdbcTemplate.update(createSql, username, status);
        } else {
            logger.debug("update : {}", username);
            jdbcTemplate.update(updateSql, status, username);
        }
    }

    public void removeUser(String username) {
        logger.debug("remove : {}", username);
        jdbcTemplate.update(removeSql, username);
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setSelectSql(String selectSql) {
        this.selectSql = selectSql;
    }

    public void setCreateSql(String createSql) {
        this.createSql = createSql;
    }

    public void setUpdateSql(String updateSql) {
        this.updateSql = updateSql;
    }

    public void setRemoveSql(String removeSql) {
        this.removeSql = removeSql;
    }
}
