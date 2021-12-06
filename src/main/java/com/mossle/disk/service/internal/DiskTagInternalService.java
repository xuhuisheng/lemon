package com.mossle.disk.service.internal;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import com.mossle.disk.persistence.domain.DiskInfo;
import com.mossle.disk.persistence.domain.DiskTag;
import com.mossle.disk.persistence.domain.DiskTagInfo;
import com.mossle.disk.persistence.manager.DiskInfoManager;
import com.mossle.disk.persistence.manager.DiskTagInfoManager;
import com.mossle.disk.persistence.manager.DiskTagManager;
import com.mossle.disk.support.Result;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DiskTagInternalService {
    private static Logger logger = LoggerFactory
            .getLogger(DiskTagInternalService.class);
    private DiskInfoManager diskInfoManager;
    private DiskTagManager diskTagManager;
    private DiskTagInfoManager diskTagInfoManager;

    // 0009
    public List<DiskTag> findTags(Long infoId) {
        String hql = "select diskTag from DiskTag diskTag left join diskTag.diskTagInfos tagInfo "
                + " where tagInfo.diskInfo.id=? order by tagInfo.priority ";

        // String hql = "select diskTag from DiskTag diskTag left join diskTag.diskTagInfos tagInfo "
        // + " where tagInfo.diskInfo.id=?0 order by tagInfo.priority ";
        List<DiskTag> diskTags = this.diskTagManager.find(hql, infoId);

        return diskTags;
    }

    // 0010
    public Result<DiskInfo> updateTags(Long infoId, String userId, String tags) {
        // validate
        if (infoId == null) {
            logger.info("id cannot be null");

            return Result.failure(400, "id cannot null");
        }

        DiskInfo diskInfo = diskInfoManager.get(infoId);

        if (diskInfo == null) {
            logger.info("diskInfo cannot be null");

            return Result.failure(404, "no file " + infoId);
        }

        if (StringUtils.isBlank(tags)) {
            logger.info("tags is blank, clear all tags");
            this.diskTagInfoManager.removeAll(diskInfo.getDiskTagInfos());

            return Result.success(diskInfo);
        }

        Set<String> tagSet = new HashSet<String>();

        for (String tag : tags.split(" ")) {
            tagSet.add(tag.trim());
        }

        int index = 0;

        for (String tag : tagSet) {
            tag = StringUtils.trim(tag);

            if (StringUtils.isBlank(tag)) {
                continue;
            }

            DiskTag diskTag = diskTagManager.findUniqueBy("name", tag);

            if (diskTag == null) {
                diskTag = new DiskTag();
                diskTag.setName(tag);
                diskTagManager.save(diskTag);
            }

            String hql = "from DiskTagInfo where diskInfo.id=? and diskTag.id=?";

            // String hql = "from DiskTagInfo where diskInfo.id=?0 and diskTag.id=?1";
            DiskTagInfo diskTagInfo = diskTagInfoManager.findUnique(hql,
                    diskInfo.getId(), diskTag.getId());

            if (diskTagInfo == null) {
                diskTagInfo = new DiskTagInfo();
                diskTagInfo.setDiskTag(diskTag);
                diskTagInfo.setDiskInfo(diskInfo);
                diskTagInfo.setPriority(index);
                diskTagInfoManager.save(diskTagInfo);
                index++;
            }
        }

        String hql2 = "from DiskTagInfo where diskInfo=?";

        // String hql2 = "from DiskTagInfo where diskInfo=?0";
        List<DiskTagInfo> diskTagInfos = diskTagInfoManager
                .find(hql2, diskInfo);

        for (DiskTagInfo diskTagInfo : diskTagInfos) {
            if (!tagSet.contains(diskTagInfo.getDiskTag().getName())) {
                diskTagInfoManager.remove(diskTagInfo);
            }
        }

        return Result.success(diskInfo);
    }

    // ~
    @Resource
    public void setDiskInfoManager(DiskInfoManager diskInfoManager) {
        this.diskInfoManager = diskInfoManager;
    }

    @Resource
    public void setDiskTagManager(DiskTagManager diskTagManager) {
        this.diskTagManager = diskTagManager;
    }

    @Resource
    public void setDiskTagInfoManager(DiskTagInfoManager diskTagInfoManager) {
        this.diskTagInfoManager = diskTagInfoManager;
    }
}
