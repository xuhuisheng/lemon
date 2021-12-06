package com.mossle.disk.support;

import java.util.Date;

import com.mossle.disk.persistence.domain.DiskFile;

public class DiskFileBuilder {
    private DiskFile diskFile = new DiskFile();

    public DiskFile build() {
        Date now = new Date();
        diskFile.setName("");
        diskFile.setFileSize(0L);
        diskFile.setValue("");
        diskFile.setType("file");
        diskFile.setPartIndex(0);
        diskFile.setFileCode(0L);
        diskFile.setRefCount(1);
        diskFile.setHashCode("");
        diskFile.setCreator("");
        diskFile.setCreateTime(now);
        diskFile.setUpdater("");
        diskFile.setUpdateTime(now);
        diskFile.setStatus("active");
        diskFile.setUserId("");
        diskFile.setFolderPath("");

        return diskFile;
    }
}
