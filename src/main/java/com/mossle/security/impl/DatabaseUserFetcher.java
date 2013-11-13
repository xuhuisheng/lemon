package com.mossle.security.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mossle.core.scope.ScopeHolder;

import com.mossle.security.api.UserFetcher;
import com.mossle.security.api.UserInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class DatabaseUserFetcher implements UserFetcher {
    private static Logger logger = LoggerFactory
            .getLogger(DatabaseUserFetcher.class);
    private Long defaultGlobalId;
    private Long defaultLocalId;
    private JdbcTemplate jdbcTemplate;

    public UserInfo getUserInfo(String username) {
        String globalCode = ScopeHolder.getGlobalCode();
        String localCode = ScopeHolder.getLocalCode();
        Long globalId = jdbcTemplate.queryForObject(
                "select id from scope_global where name=?", Long.class,
                globalCode);
        Long localId = jdbcTemplate.queryForObject(
                "select id from scope_local where name=? and global_id=?",
                Long.class, localCode, globalId);

        logger.debug("username : {}", username);
        logger.debug("globalId : {}", globalId);
        logger.debug("localId : {}", localId);

        return getUserInfo(username, globalId, localId);
    }

    public UserInfo getUserInfo(String username, Long localId) {
        logger.debug("username : {}", username);
        logger.debug("defaultGlobalId : {}", defaultGlobalId);
        logger.debug("localId : {}", localId);

        return getUserInfo(username, defaultGlobalId, localId);
    }

    public UserInfo getUserInfo(String username, Long globalId, Long localId) {
        logger.debug("username : {}", username);
        logger.debug("globalId : {}", globalId);
        logger.debug("localId : {}", localId);

        String processedUsername = null;

        if (username != null) {
            processedUsername = username.toLowerCase();
        }

        String sqlUser = "select id,username,password,status,display_name from USER_BASE"
                + " where username=? and user_repo_id=?";
        Map<String, Object> userMap = null;

        try {
            userMap = jdbcTemplate.queryForMap(sqlUser, processedUsername,
                    globalId);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new UsernameNotFoundException(processedUsername);
        }

        String sqlAuthority = "select p.name as authority"
                + " from AUTH_USER_STATUS us,AUTH_USER_ROLE ur,AUTH_ROLE r,AUTH_PERM_ROLE_DEF pr,AUTH_PERM p"
                + " where us.id=ur.user_status_id and ur.role_id=r.id and r.role_def_id=pr.role_def_id and pr.perm_id=p.id"
                + " and username=? and global_id=? and r.local_id=?";
        List<Map<String, Object>> authorityList = null;

        try {
            authorityList = jdbcTemplate.queryForList(sqlAuthority,
                    processedUsername, globalId, localId);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            authorityList = new ArrayList<Map<String, Object>>();
        }

        List<Map<String, Object>> attributeList = null;

        try {
            String sqlAttribute = "select r.name as attribute"
                    + " from AUTH_USER_STATUS us,AUTH_USER_ROLE ur,AUTH_ROLE r"
                    + " where us.id=ur.user_status_id and ur.role_id=r.id"
                    + " and username=? and global_id=? and r.local_id=?";
            attributeList = jdbcTemplate.queryForList(sqlAttribute,
                    processedUsername, globalId, localId);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            attributeList = new ArrayList<Map<String, Object>>();
        }

        List<String> authorities = new ArrayList<String>();

        for (Map<String, Object> item : authorityList) {
            authorities.add((String) item.get("authority"));
        }

        List<String> attributes = new ArrayList<String>();

        for (Map<String, Object> item : attributeList) {
            attributes.add("ROLE_" + (String) item.get("attribute"));
        }

        logger.debug("userMap : {}", userMap);
        logger.debug("authorities : {}", authorities);
        logger.debug("attributes : {}", attributes);

        UserInfoImpl userInfo = new UserInfoImpl();
        userInfo.setUsername(processedUsername);
        userInfo.setDisplayName((String) userMap.get("display_name"));
        userInfo.setPassword((String) userMap.get("password"));
        userInfo.setAuthorities(authorities);
        userInfo.setAttributes(attributes);
        userInfo.getExtra().put("userId", userMap.get("id"));

        return userInfo;
    }

    public void setDefaultGlobalId(Long defaultGlobalId) {
        this.defaultGlobalId = defaultGlobalId;
    }

    public void setDefaultLocalId(Long defaultLocalId) {
        this.defaultLocalId = defaultLocalId;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
