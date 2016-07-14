package com.mossle.auth.component;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.tenant.TenantConnector;
import com.mossle.api.tenant.TenantDTO;

import com.mossle.auth.persistence.domain.Role;
import com.mossle.auth.persistence.domain.UserStatus;
import com.mossle.auth.support.UserStatusDTO;

import org.springframework.stereotype.Component;

@Component
public class UserStatusConverter {
    private TenantConnector tenantConnector;

    public UserStatusDTO createUserStatusDto(UserStatus userStatus,
            String userRepoRef, String tenantId) {
        UserStatusDTO userStatusDto = new UserStatusDTO();
        userStatusDto.setId(userStatus.getId());
        userStatusDto.setUsername(userStatus.getUsername());
        userStatusDto.setEnabled(Integer.valueOf(1).equals(
                userStatus.getStatus()));
        userStatusDto.setRef(userStatus.getRef());

        StringBuilder buff = new StringBuilder();

        for (Role role : userStatus.getRoles()) {
            if (tenantId.equals(role.getTenantId())) {
                buff.append(role.getName()).append(",");
            } else {
                TenantDTO tenantDto = tenantConnector.findById(role
                        .getTenantId());
                buff.append(role.getName()).append("(")
                        .append(tenantDto.getName()).append("),");
            }
        }

        if (buff.length() > 0) {
            buff.deleteCharAt(buff.length() - 1);
        }

        userStatusDto.setAuthorities(buff.toString());

        return userStatusDto;
    }

    public List<UserStatusDTO> createUserStatusDtos(
            List<UserStatus> userStatuses, String userRepoRef, String tenantId) {
        List<UserStatusDTO> userStatusDtos = new ArrayList<UserStatusDTO>();

        for (UserStatus userStatus : userStatuses) {
            userStatusDtos.add(createUserStatusDto(userStatus, userRepoRef,
                    tenantId));
        }

        return userStatusDtos;
    }

    @Resource
    public void setTenantConnector(TenantConnector tenantConnector) {
        this.tenantConnector = tenantConnector;
    }
}
