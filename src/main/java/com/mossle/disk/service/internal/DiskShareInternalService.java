package com.mossle.disk.service.internal;

import java.util.Date;

import javax.annotation.Resource;

import com.mossle.disk.persistence.domain.DiskInfo;
import com.mossle.disk.persistence.domain.DiskMember;
import com.mossle.disk.persistence.domain.DiskShare;
import com.mossle.disk.persistence.manager.DiskInfoManager;
import com.mossle.disk.persistence.manager.DiskMemberManager;
import com.mossle.disk.persistence.manager.DiskShareManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DiskShareInternalService {
    private static Logger logger = LoggerFactory
            .getLogger(DiskShareInternalService.class);
    private DiskInfoManager diskInfoManager;
    private DiskShareManager diskShareManager;
    private DiskMemberManager diskMemberManager;

    public DiskShare createInternalShare(long infoId, String userId) {
        logger.debug("create internal share : {} {}", infoId, userId);

        Date now = new Date();
        DiskInfo diskInfo = diskInfoManager.get(infoId);
        DiskShare diskShare = new DiskShare();
        diskShare.setShareType("internal");
        diskShare.setShareTime(now);
        diskShare.setName("");
        diskShare.setCreator(userId);
        diskShare.setType("");
        diskShare.setDirType(0);
        diskShare.setCountView(0);
        diskShare.setCountSave(0);
        diskShare.setCountDownload(0);
        diskShare.setShareCode("");
        diskShare.setSharePassword("");
        diskShare.setCatalog("");
        diskShareManager.save(diskShare);

        return diskShare;
    }

    public DiskShare findInternalShare(long infoId, String userId) {
        String hql = "from DiskShare where shareType='internal' and creator=? and diskInfo.id=?";

        return diskShareManager.findUnique(userId, infoId);
    }

    public DiskShare createOrFindInternalShare(long infoId, String userId) {
        DiskShare diskShare = this.findInternalShare(infoId, userId);

        if (diskShare == null) {
            diskShare = this.createInternalShare(infoId, userId);
        }

        return diskShare;
    }

    public DiskShare addMember(long infoId, String userId, String memberId,
            String memberCatalog) {
        DiskShare diskShare = createOrFindInternalShare(infoId, userId);
        DiskMember diskMember = findMember(diskShare.getId(), memberId,
                memberCatalog);

        if (diskMember == null) {
            createMember(diskShare.getId(), memberId, memberCatalog);
        }

        return diskShare;
    }

    public DiskShare removeMember(long infoId, String userId, String memberId,
            String memberCatalog) {
        DiskShare diskShare = createOrFindInternalShare(infoId, userId);
        DiskMember diskMember = findMember(diskShare.getId(), memberId,
                memberCatalog);

        if (diskMember != null) {
            diskMemberManager.remove(diskMember);
        }

        return diskShare;
    }

    public DiskMember findMember(long shareId, String memberId,
            String memberCatalog) {
        String hql = "from DiskMember where diskShare.id=? and name=? and catalog=?";

        return diskMemberManager.findUnique(hql, shareId, memberId,
                memberCatalog);
    }

    public DiskMember createMember(long shareId, String memberId,
            String memberCatalog) {
        DiskMember diskMember = new DiskMember();
        diskMember.setCatalog(memberCatalog);
        diskMember.setName(memberId);
        diskMember.setDescription(memberCatalog + ":" + memberId);
        diskMember.setDiskShare(diskShareManager.get(shareId));
        diskMemberManager.save(diskMember);

        return diskMember;
    }

    // ~
    @Resource
    public void setDiskInfoManager(DiskInfoManager diskInfoManager) {
        this.diskInfoManager = diskInfoManager;
    }

    @Resource
    public void setDiskShareManager(DiskShareManager diskShareManager) {
        this.diskShareManager = diskShareManager;
    }

    @Resource
    public void setDiskMemberManager(DiskMemberManager diskMemberManager) {
        this.diskMemberManager = diskMemberManager;
    }
}
