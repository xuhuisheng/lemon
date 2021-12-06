package com.mossle.disk.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.disk.persistence.domain.DiskInfo;
import com.mossle.disk.persistence.domain.DiskMember;
import com.mossle.disk.persistence.domain.DiskShare;
import com.mossle.disk.persistence.domain.DiskSpace;
import com.mossle.disk.persistence.manager.DiskInfoManager;
import com.mossle.disk.persistence.manager.DiskMemberManager;
import com.mossle.disk.persistence.manager.DiskShareManager;
import com.mossle.disk.util.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

@Service
public class DiskService {
    private static Logger logger = LoggerFactory.getLogger(DiskService.class);
    private DiskInfoManager diskInfoManager;
    private DiskMemberManager diskMemberManager;
    private DiskShareManager diskShareManager;

    /**
     * 显示对应用户，对应目录下的所有文件.
     */
    public List<DiskInfo> listFiles(String userId, String parentPath) {
        String hql = "from DiskInfo where creator=? and parentPath=? and status='active' order by dirType";

        // String hql = "from DiskInfo where creator=?0 and parentPath=?1 and status='active' order by dirType";
        return diskInfoManager.find(hql, userId, parentPath);
    }

    /**
     * 删除.
     */
    public String remove(Long id) {
        DiskInfo diskInfo = diskInfoManager.get(id);

        if (diskInfo == null) {
            return "";
        }

        diskInfo.setStatus("trash");
        diskInfoManager.save(diskInfo);

        return diskInfo.getParentPath();
    }

    /**
     * 判断是否重复.
     */
    public boolean isDumplicated(String userId, String name, String path) {
        String hql = "from DiskInfo where creator=? and name=? and parentPath=?";

        // String hql = "from DiskInfo where creator=?0 and name=?1 and parentPath=?2";
        return diskInfoManager.findUnique(hql, userId, name, path) != null;
    }

    /**
     * 重命名.
     */
    public String rename(String userId, Long id, String name) {
        DiskInfo diskInfo = diskInfoManager.get(id);
        String parentPath = diskInfo.getParentPath();
        String type = FileUtils.getSuffix(name);
        String hql = "select name from DiskInfo where creator=? and parentPath=? and id!=?";

        // String hql = "select name from DiskInfo where creator=?0 and parentPath=?1 and id!=?2";
        List<String> currentNames = diskInfoManager.find(hql, userId,
                parentPath, id);
        String targetName = FileUtils.calculateName(name, currentNames);
        diskInfo.setName(targetName);
        diskInfo.setType(type);
        diskInfoManager.save(diskInfo);

        return parentPath;
    }

    /**
     * 移动.
     */
    public String move(String userId, Long id, Long parentId) {
        DiskInfo diskInfo = diskInfoManager.get(id);
        String parentPath = diskInfo.getParentPath();

        if (id.equals(parentId)) {
            logger.info("{} is equals {}", id, parentId);

            return diskInfo.getParentPath();
        }

        if (parentId != 0) {
            DiskInfo parent = diskInfoManager.get(parentId);

            if (!"folder".equals(parent.getType())) {
                logger.info("{}({}) is not directory", parent.getParentPath()
                        + "/" + parent.getName(), parentId);

                return diskInfo.getParentPath();
            }

            String currentPath = diskInfo.getParentPath() + "/"
                    + diskInfo.getName() + "/";
            String checkedParentPath = parent.getParentPath() + "/";

            if ("folder".equals(diskInfo.getType())
                    && checkedParentPath.startsWith(currentPath)) {
                logger.info("{}({}) is sub directory of {}({})",
                        parent.getParentPath() + "/" + parent.getName(),
                        parentId,
                        diskInfo.getParentPath() + "/" + diskInfo.getName(), id);

                return diskInfo.getParentPath();
            }

            diskInfo.setParentPath(parent.getParentPath() + "/"
                    + parent.getName());
        } else {
            diskInfo.setParentPath("");
        }

        String name = diskInfo.getName();
        String hql = "select name from DiskInfo where creator=? and parentPath=? and id!=?";

        // String hql = "select name from DiskInfo where creator=?0 and parentPath=?1 and id!=?2";
        List<String> currentNames = diskInfoManager.find(hql, userId,
                parentPath, id);
        String targetName = FileUtils.calculateName(name, currentNames);
        diskInfo.setName(targetName);

        diskInfoManager.save(diskInfo);

        return parentPath;
    }

