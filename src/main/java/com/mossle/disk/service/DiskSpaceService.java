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
public class DiskSpaceService {
    private static Logger logger = LoggerFactory
            .getLogger(DiskSpaceService.class);
    private DiskInfoManager diskInfoManager;
    private DiskSpaceManager diskSpaceManager;
    private DiskMemberManager diskMemberManager;
    private DiskShareManager diskShareManager;
    private DiskRuleManager diskRuleManager;
    private DiskAclManager diskAclManager;
    private StoreClient storeClient;

    /**
     * 创建个人空间.
     * 
     * c=user, t=user, 个人空间 c=group, t=group, 群组空间 c=group, t=repo, 文档库
     */
    public DiskSpace createUserSpace(String userId) {
        String hql = "from DiskSpace where catalog='user' and type='user' and creator=?";
        DiskSpace diskSpace = this.diskSpaceManager.findUnique(hql, userId);

        if (diskSpace != null) {
            return diskSpace;
        }

        DiskRule diskRule = new DiskRule();
        diskRuleManager.save(diskRule);

        diskSpace = new DiskSpace();
        diskSpace.setCatalog("user");
        diskSpace.setType("user");
        diskSpace.setCreator(userId);
        diskSpace.setCreateTime(new Date());
        diskSpace.setDiskRule(diskRule);
        this.diskSpaceManager.save(diskSpace);

        return diskSpace;
    }

    /**
     * 根据用户id返回或创建这个用户的个人文档.
     */
    public DiskSpace findUserSpace(String userId) {
        return this.createUserSpace(userId);
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
}
