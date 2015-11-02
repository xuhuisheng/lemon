package com.mossle.auth.support;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mossle.api.tenant.TenantConnector;
import com.mossle.api.tenant.TenantDTO;
import com.mossle.api.user.UserConnector;
import com.mossle.api.user.UserDTO;
import com.mossle.api.userauth.UserAuthConnector;
import com.mossle.api.userauth.UserAuthDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

public class DatabaseUserAuthConnector implements UserAuthConnector {
    private static Logger logger = LoggerFactory
            .getLogger(DatabaseUserAuthConnector.class);
    private JdbcTemplate jdbcTemplate;
    private TenantConnector tenantConnector;
    private UserConnector userConnector;

    // ~
    private String sqlFindPermissions = "SELECT P.CODE AS PERMISSION"
            + " FROM AUTH_USER_STATUS US,AUTH_USER_ROLE UR,AUTH_ROLE R,AUTH_PERM_ROLE_DEF PR,AUTH_PERM P"
            + " WHERE US.ID=UR.USER_STATUS_ID AND UR.ROLE_ID=R.ID AND R.ROLE_DEF_ID=PR.ROLE_DEF_ID AND PR.PERM_ID=P.ID"
            + " AND US.REF=? AND US.TENANT_ID=?";
    private String sqlFindRoles = "SELECT R.NAME AS ROLE"
            + " FROM AUTH_USER_STATUS US,AUTH_USER_ROLE UR,AUTH_ROLE R"
            + " WHERE US.ID=UR.USER_STATUS_ID AND UR.ROLE_ID=R.ID"
            + " AND US.REF=? AND US.TENANT_ID=?";
    private String sqlFindAccountLockInfo = "SELECT COUNT(*) FROM ACCOUNT_LOCK_INFO WHERE USERNAME=? AND TYPE='default'";
    private String sqlFindAccountExpire = "SELECT CLOSE_TIME FROM ACCOUNT_INFO WHERE ID=?";
    private String sqlFindPasswordExpire = "SELECT EXPIRE_TIME FROM ACCOUNT_CREDENTIAL WHERE ACCOUNT_ID=? AND CATALOG='default'";

    public UserAuthDTO findByUsername(String username, String tenantId) {
        TenantDTO tenantDto = tenantConnector.findById(tenantId);
        UserDTO userDto = userConnector.findByUsername(username,
                tenantDto.getUserRepoRef());

        if (userDto == null) {
            logger.info("cannot find user by (" + username + ","
                    + tenantDto.getUserRepoRef() + ")");

            return null;
        }

        return this.process(userDto, tenantDto);
    }

    public UserAuthDTO findByRef(String ref, String tenantId) {
        TenantDTO tenantDto = tenantConnector.findById(tenantId);
        UserDTO userDto = userConnector.findByRef(ref,
                tenantDto.getUserRepoRef());

        return process(userDto, tenantDto);
    }

    public UserAuthDTO findById(String id, String tenantId) {
        TenantDTO tenantDto = tenantConnector.findById(tenantId);
        UserDTO userDto = userConnector.findById(id);

        return process(userDto, tenantDto);
    }

    public UserAuthDTO process(UserDTO userDto, TenantDTO tenantDto) {
        UserAuthDTO userAuthDto = new UserAuthDTO();
        userAuthDto.setId(userDto.getId());
        userAuthDto.setTenantId(tenantDto.getId());
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
                sqlFindPermissions, userDto.getId(), tenantDto.getId());
        logger.debug("sqlFindPermissions : {}", sqlFindPermissions);
        logger.debug("userDto.getId() : {}", userDto.getId());
        logger.debug("tenantDto.getId() : {}", tenantDto.getId());
        logger.debug("permissions : {}", permissions);
        userAuthDto.setPermissions(this.convertMapListToStringList(permissions,
                "permission"));

        // roles
        List<Map<String, Object>> roles = jdbcTemplate.queryForList(
                sqlFindRoles, userDto.getId(), tenantDto.getId());
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

    public void setTenantConnector(TenantConnector tenantConnector) {
        this.tenantConnector = tenantConnector;
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
