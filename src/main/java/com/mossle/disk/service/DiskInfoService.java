package com.mossle.disk.service;

import java.util.Date;
import java.util.List;

import javax.activation.DataSource;

import javax.annotation.Resource;

import com.mossle.api.store.StoreDTO;

import com.mossle.client.store.StoreClient;

import com.mossle.disk.persistence.domain.DiskAcl;
import com.mossle.disk.persistence.domain.DiskInfo;
import com.mossle.disk.persistence.domain.DiskMember;
import com.mossle.disk.persistence.domain.DiskRule;
import com.mossle.disk.persistence.domain.DiskShare;
import com.mossle.disk.persistence.domain.DiskSpace;
import com.mossle.disk.persistence.manager.DiskAclManager;
import com.mossle.disk.persistence.manager.DiskInfoManager;
import com.mossle.disk.persistence.manager.DiskMemberManager;
import com.mossle.disk.persistence.manager.DiskRuleManager;
import com.mossle.disk.persistence.manager.DiskShareManager;
import com.mossle.disk.persistence.manager.DiskSpaceManager;
import com.mossle.disk.util.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

@Service
public class DiskInfoService {
    private static Logger logger = LoggerFactory
            .getLogger(DiskInfoService.class);
    private DiskInfoManager diskInfoManager;
    private DiskSpaceManager diskSpaceManager;
    private DiskMemberManager diskMemberManager;
    private DiskShareManager diskShareManager;
    private DiskRuleManager diskRuleManager;
    private DiskAclManager diskAclManager;
    private StoreClient storeClient;
    private DiskSpaceService diskSpaceService;

    /**
     * 上传文件.
     */
    public DiskInfo createFile(String userId, DataSource dataSource,
            String name, long size, String parentPath, String tenantId)
            throws Exception {
        String modelName = "disk/user/" + userId;
        String keyName = parentPath + "/" + name;
        StoreDTO storeDto = storeClient.saveStore(modelName, keyName,
                dataSource, tenantId);
        String type = FileUtils.getSuffix(name);

        return this.createDiskInfo(userId, name, size, storeDto.getKey(), type,
                1, parentPath);
    }

    /**
     * 新建文件夹.
     */
    public DiskInfo createDir(String userId, String name, String parentPath) {
        // internalStoreConnector.mkdir("1/disk/user/" + userId + "/" + parentPath
        // + "/" + name);
        return this.createDiskInfo(userId, name, 0, null, "dir", 0, parentPath);
    }

    /**
     * 上传文件，或新建文件夹.
     */
    public DiskInfo createDiskInfo(String userId, String name, long size,
            String ref, String type, int dirType, String parentPath) {
        if (name == null) {
            logger.info("name cannot be null");

            return null;
        }

        name = name.trim();

        if (name.length() == 0) {
            logger.info("name cannot be empty");

            return null;
        }

        if (parentPath == null) {
            parentPath = "";
        } else {
            parentPath = parentPath.trim();
        }

        if (parentPath.length() != 0) {
            if (!parentPath.startsWith("/")) {
                parentPath = "/" + parentPath;
            }

            int index = parentPath.lastIndexOf("/");
            String targetParentPath = parentPath.substring(0, index);
            String targetName = parentPath.substring(index + 1);
            String hql = "from DiskInfo where parentPath=? and name=?";
            DiskInfo parent = diskInfoManager.findUnique(hql, targetParentPath,
                    targetName);

            if (parent == null) {
                logger.info("cannot find : {} {} {}", parentPath,
                        targetParentPath, targetName);

                return null;
            }
        }

        String hql = "select name from DiskInfo where creator=? and parentPath=?";
        List<String> currentNames = diskInfoManager.find(hql, userId,
                parentPath);
        String targetName = FileUtils.calculateName(name, currentNames);

        // TODO: parent
        DiskSpace diskSpace = this.diskSpaceService.findUserSpace(userId);
        DiskRule diskRule = diskSpace.getDiskRule();

        Date now = new Date();
        DiskInfo diskInfo = new DiskInfo();
        diskInfo.setName(targetName);
        diskInfo.setType(type);
        diskInfo.setFileSize(size);
        diskInfo.setCreator(userId);
        diskInfo.setCreateTime(now);
        diskInfo.setLastModifier(userId);
        diskInfo.setLastModifiedTime(now);
        diskInfo.setDirType(dirType);
        diskInfo.setRef(ref);
        diskInfo.setStatus("active");
        diskInfo.setParentPath(parentPath);
        diskInfo.setInherit("true");
        diskInfo.setDiskRule(diskRule);
        diskInfoManager.save(diskInfo);

        return diskInfo;
    }

