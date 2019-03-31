package com.mossle.auth.component;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.tenant.TenantConnector;
import com.mossle.api.tenant.TenantDTO;

import com.mossle.auth.persistence.domain.Role;
import com.mossle.auth.persistence.domain.UserStatus;
import com.mossle.auth.support.RoleDTO;

import org.springframework.stereotype.Component;

public class RoleConverter {
    public RoleDTO createRoleDto(Role role) {
        RoleDTO roleDto = new RoleDTO();
        roleDto.setId(role.getId());
        roleDto.setName(role.getName());

        StringBuilder buff = new StringBuilder();

        for (UserStatus userStatus : role.getUserStatuses()) {
            buff.append(userStatus.getUsername()).append(",");
        }

        if (buff.length() > 0) {
            buff.deleteCharAt(buff.length() - 1);
        }

        roleDto.setUsers(buff.toString());

        return roleDto;
    }

    public List<RoleDTO> createRoleDtos(List<Role> roles) {
        List<RoleDTO> roleDtos = new ArrayList<RoleDTO>();

        for (Role role : roles) {
            roleDtos.add(this.createRoleDto(role));
        }

        return roleDtos;
    }
}
