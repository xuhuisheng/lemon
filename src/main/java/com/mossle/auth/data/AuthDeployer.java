package com.mossle.auth.data;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.api.user.UserConnector;
import com.mossle.api.user.UserDTO;

import com.mossle.auth.persistence.domain.Access;
import com.mossle.auth.persistence.domain.Perm;
import com.mossle.auth.persistence.domain.PermType;
import com.mossle.auth.persistence.domain.Role;
import com.mossle.auth.persistence.domain.RoleDef;
import com.mossle.auth.persistence.domain.UserStatus;
import com.mossle.auth.persistence.manager.AccessManager;
import com.mossle.auth.persistence.manager.PermManager;
import com.mossle.auth.persistence.manager.PermTypeManager;
import com.mossle.auth.persistence.manager.RoleDefManager;
import com.mossle.auth.persistence.manager.RoleManager;
import com.mossle.auth.persistence.manager.UserStatusManager;

import com.mossle.spi.security.ResourceDetailsRefresher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

public class AuthDeployer {
    private static Logger logger = LoggerFactory.getLogger(AuthDeployer.class);
    private String permissionFilePath = "data/auth-permission.csv";
    private String permissionEncoding = "GB2312";
    private String resourceFilePath = "data/auth-resource.csv";
    private String resourceEncoding = "GB2312";
    private String roleFilePath = "data/auth-role.csv";
    private String roleEncoding = "GB2312";
    private String userFilePath = "data/auth-user.csv";
    private String userEncoding = "GB2312";
    private String defaultTenantId = "1";
    private PermTypeManager permTypeManager;
    private PermManager permManager;
    private AccessManager accessManager;
    private RoleDefManager roleDefManager;
    private RoleManager roleManager;
    private UserStatusManager userStatusManager;
    private UserConnector userConnector;
    private PlatformTransactionManager platformTransactionManager;
    private ResourceDetailsRefresher resourceDetailsRefresher;

    @PostConstruct
    public void init() throws Exception {
        TransactionStatus transactionStatus = platformTransactionManager
                .getTransaction(null);

        this.initPermission();
        this.initResource();
        this.initRole();
        this.initUser();
        platformTransactionManager.commit(transactionStatus);

        // TODO: refresh role cache, resource cache
        resourceDetailsRefresher.refresh();
    }

    public void initPermission() throws Exception {
        List<String[]> list = new CsvParser().parse(permissionFilePath,
                permissionEncoding);

        int permTypeIndex = 0;
        int permIndex = 0;

        for (String[] item : list) {
            String code = item[0];
            String name = item[1];
            String type = item[2];

            Perm perm = this.permManager.findUniqueBy("code", code);

            if (perm != null) {
                continue;
            }

            PermType permType = this.permTypeManager.findUniqueBy("name", type);

            if (permType == null) {
                permType = new PermType();
                permType.setName(type);
                permType.setTenantId(defaultTenantId);
                permType.setPriority(permTypeIndex++);

                if ("默认".equals(type)) {
                    permType.setType(1);
                } else {
                    permType.setType(0);
                }

                permTypeManager.save(permType);
            }

            perm = new Perm();
            perm.setCode(code);
            perm.setName(name);
            perm.setPriority(permIndex++);
            perm.setPermType(permType);
            perm.setTenantId(defaultTenantId);
            this.permManager.save(perm);
        }
    }

    public void initResource() throws Exception {
        List<String[]> list = new CsvParser().parse(resourceFilePath,
                resourceEncoding);

        int resourceIndex = 0;

        for (String[] item : list) {
            String type = item[0];
            String value = item[1];
            String permission = item[2];

            Perm perm = this.permManager.findUniqueBy("code", permission);

            Access access = new Access();
            access.setType(type);
            access.setValue(value);
            access.setTenantId(defaultTenantId);
            access.setPerm(perm);
            access.setPriority(resourceIndex++);
            accessManager.save(access);
        }
    }

    public void initRole() throws Exception {
        List<String[]> list = new CsvParser().parse(roleFilePath, roleEncoding);

        for (String[] item : list) {
            String name = item[0];
            String permission = item[1];
            RoleDef roleDef = roleDefManager.findUniqueBy("name", name);

            if (roleDef == null) {
                roleDef = new RoleDef();
                roleDef.setName(name);
                roleDefManager.save(roleDef);
            }

            Role role = roleManager.findUniqueBy("roleDef", roleDef);

            if (role == null) {
                role = new Role();
                role.setName(name);
                role.setRoleDef(roleDef);
                role.setTenantId(defaultTenantId);
                roleManager.save(role);
            }

            for (String text : permission.split(",")) {
                Perm perm = this.permManager.findUniqueBy("code", text);

                if (perm == null) {
                    continue;
                }

                roleDef.getPerms().add(perm);
                roleDefManager.save(roleDef);
                permManager.save(perm);
            }
        }
    }

    public void initUser() throws Exception {
        List<String[]> list = new CsvParser().parse(userFilePath, userEncoding);

        for (String[] item : list) {
            String username = item[0];
            String role = item[1];
            UserStatus userStatus = userStatusManager.findUniqueBy("username",
                    username);

            if (userStatus == null) {
                UserDTO userDto = userConnector.findByUsername(username,
                        defaultTenantId);

                userStatus = new UserStatus();
                userStatus.setUsername(username);
                userStatus.setRef(userDto.getId());
                userStatus.setStatus(1);
                userStatus.setTenantId(defaultTenantId);
                userStatusManager.save(userStatus);
            }

            for (String text : role.split(",")) {
                Role roleInstance = roleManager.findUniqueBy("name", text);

                if (roleInstance == null) {
                    logger.info("role not exists : {}", text);

                    continue;
                }

                userStatus.getRoles().add(roleInstance);
                // roleInstance.getUserStatuses().add(userStatus);
                userStatusManager.save(userStatus);

                // roleManager.save(roleInstance);
            }
        }
    }

    @Resource
    public void setPermManager(PermManager permManager) {
        this.permManager = permManager;
    }

    @Resource
    public void setPermTypeManager(PermTypeManager permTypeManager) {
        this.permTypeManager = permTypeManager;
    }

    @Resource
    public void setAccessManager(AccessManager accessManager) {
        this.accessManager = accessManager;
    }

    @Resource
    public void setRoleManager(RoleManager roleManager) {
        this.roleManager = roleManager;
    }

    @Resource
    public void setRoleDefManager(RoleDefManager roleDefManager) {
        this.roleDefManager = roleDefManager;
    }

    @Resource
    public void setUserStatusManager(UserStatusManager userStatusManager) {
        this.userStatusManager = userStatusManager;
    }

    @Resource
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }

    @Resource
    public void setPlatformTransactionManager(
            PlatformTransactionManager platformTransactionManager) {
        this.platformTransactionManager = platformTransactionManager;
    }

    @Resource
    public void setResourceDetailsRefresher(
            ResourceDetailsRefresher resourceDetailsRefresher) {
        this.resourceDetailsRefresher = resourceDetailsRefresher;
    }
}
