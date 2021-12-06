package com.mossle.disk.support;

import java.util.Date;

import com.mossle.disk.persistence.domain.DiskInfo;

public class DiskInfoBuilder {
    private DiskInfo diskInfo = new DiskInfo();

    public DiskInfo build() {
        Date now = new Date();
        // common
        diskInfo.setCreator("");
        diskInfo.setCreateTime(now);
        diskInfo.setLastModifier("");
        diskInfo.setLastModifiedTime(now);
        // info
        diskInfo.setOwnerId("");
        diskInfo.setName("");
        diskInfo.setDescription("");
        diskInfo.setType("");
        diskInfo.setFileSize(0L);
        diskInfo.setDirType(0);
        diskInfo.setRef("");
        diskInfo.setStatus("active");
        diskInfo.setDiskInfo(null);
        diskInfo.setDiskSpace(null);
        diskInfo.setDiskRule(null);
        diskInfo.setInherit("true");
        // preview
        diskInfo.setPreviewStatus("");
        diskInfo.setPreviewRef("");
        diskInfo.setParentPath("");
        diskInfo.setPriority(0);
        diskInfo.setCheckoutStatus("");
        diskInfo.setFileVersion("1");
        diskInfo.setSecurityLevel("");
        diskInfo.setOriginalParentId(0L);
        // link
        diskInfo.setLinkType(0);
        diskInfo.setLinkId(0L);
        // delete
        diskInfo.setDeleteStatus("");
        diskInfo.setDeleteTime(now);
        // public
        diskInfo.setPublicType("private");
        diskInfo.setPublicEdit("false");

        return diskInfo;
    }
}
