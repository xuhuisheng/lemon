package com.mossle.auth.data;

import java.util.List;

import com.mossle.api.user.UserDTO;

import com.mossle.auth.persistence.domain.Role;
import com.mossle.auth.persistence.domain.RoleDef;
import com.mossle.auth.persistence.domain.UserStatus;
import com.mossle.auth.persistence.manager.PermManager;
import com.mossle.auth.persistence.manager.RoleDefManager;
import com.mossle.auth.persistence.manager.RoleManager;
import com.mossle.auth.persistence.manager.UserStatusManager;

import com.mossle.client.user.UserClient;

import com.mossle.core.csv.CsvCallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenUserCallback implements CsvCallback {
    private static Logger logger = LoggerFactory
            .getLogger(OpenUserCallback.class);
    private String openAppCode;
    private RoleManager roleManager;
    private UserClient userClient;
    private UserStatusManager userStatusManager;
    private String defaultTenantId = "1";

    public void process(List<String> list, int lineNo) throws Exception {
        String username = list.get(0);
        String role = list.get(1);
        UserStatus userStatus = this.findUser(username);

        if (userStatus == null) {
            UserDTO userDto = userClient.findByUsername(username,
                    defaultTenantId);

            userStatus = new UserStatus();
            userStatus.setUsername(username);
            userStatus.setRef(userDto.getId());
            userStatus.setStatus(1);
            userStatus.setTenantId(openAppCode);
            userStatusManager.save(userStatus);
        }

        for (String text : role.split(",")) {
            Role roleInstance = this.findRole(text);

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

    public UserStatus findUser(String username) {
        String hql = "from UserStatus where username=? and tenantId=?";
        UserStatus userStatus = this.userStatusManager.findUnique(hql,
                username, openAppCode);

        return userStatus;
    }

    public Role findRole(String name) {
        String hql = "from Role where name=? and tenantId=?";
        Role role = this.roleManager.findUnique(hql, name, openAppCode);

        return role;
    }

    // ~
    public void setOpenAppCode(String openAppCode) {
        this.openAppCode = openAppCode;
    }

    public void setRoleManager(RoleManager roleManager) {
        this.roleManager = roleManager;
    }

    public void setUserClient(UserClient userClient) {
        this.userClient = userClient;
    }

    public void setUserStatusManager(UserStatusManager userStatusManager) {
        this.userStatusManager = userStatusManager;
    }
}
