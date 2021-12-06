package com.mossle.disk.service.internal;

import java.io.InputStream;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.client.store.StoreClient;

import com.mossle.disk.persistence.domain.DiskInfo;
import com.mossle.disk.persistence.domain.DiskSpace;
import com.mossle.disk.persistence.domain.DiskVersion;
import com.mossle.disk.persistence.manager.DiskInfoManager;
import com.mossle.disk.persistence.manager.DiskVersionManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DiskVersionInternalService {
    private static Logger logger = LoggerFactory
            .getLogger(DiskVersionInternalService.class);
    private DiskVersionManager diskVersionManager;
    private DiskInfoManager diskInfoManager;
    private StoreClient storeClient;

    /**
     * 添加版本.
     */
    public DiskVersion createVersion(DiskInfo diskInfo) {
        List<DiskVersion> diskVersions = this.findVersions(diskInfo);
        int versionNo = diskVersions.size() + 1;

        // version
        DiskVersion diskVersion = new DiskVersion();
        diskVersion.setName(diskInfo.getName());
        diskVersion.setType(diskInfo.getType());
        diskVersion.setFileSize(diskInfo.getFileSize());
        diskVersion.setFilePath(diskInfo.getRef());
        diskVersion.setCreator(diskInfo.getCreator());
        diskVersion.setCreateTime(new Date());
        diskVersion.setLastModifier(diskInfo.getLastModifier());
        diskVersion.setLastModifiedTime(diskInfo.getLastModifiedTime());
        // diskVersion.setDirType(diskInfo.getDirType());
        diskVersion.setDiskInfo(diskInfo);
        // diskInfo.setStatus("active");
        // diskInfo.setParentPath(parentPath);
        // diskInfo.setDiskSpace(diskSpace);
        // diskInfo.setInherit("true");
        // diskInfo.setDiskRule(diskRule);
        // diskInfo.setDiskInfo(folder);
        // diskInfo.setDiskSpace(folder.getDiskSpace());
        diskVersion.setPriority(versionNo);
        diskVersion.setFileVersion(Integer.toString(versionNo));
        diskVersionManager.save(diskVersion);

        return diskVersion;
    }

    /**
     * 获取版本.
     */
    public DiskVersion findVersionByNo(DiskInfo diskInfo, int versionNo) {
        String hql = "from DiskVersion where diskInfo=? and priority=?";

        // String hql = "from DiskVersion where diskInfo=?0 and priority=?1";
        return this.diskVersionManager.findUnique(hql, diskInfo, versionNo);
    }

    /**
     * 版本列表.
     */
    public List<DiskVersion> findVersions(DiskInfo diskInfo) {
        String hql = "from DiskVersion where diskInfo=? order by id desc";

        // String hql = "from DiskVersion where diskInfo=?0 order by id desc";
        return diskInfoManager.find(hql, diskInfo);
    }

    /**
     * 获取下载使用的输入流.
     */
    public InputStream findDownloadInputStream(Long versionId, String userId,
            String tenantId) throws Exception {
        logger.info("download : {} {}", versionId, userId);

        // acl
        // if (this.diskAclService.lackPermission(fileId, userId,
        // DiskAclService.MASK_DOWNLOAD)) {
        // logger.info("lack permission : {} {} {} {} {}", fileId, "file",
        // userId, DiskAclService.MASK_DOWNLOAD, "download");
        // throw new DiskAclException(fileId, "file", userId,
        // DiskAclService.MASK_DOWNLOAD, "download");
        // }
        DiskVersion diskVersion = diskVersionManager.get(versionId);

        if (diskVersion == null) {
            logger.info("cannot find version : {}", versionId);

            return null;
        }

        DiskInfo diskInfo = diskVersion.getDiskInfo();

        if (diskInfo == null) {
            logger.info("cannot find file : {}", versionId);

            return null;
        }

        DiskSpace diskSpace = diskInfo.getDiskSpace();

        if (diskSpace == null) {
            logger.info("cannot find space : {}", versionId);

            return null;
        }

        // String modelName = "disk/" + diskSpace.getId();
        String modelName = "disk";
        String keyName = diskVersion.getFilePath();

        InputStream is = storeClient.getStore(modelName, keyName, tenantId)
                .getDataSource().getInputStream();

        // log
        // this.diskLogService.recordLog(diskInfo, userId, DiskLogService.CATALOG_DOWNLOAD);
        return is;
    }

    // ~
    @Resource
    public void setDiskVersionManager(DiskVersionManager diskVersionManager) {
        this.diskVersionManager = diskVersionManager;
    }

    @Resource
    public void setDiskInfoManager(DiskInfoManager diskInfoManager) {
        this.diskInfoManager = diskInfoManager;
    }

    @Resource
    public void setStoreClient(StoreClient storeClient) {
        this.storeClient = storeClient;
    }
}
