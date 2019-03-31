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

import com.mossle.client.user.UserClient;

import com.mossle.core.csv.CsvProcessor;

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
    private UserClient userClient;

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
        PermCallback permCallback = new PermCallback();
        permCallback.setPermManager(permManager);
        permCallback.setPermTypeManager(permTypeManager);
        new CsvProcessor().process(permissionFilePath, permissionEncoding,
                permCallback);
    }

    public void initResource() throws Exception {
        ResourceCallback resourceCallback = new ResourceCallback();
        resourceCallback.setPermManager(permManager);
        resourceCallback.setAccessManager(accessManager);
        new CsvProcessor().process(resourceFilePath, resourceEncoding,
                resourceCallback);
    }

    public void initRole() throws Exception {
        RoleCallback roleCallback = new RoleCallback();
        roleCallback.setPermManager(permManager);
        roleCallback.setRoleManager(roleManager);
        roleCallback.setRoleDefManager(roleDefManager);
        new CsvProcessor().process(roleFilePath, roleEncoding, roleCallback);
    }

    public void initUser() throws Exception {
        UserCallback userCallback = new UserCallback();
        userCallback.setUserStatusManager(userStatusManager);
        userCallback.setRoleManager(roleManager);
        userCallback.setUserClient(userClient);
        new CsvProcessor().process(userFilePath, userEncoding, userCallback);
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

    @Resource
    public void setUserClient(UserClient userClient) {
        this.userClient = userClient;
    }
}
