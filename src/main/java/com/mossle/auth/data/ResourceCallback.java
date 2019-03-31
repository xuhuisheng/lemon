package com.mossle.auth.data;

import java.util.List;

import com.mossle.auth.persistence.domain.Access;
import com.mossle.auth.persistence.domain.Perm;
import com.mossle.auth.persistence.manager.AccessManager;
import com.mossle.auth.persistence.manager.PermManager;

import com.mossle.core.csv.CsvCallback;

public class ResourceCallback implements CsvCallback {
    private PermManager permManager;
    private AccessManager accessManager;
    private String defaultTenantId = "1";

    public void process(List<String> list, int lineNo) throws Exception {
        String type = list.get(0);
        String value = list.get(1);
        String permission = list.get(2);

        Perm perm = this.permManager.findUniqueBy("code", permission);

        Access access = new Access();
        access.setType(type);
        access.setValue(value);
        access.setTenantId(defaultTenantId);
        access.setPerm(perm);
        access.setPriority(lineNo);
        accessManager.save(access);
    }

    public void setPermManager(PermManager permManager) {
        this.permManager = permManager;
    }

    public void setAccessManager(AccessManager accessManager) {
        this.accessManager = accessManager;
    }
}
