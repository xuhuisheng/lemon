package com.mossle.disk.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.disk.persistence.domain.DiskInfo;
import com.mossle.disk.persistence.domain.DiskRule;
import com.mossle.disk.persistence.domain.DiskSpace;
import com.mossle.disk.persistence.manager.DiskSpaceManager;
import com.mossle.disk.service.internal.DiskAclInternalService;
import com.mossle.disk.service.internal.DiskBaseInternalService;
import com.mossle.disk.service.internal.DiskQueryInternalService;
import com.mossle.disk.support.DiskAclException;
import com.mossle.disk.support.FolderTreeNode;
import com.mossle.disk.support.Result;
import com.mossle.disk.support.TreeViewNode;
import com.mossle.disk.support.TreeViewNodeBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DiskSpaceService {
    private static Logger logger = LoggerFactory
            .getLogger(DiskSpaceService.class);
    private DiskAclInternalService diskAclInternalService;
    private DiskBaseInternalService diskBaseInternalService;
    private DiskQueryInternalService diskQueryInternalService;
    private DiskFolderService diskFolderService;
    @Resource
    private DiskSpaceManager diskSpaceManager;

    /**
     * 获取空间.
     */
    public DiskSpace findById(Long spaceId) {
        return this.diskSpaceManager.get(spaceId);
    }

    /**
     * 创建个人空间.
     * 
     * c=user, t=user, 个人空间 c=group, t=group, 群组空间 c=group, t=repo, 文档库
     */
    public DiskSpace createUserSpace(String userId) {
        logger.info("create user space : {}", userId);

        return this.diskQueryInternalService.createUserSpace(userId);
    }

    /**
     * 共享空间，群组.
     */
    public DiskSpace createGroupSpace(String userId, String name) {
        logger.info("create group space : {} {}", userId, name);

        String hql = "from DiskSpace where catalog='group' and type='group' and name=?";

        // String hql = "from DiskSpace where catalog='group' and type='group' and name=?0";
        DiskSpace diskSpace = this.diskSpaceManager.findUnique(hql, name);

        if (diskSpace != null) {
            return diskSpace;
        }

        DiskRule diskRule = this.diskAclInternalService.createRule();

        diskSpace = new DiskSpace();
        diskSpace.setName(name);
        diskSpace.setCatalog("group");
        diskSpace.setType("group");
        diskSpace.setCreator(userId);
        diskSpace.setCreateTime(new Date());
        diskSpace.setDiskRule(diskRule);
        diskSpace.setStatus("active");
        this.diskSpaceManager.save(diskSpace);

        // this.diskService.addMember(diskSpace, userId);

        // // admin
        // diskAclInternalService.addPermission("role", "role:admin", diskInfo.getId(),
        // diskAclInternalService.MASK_ALL);
        // // owner
        // diskAclInternalService.addPermission("owner", "owner:" + userId,
        // diskInfo.getId(), diskAclInternalService.MASK_ALL);

        // diskSpace.setDiskRule(diskInfo.getDiskRule());
        // diskSpaceManager.save(diskSpace);
        return diskSpace;
    }

    /**
     * 共享空间，知识库.
     */
    public DiskSpace createRepoSpace(String userId, String name) {
        logger.info("create repo space : {} {}", userId, name);

        String hql = "from DiskSpace where catalog='group' and type='repo' and status='active' and name=?";

        // String hql = "from DiskSpace where catalog='group' and type='repo' and status='active' and name=?0";
        DiskSpace diskSpace = this.diskSpaceManager.findUnique(hql, name);

        if (diskSpace != null) {
            return diskSpace;
        }

        // DiskRule diskRule = new DiskRule();
        // diskRuleManager.save(diskRule);
        diskSpace = new DiskSpace();
        diskSpace.setName(name);
        diskSpace.setCatalog("group");
        diskSpace.setType("repo");
        diskSpace.setCreator(userId);
        diskSpace.setCreateTime(new Date());
        // diskSpace.setDiskRule(diskRule);
        diskSpace.setStatus("active");
        this.diskSpaceManager.save(diskSpace);

        // this.diskService.addMember(diskSpace, userId);

        // 默认根目录
        Result<DiskInfo> result = this.diskBaseInternalService.createRoot(name,
                diskSpace.getId());

        if (result.isFailure()) {
            logger.info("create root folder failure");
            throw new IllegalStateException("create root folder failure");
        }

        DiskInfo diskInfo = result.getData();
        DiskRule diskRule = this.diskAclInternalService
                .createPrivateRule(diskInfo);
        diskInfo.setDiskRule(diskRule);
        this.diskBaseInternalService.save(diskInfo);

        diskSpace.setDiskRule(diskRule);
        diskSpaceManager.save(diskSpace);

        return diskSpace;
    }

    /**
     * 根据用户id返回或创建这个用户的个人文档.
     */
    public DiskSpace findUserSpace(String userId) {
        logger.info("find user space : {}", userId);

        return this.createUserSpace(userId);
    }

    /**
     * 共享空间.
     */
    public List<DiskSpace> findShareSpaces(String userId) {
        logger.info("find share spaces : {}", userId);

        String hql = "select diskSpace from DiskSpace diskSpace join diskSpace.diskMembers diskMember "
                + "where diskSpace.status='active' and diskSpace.catalog='user' and diskMember.userId=?";

        // String hql = "select diskSpace from DiskSpace diskSpace join diskSpace.diskMembers diskMember "
        // + "where diskSpace.status='active' and diskSpace.catalog='user' and diskMember.userId=?0";
        List<DiskSpace> diskSpaces = diskSpaceManager.find(hql, userId);

        return diskSpaces;
    }

    /**
     * 群组空间.
     */
    public List<DiskSpace> findGroupSpaces(String userId) {
        logger.info("find group spaces : {}", userId);

        String hql = "select diskSpace from DiskSpace diskSpace join diskSpace.diskMembers diskMember "
                + "where diskSpace.status='active' and diskSpace.catalog='group' "
                + "and diskSpace.type='group' and diskMember.userId=?";

        // String hql = "select diskSpace from DiskSpace diskSpace join diskSpace.diskMembers diskMember "
        // + "where diskSpace.status='active' and diskSpace.catalog='group' "
        // + "and diskSpace.type='group' and diskMember.userId=?0";
        List<DiskSpace> diskSpaces = diskSpaceManager.find(hql, userId);

        return diskSpaces;
    }

    /**
     * 文档库.
     */
    public List<DiskSpace> findRepoSpaces(String userId) {
        logger.info("find repo spaces : {}", userId);

        String hql = "select diskSpace from DiskSpace diskSpace join diskSpace.diskMembers diskMember "
                + "where diskSpace.status='active' and diskSpace.catalog='group' "
                + "and diskSpace.type='repo' and diskMember.userId=?";

        // String hql = "select diskSpace from DiskSpace diskSpace join diskSpace.diskMembers diskMember "
        // + "where diskSpace.status='active' and diskSpace.catalog='group' "
        // + "and diskSpace.type='repo' and diskMember.userId=?0";
        List<DiskSpace> diskSpaces = diskSpaceManager.find(hql, userId);

        return diskSpaces;
    }

    /**
     * 文档库.
     */

    // public List<DiskSpace> findRepoSpacesAcl(String userId) {
    // logger.info("find repo spaces : {}", userId);

    // String dataSql = "select distinct space from DiskSpace space left join space.diskRule.diskAcls acl "
    // + " where space.status='active' and space.catalog!='user' "
    // + " and (acl.sid in (:sids) or space.creator=:owner) "
    // + " order by space.id ";
    // List<Long> sids = this.diskAclInternalService.findSidsByUser(userId);

    // Map<String, Object> params = new HashMap<String, Object>();
    // params.put("sids", sids);
    // params.put("owner", userId);

    // List<DiskSpace> result = diskSpaceManager.find(dataSql, params);

    // return result;
    // }

    /**
     * 特殊场景下，只有一个默认文档库，就选中这个文档库.
     */
    public DiskSpace findDefaultRepoSpace() {
        logger.info("find default repo space");

        String defaultRepoSpaceName = "default";
        String hql = "from DiskSpace where catalog='group' and type='repo' and status='active' and name=?";

        // String hql = "from DiskSpace where catalog='group' and type='repo' and status='active' and name=?0";
        DiskSpace diskSpace = this.diskSpaceManager.findUnique(hql,
                defaultRepoSpaceName);

        if (diskSpace == null) {
            diskSpace = this.createRepoSpace("system", defaultRepoSpaceName);
        }

        return diskSpace;
    }

    /**
     * 左侧空间菜单.
     */
    public List<TreeViewNode> findTreeView(Long parentId, String parentType,
            String userId) {
        logger.info("find tree view : {} {} {}", parentId, parentType, userId);

        if (parentId == null) {
            // root
            DiskSpace userSpace = this.findUserSpace(userId);
            DiskInfo userSpaceRootFolder = this.diskQueryInternalService
                    .findRootFolderBySpace(userSpace.getId());
            FolderTreeNode userRootSpaceNode = this.diskFolderService
                    .convertJson(userSpaceRootFolder, userId);

            return new TreeViewNodeBuilder()
                    .buildRoot(userRootSpaceNode, false);
        } else if ("folder".equals(parentType)) {
            return new TreeViewNodeBuilder().buildChildren();
        }

        return new TreeViewNodeBuilder().buildChildren();
    }

    /**
     * 菜单，顶级节点.
     */
    public List<TreeViewNode> findTreeRoot(String userId, boolean hideTrash) {
        logger.info("find tree root : {} {}", userId, hideTrash);

        DiskSpace userSpace = this.findUserSpace(userId);
        DiskInfo userSpaceRootFolder = this.diskQueryInternalService
                .findRootFolderBySpace(userSpace.getId());
        FolderTreeNode userRootSpaceNode = this.diskFolderService.convertJson(
                userSpaceRootFolder, userId);

        return new TreeViewNodeBuilder()
                .buildRoot(userRootSpaceNode, hideTrash);
    }

    /**
     * 菜单，共享空间节点.
     */
    public List<TreeViewNode> findTreeSpaces(String userId) {
        logger.info("find tree spaces : {}", userId);

        List<DiskSpace> diskSpaces = this.diskQueryInternalService
                .findGroupSpaces(userId);

        // logger.info("diskSpaces : {}", diskSpaces);
        List<FolderTreeNode> list = new ArrayList<FolderTreeNode>();

        for (DiskSpace diskSpace : diskSpaces) {
            DiskInfo rootFolder = this.diskQueryInternalService
                    .findRootFolderBySpace(diskSpace.getId());
            FolderTreeNode folderTreeNode = this.diskFolderService.convertJson(
                    rootFolder, userId);
            list.add(folderTreeNode);
        }

        return new TreeViewNodeBuilder().buildSpaces(list);
    }

    /**
     * 菜单，空间下文件夹.
     * 
     * 可能不需要
     */
    public List<TreeViewNode> findTreeSpaceFolders(String userId) {
        logger.info("find tree space folders : {}", userId);

        DiskSpace userSpace = this.findUserSpace(userId);
        DiskInfo userSpaceRootFolder = this.diskQueryInternalService
                .findRootFolderBySpace(userSpace.getId());
        FolderTreeNode userRootSpaceNode = this.diskFolderService.convertJson(
                userSpaceRootFolder, userId);

        return new TreeViewNodeBuilder().buildRoot(userRootSpaceNode, false);
    }

    /**
     * 菜单，文件夹下子文件夹.
     */
    public List<TreeViewNode> findTreeFolders(Long folderId, String userId) {
        logger.info("find tree folders : {} {}", folderId, userId);

        DiskSpace userSpace = this.findUserSpace(userId);
        DiskInfo userSpaceRootFolder = this.diskQueryInternalService
                .findRootFolderBySpace(userSpace.getId());
        List<DiskInfo> children = this.findChildrenFolders(folderId, userId);
        List<FolderTreeNode> list = this.diskFolderService.convertJson(
                children, userId);

        return new TreeViewNodeBuilder().buildFolders(list);
    }

    // ~

    /**
     * 根据上级文件夹，搜索子文件夹.
     */
    public List<DiskInfo> findChildrenFolders(Long folderId, String userId) {
        logger.info("findChildrenFolders {} {}", folderId, userId);

        // acl
        if (this.diskAclInternalService.lackPermission(folderId, userId,
                diskAclInternalService.MASK_READ)) {
            logger.info("lack permission : {} {} {} {} {}", folderId, "folder",
                    userId, diskAclInternalService.MASK_READ, "findChildren");
            throw new DiskAclException(folderId, "folder", userId,
                    diskAclInternalService.MASK_READ, "findChildren");
        }

        return this.diskQueryInternalService.findChildrenFolders(folderId,
                userId);
    }

    // ~
    @Resource
    public void setDiskSpaceManager(DiskSpaceManager diskSpaceManager) {
        this.diskSpaceManager = diskSpaceManager;
    }

    @Resource
    public void setDiskAclInternalService(
            DiskAclInternalService diskAclInternalService) {
        this.diskAclInternalService = diskAclInternalService;
    }

    @Resource
    public void setDiskBaseInternalService(
            DiskBaseInternalService diskBaseInternalService) {
        this.diskBaseInternalService = diskBaseInternalService;
    }

    @Resource
    public void setDiskQueryInternalService(
            DiskQueryInternalService diskQueryInternalService) {
        this.diskQueryInternalService = diskQueryInternalService;
    }

    @Resource
    public void setDiskFolderService(DiskFolderService diskFolderService) {
        this.diskFolderService = diskFolderService;
    }
}
