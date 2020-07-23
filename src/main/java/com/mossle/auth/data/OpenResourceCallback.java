package com.mossle.auth.data;

import java.util.List;

import com.mossle.auth.persistence.domain.Access;
import com.mossle.auth.persistence.domain.Perm;
import com.mossle.auth.persistence.manager.AccessManager;
import com.mossle.auth.persistence.manager.PermManager;

import com.mossle.core.csv.CsvCallback;

public class OpenResourceCallback implements CsvCallback {
    private String openAppCode;
    private PermManager permManager;
    private AccessManager accessManager;

    public void process(List<String> list, int lineNo) throws Exception {
        String type = list.get(0);
        String value = list.get(1);
        String permission = list.get(2);

        Perm perm = this.findPerm(permission);

        Access access = new Access();
        access.setType(type);
        access.setValue(value);
        access.setTenantId(openAppCode);
        access.setPerm(perm);
        access.setPriority(lineNo);
        accessManager.save(access);
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

    public void setAccessManager(AccessManager accessManager) {
        this.accessManager = accessManager;
    }
}