    /**
     * 上传文件.
     */
    public DiskInfo createFile(String userId, DataSource dataSource,
            String name, long size, String parentPath, Long spaceId,
            String tenantId) throws Exception {
        String modelName = "disk/user/" + userId;
        String keyName = parentPath + "/" + name;
        StoreDTO storeDto = storeClient.saveStore(modelName, keyName,
                dataSource, tenantId);
        String type = FileUtils.getSuffix(name);

        return this.createDiskInfo(userId, name, size, storeDto.getKey(), type,
                1, parentPath, spaceId);
    }

    /**
     * 新建文件夹.
     */
    public DiskInfo createDir(String userId, String name, String parentPath,
            Long spaceId) {
        // internalStoreConnector.mkdir("1/disk/user/" + userId + "/" + parentPath
        // + "/" + name);
        return this.createDiskInfo(userId, name, 0, null, "dir", 0, parentPath,
                spaceId);
    }

    /**
     * 上传文件，或新建文件夹.
     */
    public DiskInfo createDiskInfo(String userId, String name, long size,
            String ref, String type, int dirType, String parentPath,
            Long spaceId) {
        if (name == null) {
            logger.info("name cannot be null");

            return null;
        }

        name = name.trim();

        if (name.length() == 0) {
            logger.info("name cannot be empty");

            return null;
        }

        DiskSpace diskSpace = diskSpaceManager.get(spaceId);

        if (parentPath == null) {
            parentPath = "";
        } else {
            parentPath = parentPath.trim();
        }

        if (parentPath.length() != 0) {
            if (!parentPath.startsWith("/")) {
                parentPath = "/" + parentPath;
            }

            int index = parentPath.lastIndexOf("/");
            String targetParentPath = parentPath.substring(0, index);
            String targetName = parentPath.substring(index + 1);
            String hql = "from DiskInfo where parentPath=? and name=? and diskSpace=?";
            DiskInfo parent = diskInfoManager.findUnique(hql, targetParentPath,
                    targetName, diskSpace);

            if (parent == null) {
                logger.info("cannot find : {} {} {}", parentPath,
                        targetParentPath, targetName);

                return null;
            }
        }

        String hql = "select name from DiskInfo where creator=? and parentPath=? and diskSpace=?";
        List<String> currentNames = diskInfoManager.find(hql, userId,
                parentPath, diskSpace);
        String targetName = FileUtils.calculateName(name, currentNames);

        // TODO: parent
        // DiskSpace diskSpace = this.diskSpaceService.findUserSpace(userId);
        DiskRule diskRule = diskSpace.getDiskRule();

        Date now = new Date();
        DiskInfo diskInfo = new DiskInfo();
        diskInfo.setName(targetName);
        diskInfo.setType(type);
        diskInfo.setFileSize(size);
        diskInfo.setCreator(userId);
        diskInfo.setCreateTime(now);
        diskInfo.setLastModifier(userId);
        diskInfo.setLastModifiedTime(now);
        diskInfo.setDirType(dirType);
        diskInfo.setRef(ref);
        diskInfo.setStatus("active");
        diskInfo.setParentPath(parentPath);
        diskInfo.setDiskSpace(diskSpace);
        diskInfo.setInherit("true");
        diskInfo.setDiskRule(diskRule);
        diskInfoManager.save(diskInfo);

        return diskInfo;
    }

    // ~
    @Resource
    public void setDiskInfoManager(DiskInfoManager diskInfoManager) {
        this.diskInfoManager = diskInfoManager;
    }

    @Resource
    public void setDiskSpaceManager(DiskSpaceManager diskSpaceManager) {
        this.diskSpaceManager = diskSpaceManager;
    }

    @Resource
    public void setDiskMemberManager(DiskMemberManager diskMemberManager) {
        this.diskMemberManager = diskMemberManager;
    }

    @Resource
    public void setDiskShareManager(DiskShareManager diskShareManager) {
        this.diskShareManager = diskShareManager;
    }

    @Resource
    public void setDiskRuleManager(DiskRuleManager diskRuleManager) {
        this.diskRuleManager = diskRuleManager;
    }

    @Resource
    public void setDiskAclManager(DiskAclManager diskAclManager) {
        this.diskAclManager = diskAclManager;
    }

    @Resource
    public void setStoreClient(StoreClient storeClient) {
        this.storeClient = storeClient;
    }

    @Resource
    public void setDiskSpaceService(DiskSpaceService diskSpaceService) {
        this.diskSpaceService = diskSpaceService;
    }
}
