package com.mossle.auth.component;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.LocalScopeDTO;
import com.mossle.api.ScopeConnector;

import com.mossle.auth.domain.Role;
import com.mossle.auth.domain.UserStatus;
import com.mossle.auth.support.UserStatusDTO;

import com.mossle.security.region.RegionRoleDTO;

import org.springframework.stereotype.Component;

@Component
public class UserStatusConverter {
    private ScopeConnector scopeConnector;

    public UserStatusDTO createUserStatusDto(UserStatus userStatus,
            Long globalId, Long localId) {
        UserStatusDTO userStatusDto = new UserStatusDTO();
        userStatusDto.setId(userStatus.getId());
        userStatusDto.setUsername(userStatus.getUsername());
        userStatusDto.setEnabled(userStatus.getStatus() == 1);
        userStatusDto.setReference(userStatus.getReference());

        StringBuilder buff = new StringBuilder();

        for (Role role : userStatus.getRoles()) {
            if (localId.equals(role.getLocalId())) {
                buff.append(role.getName()).append(",");
            } else {
                LocalScopeDTO localScopeDto = scopeConnector.getLocalScope(role
                        .getLocalId());
                buff.append(role.getName()).append("(")
                        .append(localScopeDto.getName()).append("),");
            }
        }

        if (buff.length() > 0) {
            buff.deleteCharAt(buff.length() - 1);
        }

        userStatusDto.setAuthorities(buff.toString());

        return userStatusDto;
    }

    public List<UserStatusDTO> createUserStatusDtos(
            List<UserStatus> userStatuses, Long globalId, Long localId) {
        List<UserStatusDTO> userStatusDtos = new ArrayList<UserStatusDTO>();

        for (UserStatus userStatus : userStatuses) {
            userStatusDtos.add(createUserStatusDto(userStatus, globalId,
                    localId));
        }

        return userStatusDtos;
    }

    @Resource
    public void setScopeConnector(ScopeConnector scopeConnector) {
        this.scopeConnector = scopeConnector;
    }
}
