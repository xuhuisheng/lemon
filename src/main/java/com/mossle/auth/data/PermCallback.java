package com.mossle.auth.data;

import java.util.List;

import com.mossle.auth.persistence.domain.Perm;
import com.mossle.auth.persistence.domain.PermType;
import com.mossle.auth.persistence.manager.PermManager;
import com.mossle.auth.persistence.manager.PermTypeManager;

import com.mossle.core.csv.CsvCallback;

public class PermCallback implements CsvCallback {
    private PermManager permManager;
    private PermTypeManager permTypeManager;
    private String defaultTenantId = "1";

    public void process(List<String> list, int lineNo) throws Exception {
        String code = list.get(0);
        String name = list.get(1);
        String type = list.get(2);

        Perm perm = this.permManager.findUniqueBy("code", code);

        if (perm != null) {
            return;
        }

        PermType permType = this.permTypeManager.findUniqueBy("name", type);

        if (permType == null) {
            permType = new PermType();
            permType.setName(type);
            permType.setTenantId(defaultTenantId);
            permType.setPriority(lineNo++);

            if ("默认".equals(type)) {
                permType.setType(1);
            } else {
                permType.setType(0);
            }

            permTypeManager.save(permType);
        }

        perm = new Perm();
        perm.setCode(code);
        perm.setName(name);
        perm.setPriority(lineNo);
        perm.setPermType(permType);
        perm.setTenantId(defaultTenantId);
        this.permManager.save(perm);
    }

    public void setPermManager(PermManager permManager) {
        this.permManager = permManager;
    }

    public void setPermTypeManager(PermTypeManager permTypeManager) {
        this.permTypeManager = permTypeManager;
    }
}
