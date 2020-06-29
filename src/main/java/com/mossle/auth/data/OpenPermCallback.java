package com.mossle.auth.data;

import java.util.List;

import com.mossle.auth.persistence.domain.Perm;
import com.mossle.auth.persistence.domain.PermType;
import com.mossle.auth.persistence.manager.PermManager;
import com.mossle.auth.persistence.manager.PermTypeManager;

import com.mossle.core.csv.CsvCallback;

public class OpenPermCallback implements CsvCallback {
    private String openAppCode;
    private PermManager permManager;
    private PermTypeManager permTypeManager;

    public void process(List<String> list, int lineNo) throws Exception {
        String code = list.get(0);
        String name = list.get(1);
        String type = list.get(2);

        Perm perm = this.findPerm(code);

        if (perm != null) {
            return;
        }

        PermType permType = this.findPermType(type);

        if (permType == null) {
            permType = new PermType();
            permType.setName(type);
            permType.setTenantId(openAppCode);
            permType.setPriority(lineNo + 1);

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
        perm.setTenantId(openAppCode);
        this.permManager.save(perm);
    }

    public Perm findPerm(String code) {
        String hql = "from Perm where code=? and tenantId=?";
        Perm perm = this.permManager.findUnique(hql, code, openAppCode);

        return perm;
    }

    public PermType findPermType(String code) {
        String hql = "from PermType where name=? and tenantId=?";
        PermType permType = this.permTypeManager.findUnique(hql, code,
                openAppCode);

        return permType;
    }

    // ~
    public void setPermManager(PermManager permManager) {
        this.permManager = permManager;
    }

    public void setPermTypeManager(PermTypeManager permTypeManager) {
        this.permTypeManager = permTypeManager;
    }

    public void setOpenAppCode(String openAppCode) {
        this.openAppCode = openAppCode;
    }
}
