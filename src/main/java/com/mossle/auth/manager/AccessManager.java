package com.mossle.auth.manager;

import java.util.List;

import com.mossle.auth.domain.Access;
import com.mossle.auth.domain.Perm;
import com.mossle.auth.domain.Resc;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class AccessManager extends HibernateEntityDao<Access> {
    private static final int PRIORITY_STEP = 10;

    public void batchSave(Long globalId, Long localId, String type,
            List<String> ids, List<String> values, List<String> perms) {
        int priority = 0;

        for (int i = 0; i < values.size(); i++) {
            String idStr = ids.get(i);
            String value = values.get(i);
            String permStr = perms.get(i);

            if (value.trim().length() == 0) {
                continue;
            }

            Access access = createOrGetAccess(idStr, value, type, permStr,
                    globalId, localId);

            priority += PRIORITY_STEP;
            access.setPriority(priority);

            Perm perm = this.createOrGetPerm(permStr, globalId, localId);
            access.setPerm(perm);

            this.save(access);
        }
    }

    public Access createOrGetAccess(String id, String value, String type,
            String perm, Long globalId, Long localId) {
        Access access;

        if (id.length() != 0) {
            access = this.get(Long.parseLong(id));
        } else {
            access = this
                    .findUnique(
                            "from Access where value=? and type=? and global_id=? and local_id=?",
                            value, type, globalId, localId);
        }

        if (access == null) {
            access = new Access();
            access.setType(type);
            access.setValue(value);
            access.setGlobalId(globalId);
            access.setLocalId(localId);
            this.save(access);
        }

        return access;
    }

    public Perm createOrGetPerm(String name, Long globalId, Long localId) {
        Resc resc = createOrGetResc(name, globalId, localId);
        Perm perm = this.findUnique(
                "from Perm where name=? and global_id=? and local_id=?", name,
                globalId, localId);

        if (perm == null) {
            perm = new Perm();
            perm.setName(name);
            perm.setResc(resc);
            perm.setGlobalId(globalId);
            perm.setLocalId(localId);
            this.save(perm);
        }

        return perm;
    }

    public Resc createOrGetResc(String name, Long globalId, Long localId) {
        Resc resc = this.findUnique(
                "from Resc where name=? and global_id=? and local_id=?", name,
                globalId, localId);

        if (resc == null) {
            resc = new Resc();
            resc.setName(name);
            resc.setGlobalId(globalId);
            resc.setLocalId(localId);
            this.save(resc);
        }

        return resc;
    }
}
