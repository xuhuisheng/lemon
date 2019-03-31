package com.mossle.auth.data;

import java.util.List;

import com.mossle.auth.persistence.domain.Perm;
import com.mossle.auth.persistence.domain.Role;
import com.mossle.auth.persistence.domain.RoleDef;
import com.mossle.auth.persistence.manager.PermManager;
import com.mossle.auth.persistence.manager.RoleDefManager;
import com.mossle.auth.persistence.manager.RoleManager;

import com.mossle.core.csv.CsvCallback;

public class RoleCallback implements CsvCallback {
    private PermManager permManager;
    private RoleManager roleManager;
    private RoleDefManager roleDefManager;
    private String defaultTenantId = "1";

    public void process(List<String> list, int lineNo) throws Exception {
        String name = list.get(0);
        String permission = list.get(1);
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
