package com.mossle.security.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.security.api.UserFetcher;
import com.mossle.security.api.UserInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class DatabaseUserFetcher implements UserFetcher {
    private static Logger logger = LoggerFactory
            .getLogger(DatabaseUserFetcher.class);
    private String defaultUserRepoRef;
    private JdbcTemplate jdbcTemplate;
    private TenantHolder tenantHolder;

    public UserInfo getUserInfo(String username) {
        return getUserInfo(username, tenantHolder.getUserRepoRef(),
                tenantHolder.getTenantId());
    }

    public UserInfo getUserInfo(String username, String tenantId) {
        return getUserInfo(username, defaultUserRepoRef, tenantId);
    }

    public UserInfo getUserInfo(String username, String userRepoRef,
            String tenantId) {
        logger.debug("username : {}", username);
        logger.debug("userRepoRef : {}", userRepoRef);
        logger.debug("tenantId : {}", tenantId);

        String processedUsername = null;

        if (username != null) {
            processedUsername = username.toLowerCase();
        }

        Map<String, Object> userMap = this.fetchUserMap(processedUsername,
                userRepoRef, tenantId);
        List<Map<String, Object>> authorityList = this.fetchAuthoritieList(
                processedUsername, userRepoRef, tenantId);

        List<Map<String, Object>> attributeList = this.fetchAttributeList(
                processedUsername, userRepoRef, tenantId);

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
        userInfo.setId(userMap.get("id").toString());
        userInfo.setUsername(processedUsername);
        userInfo.setDisplayName((String) userMap.get("display_name"));
        userInfo.setPassword((String) userMap.get("password"));
        userInfo.setTenantId(tenantId);
        userInfo.setAuthorities(authorities);
        userInfo.setAttributes(attributes);

        return userInfo;
    }

    public Map<String, Object> fetchUserMap(String username,
            String userRepoRef, String tenantId) {
        String sqlUser = "select id,username,password,status,display_name from USER_BASE"
                + " where username=? and user_repo_id=?";

        try {
            Map<String, Object> userMap = null;

            userMap = jdbcTemplate.queryForMap(sqlUser, username, userRepoRef);

            return userMap;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new UsernameNotFoundException(username, ex);
        }
    }

    public List<Map<String, Object>> fetchAuthoritieList(String username,
            String userRepoRef, String tenantId) {
        String sqlAuthority = "select p.code as authority"
                + " from AUTH_USER_STATUS us,AUTH_USER_ROLE ur,AUTH_ROLE r,AUTH_PERM_ROLE_DEF pr,AUTH_PERM p"
                + " where us.id=ur.user_status_id and ur.role_id=r.id and r.role_def_id=pr.role_def_id and pr.perm_id=p.id"
                + " and username=? and user_repo_ref=? and r.tenant_id=?";

        List<Map<String, Object>> authorityList = null;

        try {
            authorityList = jdbcTemplate.queryForList(sqlAuthority, username,
                    userRepoRef, tenantId);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            authorityList = new ArrayList<Map<String, Object>>();
        }

        return authorityList;
    }

    public List<Map<String, Object>> fetchAttributeList(String username,
            String userRepoRef, String tenantId) {
        List<Map<String, Object>> attributeList = null;

        try {
            String sqlAttribute = "select r.name as attribute"
                    + " from AUTH_USER_STATUS us,AUTH_USER_ROLE ur,AUTH_ROLE r"
                    + " where us.id=ur.user_status_id and ur.role_id=r.id"
                    + " and username=? and user_repo_ref=? and r.tenant_id=?";
            attributeList = jdbcTemplate.queryForList(sqlAttribute, username,
                    userRepoRef, tenantId);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            attributeList = new ArrayList<Map<String, Object>>();
        }

        return attributeList;
    }

    public void setDefaultUserRepoRef(String defaultUserRepoRef) {
        this.defaultUserRepoRef = defaultUserRepoRef;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
