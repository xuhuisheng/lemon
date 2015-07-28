package com.mossle.auth.support;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.scope.ScopeConnector;
import com.mossle.api.scope.ScopeDTO;
import com.mossle.api.user.UserConnector;
import com.mossle.api.user.UserDTO;
import com.mossle.api.userauth.UserAuthConnector;
import com.mossle.api.userauth.UserAuthDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.EmptyResultDataAccessException;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.util.Assert;

public class DatabaseUserAuthConnector implements UserAuthConnector {
    private static Logger logger = LoggerFactory
            .getLogger(DatabaseUserAuthConnector.class);
    private JdbcTemplate jdbcTemplate;
    private ScopeConnector scopeConnector;
    private UserConnector userConnector;

    // ~
    private String sqlFindPermissions = "select p.code as permission"
            + " from AUTH_USER_STATUS us,AUTH_USER_ROLE ur,AUTH_ROLE r,AUTH_PERM_ROLE_DEF pr,AUTH_PERM p"
            + " where us.id=ur.user_status_id and ur.role_id=r.id and r.role_def_id=pr.role_def_id and pr.perm_id=p.id"
            + " and us.ref=? and us.scope_id=?";
    private String sqlFindRoles = "select r.name as role"
            + " from AUTH_USER_STATUS us,AUTH_USER_ROLE ur,AUTH_ROLE r"
            + " where us.id=ur.user_status_id and ur.role_id=r.id"
            + " and us.ref=? and us.scope_id=?";
    private String sqlFindAccountLockInfo = "select count(*) from ACCOUNT_LOCK_INFO where username=? and type='default'";
    private String sqlFindAccountExpire = "select CLOSE_TIME from ACCOUNT_INFO where id=?";
    private String sqlFindPasswordExpire = "select EXPIRE_TIME from ACCOUNT_CREDENTIAL where account_id=? and catalog='default'";

    public UserAuthDTO findByUsername(String username, String scopeId) {
        ScopeDTO scopeDto = scopeConnector.findById(scopeId);
        UserDTO userDto = userConnector.findByUsername(username,
                scopeDto.getUserRepoRef());
        Assert.notNull(userDto, "cannot find user by (" + username + ","
                + scopeId + ")");

        return process(userDto, scopeDto);
    }

    public UserAuthDTO findByRef(String ref, String scopeId) {
        ScopeDTO scopeDto = scopeConnector.findById(scopeId);
        UserDTO userDto = userConnector.findByRef(ref,
                scopeDto.getUserRepoRef());

        return process(userDto, scopeDto);
    }

    public UserAuthDTO findById(String id, String scopeId) {
        ScopeDTO scopeDto = scopeConnector.findById(scopeId);
        UserDTO userDto = userConnector.findById(id);

        return process(userDto, scopeDto);
    }

    public UserAuthDTO process(UserDTO userDto, ScopeDTO scopeDto) {
        UserAuthDTO userAuthDto = new UserAuthDTO();
        userAuthDto.setId(userDto.getId());
        userAuthDto.setScopeId(scopeDto.getId());
        userAuthDto.setUsername(userDto.getUsername());
        userAuthDto.setRef(userDto.getRef());
        userAuthDto.setDisplayName(userDto.getDisplayName());
        userAuthDto.setStatus(Integer.toString(userDto.getStatus()));
        // enable
        userAuthDto.setEnabled("1".equals(userAuthDto.getStatus()));
        userAuthDto.setCredentialsExpired(false);
        userAuthDto.setAccountLocked(false);
        userAuthDto.setAccountExpired(false);

        // lock
        int lockCount = jdbcTemplate.queryForObject(sqlFindAccountLockInfo,
                Integer.class, userDto.getUsername());

        if (lockCount > 0) {
            userAuthDto.setAccountLocked(true);
        }

        Date now = new Date();

        try {
            // account expire
            Date accountExpireDate = jdbcTemplate.queryForObject(
                    sqlFindAccountExpire, Date.class, userDto.getId());

            if ((accountExpireDate != null) && accountExpireDate.before(now)) {
                userAuthDto.setAccountExpired(true);
            }

            // password expire
            Date passwordExpireDate = jdbcTemplate.queryForObject(
                    sqlFindPasswordExpire, Date.class, userDto.getId());

            if ((passwordExpireDate != null) && passwordExpireDate.before(now)) {
                userAuthDto.setCredentialsExpired(true);
            }
        } catch (Exception ex) {
            logger.debug(ex.getMessage(), ex);
        }

        // permissions
        List<Map<String, Object>> permissions = jdbcTemplate.queryForList(
                sqlFindPermissions, userDto.getId(), scopeDto.getId());
        userAuthDto.setPermissions(this.convertMapListToStringList(permissions,
                "permission"));

        // roles
        List<Map<String, Object>> roles = jdbcTemplate.queryForList(
                sqlFindRoles, userDto.getId(), scopeDto.getId());
        userAuthDto.setRoles(this.convertMapListToStringList(roles, "role"));

        return userAuthDto;
    }

    public List<String> convertMapListToStringList(
            List<Map<String, Object>> mapList, String name) {
        List<String> stringList = new ArrayList<String>();

        for (Map<String, Object> map : mapList) {
            Object value = map.get(name);

            if (value != null) {
                stringList.add(value.toString());
            }
        }

        return stringList;
    }

    public void setScopeConnector(ScopeConnector scopeConnector) {
        this.scopeConnector = scopeConnector;
    }

    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setSqlFindPermission(String sqlFindPermissions) {
        this.sqlFindPermissions = sqlFindPermissions;
    }

    public void setSqlFindRole(String sqlFindRoles) {
        this.sqlFindRoles = sqlFindRoles;
    }

    public void setSqlFindAccountLockInfo(String sqlFindAccountLockInfo) {
        this.sqlFindAccountLockInfo = sqlFindAccountLockInfo;
    }
}
