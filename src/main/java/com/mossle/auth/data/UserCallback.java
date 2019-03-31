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

public class UserCallback implements CsvCallback {
    private static Logger logger = LoggerFactory.getLogger(UserCallback.class);
    private RoleManager roleManager;
    private String defaultTenantId = "1";
    private UserClient userClient;
    private UserStatusManager userStatusManager;

    public void process(List<String> list, int lineNo) throws Exception {
        String username = list.get(0);
        String role = list.get(1);
        UserStatus userStatus = userStatusManager.findUniqueBy("username",
                username);

        if (userStatus == null) {
            UserDTO userDto = userClient.findByUsername(username,
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
