package com.mossle.disk.support;

import java.util.Date;

import com.mossle.disk.persistence.domain.DiskSid;

public class DiskSidBuilder {
    private DiskSid diskSid = new DiskSid();

    public DiskSid build() {
        Date now = new Date();
        diskSid.setCatalog("");
        diskSid.setValue("");
        diskSid.setName("");
        diskSid.setRef("");
        diskSid.setCreator("system");
        diskSid.setCreateTime(now);
        diskSid.setUpdater("system");
        diskSid.setUpdateTime(now);
        diskSid.setStatus("active");

        return diskSid;
    }
}
