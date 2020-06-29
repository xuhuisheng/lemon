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
public class DiskAclService {
    private static Logger logger = LoggerFactory
            .getLogger(DiskAclService.class);
    private DiskInfoManager diskInfoManager;
    private DiskSpaceManager diskSpaceManager;
    private DiskMemberManager diskMemberManager;
    private DiskShareManager diskShareManager;
    private DiskAclManager diskAclManager;
    private DiskRuleManager diskRuleManager;
    private StoreClient storeClient;

    /**
     * 添加权限.
     */
    public void addPermission(String entityCatalog, String entityRef,
            Long diskInfoId, Integer mask) {
        if (diskInfoId == null) {
            logger.info("disk id cannot null");

            return;
        }

        DiskInfo diskInfo = this.diskInfoManager.get(diskInfoId);

        if (diskInfo == null) {
            logger.info("cannot find disk {}", diskInfoId);

            return;
        }

        DiskRule diskRule = null;

        if ("true".equals(diskInfo.getInherit())) {
            diskRule = new DiskRule();
            diskRuleManager.save(diskRule);
            diskInfo.setDiskRule(diskRule);
            diskInfoManager.save(diskInfo);
        } else {
            diskRule = diskInfo.getDiskRule();
        }

        String hql = "from DiskAcl where diskRule=? and entityCatalog=? and entityRef=?";
        DiskAcl diskAcl = this.diskAclManager.findUnique(hql, diskRule,
                entityCatalog, entityRef);

        if (diskAcl == null) {
            diskAcl = new DiskAcl();
            diskAcl.setEntityCatalog(entityCatalog);
            diskAcl.setEntityRef(entityRef);
            diskAcl.setMask(mask);
            diskAcl.setDiskRule(diskRule);
            diskAclManager.save(diskAcl);
        } else if (diskAcl.getMask() != mask) {
            diskAcl.setMask(mask);
            diskAclManager.save(diskAcl);
        }
    }

    /**
     * 删除权限.
     */
    public void removePermission(String entityCatalog, String entityRef,
            Long diskInfoId) {
        if (diskInfoId == null) {
            logger.info("disk id cannot null");

            return;
        }

        DiskInfo diskInfo = this.diskInfoManager.get(diskInfoId);

        if (diskInfo == null) {
            logger.info("cannot find disk {}", diskInfoId);

            return;
        }

        String hql = "from DiskAcl where diskInfo=? and entityCatalog=? and entityRef=?";
        DiskAcl diskAcl = this.diskInfoManager.findUnique(hql, diskInfo,
                entityCatalog, entityRef);

        if (diskAcl == null) {
            logger.info("cannot find acl : {} {} {}", diskInfoId,
                    entityCatalog, entityRef);

            return;
        }

        diskAclManager.remove(diskAcl);
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
    public void setDiskAclManager(DiskAclManager diskAclManager) {
        this.diskAclManager = diskAclManager;
    }

    @Resource
    public void setDiskRuleManager(DiskRuleManager diskRuleManager) {
        this.diskRuleManager = diskRuleManager;
    }

    @Resource
    public void setStoreClient(StoreClient storeClient) {
        this.storeClient = storeClient;
    }
}
