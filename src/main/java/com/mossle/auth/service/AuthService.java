package com.mossle.auth.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.auth.persistence.domain.Access;
import com.mossle.auth.persistence.domain.Perm;
import com.mossle.auth.persistence.domain.Role;
import com.mossle.auth.persistence.domain.UserStatus;
import com.mossle.auth.persistence.manager.AccessManager;
import com.mossle.auth.persistence.manager.PermManager;
import com.mossle.auth.persistence.manager.RoleManager;
import com.mossle.auth.persistence.manager.UserStatusManager;
import com.mossle.auth.support.Exporter;
import com.mossle.auth.support.Importer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.util.Assert;

@Transactional
@Service
public class AuthService {
    private static final int PRIORITY_STEP = 10;
    private static Logger logger = LoggerFactory.getLogger(AuthService.class);
    private UserStatusManager userStatusManager;
    private RoleManager roleManager;
    private JdbcTemplate jdbcTemplate;
    private AccessManager accessManager;
    private PermManager permManager;

    public UserStatus createOrGetUserStatus(String username, String ref,
            String userRepoRef, String tenantId) {
        UserStatus userStatus = userStatusManager.findUnique(
                "from UserStatus where username=? and tenantId=?", username,
                tenantId);

        if (userStatus == null) {
            userStatus = new UserStatus();
            userStatus.setUsername(username);
            userStatus.setRef(ref);
            userStatus.setUserRepoRef(userRepoRef);
            userStatus.setTenantId(tenantId);
            // TODO: 考虑status同步的策略，目前是默认都设置成了有效
            userStatus.setStatus(1);
            userStatusManager.save(userStatus);
        }

        return userStatus;
    }

    public void configUserRole(Long userId, List<Long> roleIds,
            String userRepoRef, String tenantId, boolean clearRoles) {
        logger.debug("userId: {}, roleIds: {}", userId, roleIds);

        UserStatus userStatus = userStatusManager.get(userId);

        if (userStatus == null) {
            logger.warn("cannot find UserStatus : {}", userId);

            return;
        }

        if (clearRoles) {
            List<Role> roles = new ArrayList<Role>();

            roles.addAll(userStatus.getRoles());

            for (Role role : roles) {
                userStatus.getRoles().remove(role);
            }
        }

        if (roleIds == null) {
            roleIds = Collections.emptyList();
        }

        for (Long roleId : roleIds) {
            Role role = roleManager.get(roleId);
            boolean skip = false;

            if (role == null) {
                logger.warn("role is null, roleId : {}", roleId);

                continue;
            }

            for (Role r : userStatus.getRoles()) {
                logger.debug("r.getId() : {}, role.getId() : {}", r.getId(),
                        role.getId());

                if (r.getId().equals(role.getId())) {
                    skip = true;

                    break;
                }
            }

            if (skip) {
                continue;
            }

            userStatus.getRoles().add(role);
        }

        userStatusManager.save(userStatus);
    }

    public String doExport() {
        Exporter exporter = new Exporter();
        exporter.setJdbcTemplate(jdbcTemplate);

        return exporter.execute();
    }

    public void doImport(String text) {
        Importer importer = new Importer();
        importer.setJdbcTemplate(jdbcTemplate);
        importer.execute(text);
    }

    public void batchSaveAccess(String text, String type, String tenantId) {
        List<Access> accesses = accessManager.find(
                "from Access where type=? and tenantId=?", type, tenantId);

        for (Access access : accesses) {
            accessManager.remove(access);
        }

        int priority = 0;

        for (String line : text.split("\n")) {
            String[] array = line.split(",");
            String value = array[0];
            String permStr = array[1];
            logger.debug("value : {}, perm : {}", value, permStr);

            value = value.trim();
            permStr = permStr.trim();

            if (value.length() == 0) {
                continue;
            }

            priority += PRIORITY_STEP;

            Access access = new Access();
            access.setValue(value);
            access.setTenantId(tenantId);
            access.setType(type);
            access.setPriority(priority);

            Perm perm = permManager.findUnique(
                    "from Perm where code=? and tenantId=?", permStr, tenantId);
            Assert.notNull(perm, "cannot find perm");
            access.setPerm(perm);
            accessManager.save(access);
        }
    }

    public List<Role> findRoles(String tenantId) {
        return roleManager.find("from Role where tenantId=?", tenantId);
    }

    @Resource
    public void setUserStatusManager(UserStatusManager userStatusManager) {
        this.userStatusManager = userStatusManager;
    }

    @Resource
    public void setRoleManager(RoleManager roleManager) {
        this.roleManager = roleManager;
    }

    @Resource
    public void setAccessManager(AccessManager accessManager) {
        this.accessManager = accessManager;
    }

    @Resource
    public void setPermManager(PermManager permManager) {
        this.permManager = permManager;
    }

    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
