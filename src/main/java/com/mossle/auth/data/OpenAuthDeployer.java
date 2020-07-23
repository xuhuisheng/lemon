package com.mossle.auth.data;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

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

import com.mossle.client.open.OpenAppDTO;
import com.mossle.client.user.UserClient;

import com.mossle.core.csv.CsvProcessor;

import com.mossle.spi.security.ResourceDetailsRefresher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

public class OpenAuthDeployer {
    private static Logger logger = LoggerFactory
            .getLogger(OpenAuthDeployer.class);
    private String permissionFilePath = "data/auth-permission.csv";
    private String permissionEncoding = "GB2312";
    private String resourceFilePath = "data/auth-resource.csv";
    private String resourceEncoding = "GB2312";
    private String roleFilePath = "data/auth-role.csv";
    private String roleEncoding = "GB2312";
    private String userFilePath = "data/auth-user.csv";
    private String userEncoding = "GB2312";
    private String openDataFilePath = "data/open-app.csv";
    private String openDataEncoding = "GB2312";
    private String defaultTenantId = "1";
    private PermTypeManager permTypeManager;
    private PermManager permManager;
    private AccessManager accessManager;
    private RoleDefManager roleDefManager;
    private RoleManager roleManager;
    private UserStatusManager userStatusManager;
    private PlatformTransactionManager platformTransactionManager;
    private ResourceDetailsRefresher resourceDetailsRefresher;
    private UserClient userClient;

    @PostConstruct
    public void init() throws Exception {
        List<OpenAppDTO> openAppDtos = new ArrayList<OpenAppDTO>();
        OpenCallback openCallback = new OpenCallback();
        openCallback.setOpenAppDtos(openAppDtos);
        new CsvProcessor().process(openDataFilePath, openDataEncoding,
                openCallback);

        TransactionStatus transactionStatus = platformTransactionManager
                .getTransaction(null);

        for (OpenAppDTO openAppDto : openAppDtos) {
            String openAppCode = openAppDto.getCode();
            this.initPermission(openAppCode);
            this.initResource(openAppCode);
            this.initRole(openAppCode);
            this.initUser(openAppCode);
        }

        platformTransactionManager.commit(transactionStatus);

        // TODO: refresh role cache, resource cache
        resourceDetailsRefresher.refresh();
    }

    public void initPermission(String openAppCode) throws Exception {
        OpenPermCallback openPermCallback = new OpenPermCallback();
        openPermCallback.setOpenAppCode(openAppCode);
        openPermCallback.setPermManager(permManager);
        openPermCallback.setPermTypeManager(permTypeManager);

        String fileName = "data/open/" + openAppCode + "/auth-permission.csv";
        new CsvProcessor().process(fileName, permissionEncoding,
                openPermCallback);
    }

    public void initResource(String openAppCode) throws Exception {
        OpenResourceCallback openResourceCallback = new OpenResourceCallback();
        openResourceCallback.setOpenAppCode(openAppCode);
        openResourceCallback.setPermManager(permManager);
        openResourceCallback.setAccessManager(accessManager);

        String fileName = "data/open/" + openAppCode + "/auth-resource.csv";
        new CsvProcessor().process(fileName, resourceEncoding,
                openResourceCallback);
    }

    public void initRole(String openAppCode) throws Exception {
        OpenRoleCallback openRoleCallback = new OpenRoleCallback();
        openRoleCallback.setOpenAppCode(openAppCode);
        openRoleCallback.setPermManager(permManager);
        openRoleCallback.setRoleManager(roleManager);
        openRoleCallback.setRoleDefManager(roleDefManager);

        String fileName = "data/open/" + openAppCode + "/auth-role.csv";
        new CsvProcessor().process(fileName, roleEncoding, openRoleCallback);
    }

    public void initUser(String openAppCode) throws Exception {
        OpenUserCallback openUserCallback = new OpenUserCallback();
        openUserCallback.setOpenAppCode(openAppCode);
        openUserCallback.setUserStatusManager(userStatusManager);
        openUserCallback.setRoleManager(roleManager);
        openUserCallback.setUserClient(userClient);

        String fileName = "data/open/" + openAppCode + "/auth-user.csv";
        new CsvProcessor().process(fileName, userEncoding, openUserCallback);
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
