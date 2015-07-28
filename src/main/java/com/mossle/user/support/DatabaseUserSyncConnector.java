package com.mossle.user.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.user.UserDTO;
import com.mossle.api.user.UserSyncConnector;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.hibernate.PropertyFilterUtils;
import com.mossle.core.page.Page;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.EmptyResultDataAccessException;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.util.Assert;

public class DatabaseUserSyncConnector implements UserSyncConnector {
    private static Logger logger = LoggerFactory
            .getLogger(DatabaseUserSyncConnector.class);
    private JdbcTemplate jdbcTemplate;
    private String sqlFindByCode = "select count(*) from ACCOUNT_INFO where code=?";
    private String sqlUpdate = "update ACCOUNT_INFO set username=?,nick_name=?,display_name=?,status='active' where code=?";
    private String sqlInsert = "insert into ACCOUNT_INFO(code,username,nick_name,display_name,status) values(?,?,?,?,'active')";

    @Transactional
    public void updateUser(UserDTO userDto) {
        Assert.notNull(userDto, "userDto should not be null");

        String code = userDto.getRef();
        int count = jdbcTemplate.queryForObject(sqlFindByCode, Integer.class,
                code);

        if (count > 0) {
            jdbcTemplate.update(sqlUpdate,
                    new Object[] { userDto.getUsername(),
                            userDto.getNickName(), userDto.getDisplayName(),
                            code });
        } else {
            jdbcTemplate.update(
                    sqlInsert,
                    new Object[] { code, userDto.getUsername(),
                            userDto.getNickName(), userDto.getDisplayName() });
        }
    }

    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
