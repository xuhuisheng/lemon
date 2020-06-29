package com.mossle.auth.data;

import java.util.List;

import com.mossle.auth.persistence.domain.Perm;
import com.mossle.auth.persistence.domain.Role;
import com.mossle.auth.persistence.domain.RoleDef;
import com.mossle.auth.persistence.manager.PermManager;
import com.mossle.auth.persistence.manager.RoleDefManager;
import com.mossle.auth.persistence.manager.RoleManager;

import com.mossle.core.csv.CsvCallback;

public class OpenRoleCallback implements CsvCallback {
    private String openAppCode;
    private PermManager permManager;
    private RoleManager roleManager;
    private RoleDefManager roleDefManager;

    public void process(List<String> list, int lineNo) throws Exception {
        String name = list.get(0);
        String permission = list.get(1);
        RoleDef roleDef = this.findRoleDef(name);

        if (roleDef == null) {
            roleDef = new RoleDef();
            roleDef.setName(name);
            roleDef.setTenantId(openAppCode);
            roleDefManager.save(roleDef);
        }

        Role role = this.findRole(roleDef);

        if (role == null) {
            role = new Role();
            role.setName(name);
            role.setRoleDef(roleDef);
            role.setTenantId(openAppCode);
            roleManager.save(role);
        }

        for (String text : permission.split(",")) {
            Perm perm = this.findPerm(text);

            if (perm == null) {
                continue;
            }

            roleDef.getPerms().add(perm);
            roleDefManager.save(roleDef);
            permManager.save(perm);
        }
    }

    public RoleDef findRoleDef(String name) {
        String hql = "from RoleDef where name=? and tenantId=?";
        RoleDef roleDef = this.roleDefManager
                .findUnique(hql, name, openAppCode);

        return roleDef;
    }

    public Role findRole(RoleDef roleDef) {
        String hql = "from Role where roleDef=? and tenantId=?";
        Role role = this.roleManager.findUnique(hql, roleDef, openAppCode);

        return role;
    }

    public Perm findPerm(String code) {
        String hql = "from Perm where code=? and tenantId=?";
        Perm perm = this.permManager.findUnique(hql, code, openAppCode);

        return perm;
    }

    // ~
    public void setOpenAppCode(String openAppCode) {
        this.openAppCode = openAppCode;
    }

    public void setPermManager(PermManager permManager) {
        this.permManager = permManager;
    }

    public void setRoleManager(RoleManager roleManager) {
        this.roleManager = roleManager;
    }

    public void setRoleDefManager(RoleDefManager roleDefManager) {
        this.roleDefManager = roleDefManager;
    }
}
