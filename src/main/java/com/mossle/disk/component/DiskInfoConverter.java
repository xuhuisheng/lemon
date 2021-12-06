package com.mossle.disk.component;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.user.UserDTO;

import com.mossle.client.user.UserClient;

import com.mossle.disk.persistence.domain.DiskFavorite;
import com.mossle.disk.persistence.domain.DiskInfo;
import com.mossle.disk.persistence.domain.DiskTagInfo;
import com.mossle.disk.persistence.manager.DiskFavoriteManager;
import com.mossle.disk.support.DiskInfoDTO;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
public class DiskInfoConverter {
    private static Logger logger = LoggerFactory
            .getLogger(DiskInfoConverter.class);
    private UserClient userClient;
    private DiskFavoriteManager diskFavoriteManager;

    public List<DiskInfoDTO> convertList(List<DiskInfo> diskInfos) {
        return this.convertList(diskInfos, "");
    }

    public DiskInfoDTO convertOne(DiskInfo diskInfo) {
        return this.convertOne(diskInfo, "");
    }

    public List<DiskInfoDTO> convertList(List<DiskInfo> diskInfos, String userId) {
        List<DiskInfoDTO> list = new ArrayList<DiskInfoDTO>();

        for (DiskInfo diskInfo : diskInfos) {
            list.add(this.convertOne(diskInfo, userId));
        }

        return list;
    }

    public DiskInfoDTO convertOne(DiskInfo diskInfo, String userId) {
        String ownerId = diskInfo.getCreator();
        UserDTO userDto = userClient.findById(ownerId, "1");
        DiskInfo folder = diskInfo.getDiskInfo();
        String folderCode = "";
        String folderName = "";

        if (folder != null) {
            folderCode = Long.toString(folder.getId());
            folderName = folder.getName();
        }

        DiskInfoDTO diskInfoDto = new DiskInfoDTO();
        diskInfoDto.setCode(Long.toString(diskInfo.getId()));
        diskInfoDto.setName(diskInfo.getName());
        diskInfoDto.setType(diskInfo.getType());
        diskInfoDto.setDirType(convertInt(diskInfo.getDirType()));
        diskInfoDto.setLinkType(convertInt(diskInfo.getLinkType()));
        diskInfoDto.setSize(convertLong(diskInfo.getFileSize()));
        diskInfoDto.setOwnerId(ownerId);
        diskInfoDto.setOwnerName(userDto.getDisplayName());
        diskInfoDto.setOwnerAvatar(ownerId);
        diskInfoDto.setOwnerType("user");
        diskInfoDto.setCreateTime(diskInfo.getCreateTime().getTime());

        if (diskInfo.getLastModifiedTime() != null) {
            diskInfoDto.setLastModifiedTime(diskInfo.getLastModifiedTime()
                    .getTime());
        } else {
            diskInfoDto.setLastModifiedTime(diskInfo.getCreateTime().getTime());
        }

        diskInfoDto.setStatus(diskInfo.getStatus());
        diskInfoDto.setSecurityLevel(diskInfo.getSecurityLevel());
        diskInfoDto.setFolderCode(folderCode);
        diskInfoDto.setFolderName(folderName);
        diskInfoDto.setDeleteTime(diskInfo.getDeleteTime().getTime());
        diskInfoDto
                .setSpaceCode(Long.toString(diskInfo.getDiskSpace().getId()));
        diskInfoDto.setSpaceName(diskInfo.getDiskSpace().getName());
        diskInfoDto.setSpaceType(diskInfo.getDiskSpace().getCatalog());

        StringBuilder buff = new StringBuilder();

        for (DiskTagInfo diskTagInfo : diskInfo.getDiskTagInfos()) {
            buff.append(diskTagInfo.getDiskTag().getName()).append(" ");
        }

        diskInfoDto.setTags(buff.toString());

        if (StringUtils.isNotBlank(userId)) {
            logger.debug("userId : {}, infoId : {}", userId, diskInfo.getId());

            String hql = "from DiskFavorite where creator=? and diskInfo.id=?";
            DiskFavorite diskFavorite = diskFavoriteManager.findUnique(hql,
                    userId, diskInfo.getId());

            if (diskFavorite != null) {
                diskInfoDto.setFavorite(true);
            }
        }

        return diskInfoDto;
    }

    //
    public int convertInt(Integer value) {
        if (value == null) {
            return 0;
        }

        return value.intValue();
    }

    public long convertLong(Long value) {
        if (value == null) {
            return 0L;
        }

        return value.longValue();
    }

    @Resource
    public void setUserClient(UserClient userClient) {
        this.userClient = userClient;
    }

    @Resource
    public void setDiskFavoriteManager(DiskFavoriteManager diskFavoriteManager) {
        this.diskFavoriteManager = diskFavoriteManager;
    }
}