    // ~

    /**
     * 根据space显示文件列表.
     */
    public List<DiskInfo> listFiles(DiskSpace diskSpace, String parentPath) {
        if (parentPath == null) {
            parentPath = "";
        }

        String hql = "from DiskInfo where diskSpace=? and parentPath=? and status='active' order by dirType";

        // String hql = "from DiskInfo where diskSpace=?0 and parentPath=?1 and status='active' order by dirType";
        return diskInfoManager.find(hql, diskSpace, parentPath);
    }

    /**
     * 根据share显示文件列表.
     */
    public List<DiskInfo> listFiles(DiskShare diskShare, String parentPath) {
        if (parentPath == null) {
            parentPath = "";
        }

        DiskInfo diskInfo = diskShare.getDiskInfo();
        DiskSpace diskSpace = diskInfo.getDiskSpace();
        String targetParentPath = diskInfo.getParentPath() + parentPath;

        return listFiles(diskSpace, targetParentPath);
    }

    /**
     * 查询或创建共享根目录.
     */
    public DiskShare findShare(Long infoId, String creator, String member) {
        DiskInfo diskInfo = diskInfoManager.get(infoId);
        String hql = "from DiskShare where diskInfo=? and creator=?";

        // String hql = "from DiskShare where diskInfo=?0 and creator=?1";
        DiskShare diskShare = diskShareManager.findUnique(hql, diskInfo,
                creator);

        if (diskShare == null) {
            diskShare = new DiskShare();
            diskShare.setName(diskInfo.getName());
            diskShare.setDiskInfo(diskInfo);
            diskShare.setCreator(creator);
            diskShare.setCatalog("internal");
            diskShareManager.save(diskShare);
        }

        this.addMember(diskShare, member);
        this.addMember(diskInfo.getDiskSpace(), member);

        return diskShare;
    }

    public void addMember(DiskShare diskShare, String member) {
        String hql = "from DiskMember where diskShare=? and userId=?";

        // String hql = "from DiskMember where diskShare=?0 and userId=?1";
        DiskMember diskMember = this.diskMemberManager.findUnique(hql,
                diskShare, member);

        if (diskMember != null) {
            return;
        }

        diskMember = new DiskMember();
        diskMember.setDiskShare(diskShare);
        diskMember.setUserId(member);
        diskMember.setCreateTime(new Date());
        diskMemberManager.save(diskMember);
    }

    public void addMember(DiskSpace diskSpace, String member) {
        String hql = "from DiskMember where diskShare=? and userId=?";

        // String hql = "from DiskMember where diskShare=?0 and userId=?1";
        DiskMember diskMember = this.diskMemberManager.findUnique(hql,
                diskSpace, member);

        if (diskMember != null) {
            return;
        }

        diskMember = new DiskMember();
        diskMember.setDiskSpace(diskSpace);
        diskMember.setUserId(member);
        diskMember.setCreateTime(new Date());
        diskMemberManager.save(diskMember);
    }

    // ~
    @Resource
    public void setDiskInfoManager(DiskInfoManager diskInfoManager) {
        this.diskInfoManager = diskInfoManager;
    }

    @Resource
    public void setDiskMemberManager(DiskMemberManager diskMemberManager) {
        this.diskMemberManager = diskMemberManager;
    }

    @Resource
    public void setDiskShareManager(DiskShareManager diskShareManager) {
        this.diskShareManager = diskShareManager;
    }
}
