package com.mossle.disk.service;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.page.Page;

import com.mossle.disk.persistence.domain.DiskAcl;
import com.mossle.disk.persistence.domain.DiskInfo;
import com.mossle.disk.persistence.domain.DiskTag;
import com.mossle.disk.service.internal.DiskAclInternalService;
import com.mossle.disk.service.internal.DiskBaseInternalService;
import com.mossle.disk.service.internal.DiskLogInternalService;
import com.mossle.disk.service.internal.DiskQueryInternalService;
import com.mossle.disk.service.internal.DiskTagInternalService;
import com.mossle.disk.support.DiskAclException;
import com.mossle.disk.support.FolderTreeNode;
import com.mossle.disk.support.Result;
import com.mossle.disk.support.TreeNode;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DiskFolderService {
    private static Logger logger = LoggerFactory
            .getLogger(DiskFolderService.class);
    private DiskLogInternalService diskLogInternalService;
    private DiskAclInternalService diskAclInternalService;
    private DiskBaseInternalService diskBaseInternalService;
    private DiskTagInternalService diskTagInternalService;
    private DiskQueryInternalService diskQueryInternalService;
    private JsonMapper jsonMapper = new JsonMapper();

    /**
     * 0101 新建文件夹.
     */
    public DiskInfo createFolder(Long folderId, String userId, String name,
            String description) throws Exception {
        logger.info("createFolder {} {} {}", folderId, userId, name);

        // acl
        if (this.diskAclInternalService.lackPermission(folderId, userId,
                diskAclInternalService.MASK_CREATE)) {
            logger.info("lack permission : {} {} {} {} {}", folderId, "folder",
                    userId, diskAclInternalService.MASK_CREATE, "createFolder");
            throw new DiskAclException(folderId, "folder", userId,
                    diskAclInternalService.MASK_CREATE, "createFolder");
        }

        // validate
        if (StringUtils.isBlank(name)) {
            logger.info("name cannot be blank");

            return null;
        } else {
            name = StringUtils.trim(name);
        }

        if (description == null) {
            description = "";
        } else {
            description = StringUtils.trim(description);
        }

        Result<DiskInfo> result = this.diskBaseInternalService.create(userId,
                name, 0, "", "folder", 0, folderId);

        if (result.isFailure()) {
            logger.info("create folder failure : {}", result.getMessage());

            return null;
        }

        DiskInfo folder = result.getData();
        // log
        this.diskLogInternalService.recordLog(folder, userId,
                DiskLogInternalService.CATALOG_CREATE_FOLDER);

        return folder;
    }

    public void initFolderStructure(Long folderId, String userId,
            String templateId) throws Exception {
        InputStream is = DiskFolderService.class.getClassLoader()
                .getResourceAsStream(templateId);
        String json = IOUtils.toString(is);

        // logger.info("json : {}", json);
        TreeNode root = jsonMapper.fromJson(json, TreeNode.class);

        // logger.info("root : {}", root.getChildren());
        Result<DiskInfo> result = diskBaseInternalService.findActive(folderId);

        if (result.isFailure()) {
            return;
        }

        DiskInfo folder = result.getData();

        for (TreeNode child : root.getChildren()) {
            this.visitTreeNode(child, folder, userId);
        }
    }

    public void visitTreeNode(TreeNode treeNode, DiskInfo parentFolder,
            String userId) throws Exception {
        String name = treeNode.getName();
        Long folderId = parentFolder.getId();
        Result<DiskInfo> result = this.diskBaseInternalService.create(userId,
                name, 0, "", "folder", 0, folderId);

        if (result.isFailure()) {
            logger.info("create folder failure : {}", result.getMessage());

            // TODO: dumplicated?
            return;
        }

        DiskInfo folder = result.getData();

        for (TreeNode child : treeNode.getChildren()) {
            this.visitTreeNode(child, folder, userId);
        }
    }

    /**
     * 0102 删除文件夹.
     */
    public DiskInfo removeFolder(Long folderId, String userId) {
        logger.info("removeFolder {} {} {}", folderId, userId);

        // acl
        if (this.diskAclInternalService.lackPermission(folderId, userId,
                diskAclInternalService.MASK_DELETE)) {
            logger.info("lack permission : {} {} {} {} {}", folderId, "folder",
                    userId, diskAclInternalService.MASK_DELETE, "removeFolder");
            throw new DiskAclException(folderId, "folder", userId,
                    diskAclInternalService.MASK_DELETE, "removeFolder");
        }

        Result<DiskInfo> result = this.diskBaseInternalService.remove(folderId,
                userId);

        if (result.isSuccess()) {
            DiskInfo diskInfo = result.getData();
            // log
            this.diskLogInternalService.recordLog(diskInfo, userId,
                    DiskLogInternalService.CATALOG_REMOVE_FOLDER);

            return diskInfo;
        }

        return null;
    }

    /**
     * 0103 详情.
     */
    public DiskInfo findFolder(Long folderId, String userId) {
        logger.info("findFolder {} {}", folderId, userId);

        // acl
        if (this.diskAclInternalService.lackPermission(folderId, userId,
                diskAclInternalService.MASK_READ)) {
            logger.info("lack permission : {} {} {} {} {}", folderId, "folder",
                    userId, diskAclInternalService.MASK_READ, "findFolder");
            throw new DiskAclException(folderId, "folder", userId,
                    diskAclInternalService.MASK_READ, "findFolder");
        }

        Result<DiskInfo> result = this.diskBaseInternalService
                .findById(folderId);

        if (result.isFailure()) {
            return null;
        }

        return result.getData();
    }

    /**
     * 0104 重命名.
     */
    public DiskInfo rename(Long folderId, String userId, String name,
            String description) {
        logger.info("rename : {} {} {} {}", folderId, userId, name, description);

        // acl
        if (this.diskAclInternalService.lackPermission(folderId, userId,
                diskAclInternalService.MASK_EDIT)) {
            logger.info("lack permission : {} {} {} {} {}", folderId, "folder",
                    userId, diskAclInternalService.MASK_EDIT, "rename");
            throw new DiskAclException(folderId, "folder", userId,
                    diskAclInternalService.MASK_EDIT, "rename");
        }

        Result<DiskInfo> result = this.diskBaseInternalService.rename(folderId,
                userId, name);

        if (result.isFailure()) {
            return null;
        }

        return result.getData();
    }

    /**
     * 0105 移动.
     */
    public DiskInfo move(Long folderId, String userId, Long parentId) {
        logger.info("move : {} {} {}", folderId, userId, parentId);

        // acl
        if (this.diskAclInternalService.lackPermission(folderId, userId,
                diskAclInternalService.MASK_DELETE)) {
            logger.info("lack permission : {} {} {} {} {}", folderId, "folder",
                    userId, diskAclInternalService.MASK_DELETE, "move");
            throw new DiskAclException(folderId, "folder", userId,
                    diskAclInternalService.MASK_DELETE, "move");
        }

        if (this.diskAclInternalService.lackPermission(parentId, userId,
                diskAclInternalService.MASK_CREATE)) {
            logger.info("lack permission : {} {} {} {} {}", parentId, "folder",
                    userId, diskAclInternalService.MASK_CREATE, "move");
            throw new DiskAclException(parentId, "folder", userId,
                    diskAclInternalService.MASK_CREATE, "move");
        }

        Result<DiskInfo> result = this.diskBaseInternalService.move(folderId,
                userId, parentId);

        if (result.isFailure()) {
            return null;
        }

        return result.getData();
    }

    /**
     * 0106 复制.
     */
    public DiskInfo copyFolder(Long folderId, String userId, Long parentId) {
        logger.info("copy : {} {} {}", folderId, userId, parentId);

        // acl
        if (this.diskAclInternalService.lackPermission(folderId, userId,
                diskAclInternalService.MASK_COPY)) {
            logger.info("lack permission : {} {} {} {} {}", folderId, "folder",
                    userId, diskAclInternalService.MASK_COPY, "copy");
            throw new DiskAclException(folderId, "folder", userId,
                    diskAclInternalService.MASK_COPY, "copy");
        }

        if (this.diskAclInternalService.lackPermission(parentId, userId,
                diskAclInternalService.MASK_CREATE)) {
            logger.info("lack permission : {} {} {} {} {}", parentId, "folder",
                    userId, diskAclInternalService.MASK_CREATE, "copy");
            throw new DiskAclException(parentId, "folder", userId,
                    diskAclInternalService.MASK_CREATE, "copy");
        }

        Result<DiskInfo> result = this.diskBaseInternalService.copy(folderId,
                userId, parentId);

        if (result.isFailure()) {
            return null;
        }

        // this.diskVersionService.createVersion(diskInfo);
        return result.getData();
    }

    /**
     * 0107 恢复.
     */
    public DiskInfo recover(Long folderId, String userId) {
        logger.info("recover : {} {}", folderId, userId);

        Long parentId = null;
        Result<DiskInfo> result = this.diskBaseInternalService
                .findById(folderId);

        if (result.isFailure()) {
            return null;
        }

        DiskInfo folder = result.getData();

        if (folder.getDiskInfo() != null) {
            parentId = folder.getDiskInfo().getId();
        }

        // acl
        if (this.diskAclInternalService.lackPermission(folderId, userId,
                diskAclInternalService.MASK_READ)) {
            logger.info("lack permission : {} {} {} {} {}", folderId, "folder",
                    userId, diskAclInternalService.MASK_READ, "recover");
            throw new DiskAclException(folderId, "folder", userId,
                    diskAclInternalService.MASK_READ, "recover");
        }

        if (parentId != null) {
            if (this.diskAclInternalService.lackPermission(parentId, userId,
                    diskAclInternalService.MASK_CREATE)) {
                logger.info("lack permission : {} {} {} {} {}", parentId,
                        "folder", userId, diskAclInternalService.MASK_CREATE,
                        "recover");
                throw new DiskAclException(parentId, "folder", userId,
                        diskAclInternalService.MASK_CREATE, "recover");
            }
        }

        Result<DiskInfo> result2 = this.diskBaseInternalService.recover(
                folderId, userId, parentId);

        if (result2.isFailure()) {
            return null;
        }

        return result2.getData();
    }

    /**
     * 0108 彻底删除.
     */
    public DiskInfo delete(Long folderId, String userId) {
        logger.info("delete : {} {} {}", folderId, userId);

        // acl
        if (this.diskAclInternalService.lackPermission(folderId, userId,
                diskAclInternalService.MASK_DELETE)) {
            logger.info("lack permission : {} {} {} {} {}", folderId, "folder",
                    userId, diskAclInternalService.MASK_DELETE, "delete");
            throw new DiskAclException(folderId, "folder", userId,
                    diskAclInternalService.MASK_DELETE, "delete");
        }

        Result<DiskInfo> result = this.diskBaseInternalService.delete(folderId,
                userId);

        if (result.isFailure()) {
            return null;
        }

        return result.getData();
    }

    /**
     * 0109 获取标签.
     */
    public List<DiskTag> findTags(Long folderId, String userId) {
        logger.info("findTags : {} {}", folderId, userId);

        // acl
        if (this.diskAclInternalService.lackPermission(folderId, userId,
                diskAclInternalService.MASK_READ)) {
            logger.info("lack permission : {} {} {} {} {}", folderId, "folder",
                    userId, diskAclInternalService.MASK_READ, "findTags");
            throw new DiskAclException(folderId, "folder", userId,
                    diskAclInternalService.MASK_READ, "findTags");
        }

        List<DiskTag> diskTags = this.diskTagInternalService.findTags(folderId);

        return diskTags;
    }

    /**
     * 0110 保存标签.
     */
    public DiskInfo saveTags(Long folderId, String userId, String tags) {
        logger.info("saveTags : {} {} {}", folderId, userId, tags);

        // acl
        if (this.diskAclInternalService.lackPermission(folderId, userId,
                diskAclInternalService.MASK_EDIT)) {
            logger.info("lack permission : {} {} {} {} {}", folderId, "folder",
                    userId, diskAclInternalService.MASK_EDIT, "saveTags");
            throw new DiskAclException(folderId, "folder", userId,
                    diskAclInternalService.MASK_EDIT, "saveTags");
        }

        Result<DiskInfo> result = this.diskTagInternalService.updateTags(
                folderId, userId, tags);

        if (result.isFailure()) {
            return null;
        }

        DiskInfo diskInfo = result.getData();

        // log
        this.diskLogInternalService.recordLogEditTag(diskInfo, userId,
                DiskLogInternalService.CATALOG_EDIT_TAGS, tags);

        return diskInfo;
    }

    /**
     * 0211 获取权限.
     */
    public List<DiskAcl> findPermissions(Long folderId, String userId) {
        return this.diskAclInternalService.findPermissions(folderId, userId);
    }

    /**
     * 0112 添加权限.
     */
    public DiskInfo addPermission(Long folderId, String userId,
            String memberId, int mask) {
        logger.info("addPermission : {} {} {} {}", folderId, userId, memberId,
                mask);

        // acl
        if (this.diskAclInternalService.lackPermission(folderId, userId,
                diskAclInternalService.MASK_READ)) {
            logger.info("lack permission : {} {} {} {} {}", folderId, "folder",
                    userId, diskAclInternalService.MASK_READ, "addPermission");
            throw new DiskAclException(folderId, "folder", userId,
                    diskAclInternalService.MASK_READ, "addPermission");
        }

        Result<DiskInfo> result = this.diskAclInternalService.addPermission(
                folderId, userId, memberId, mask);

        if (result.isFailure()) {
            return null;
        }

        return result.getData();
    }

    /**
     * 0112 删除权限.
     */
    public Result<DiskInfo> removePermission(Long folderId, String userId,
            Long aclId) {
        // TODO: 应该是自己自己添加的权限才能删除吧
        // TODO: 或者只要有修改权限，就能删除所有权限
        // 目前是只要有read权限，就可以删除所有权限，能量有点儿大，回头细化
        logger.info("removePermission : {} {} {}", folderId, userId, aclId);

        // acl
        if (this.diskAclInternalService.lackPermission(folderId, userId,
                diskAclInternalService.MASK_READ)) {
            logger.info("lack permission : {} {} {} {} {}", folderId, "folder",
                    userId, diskAclInternalService.MASK_READ,
                    "removePermission");
            throw new DiskAclException(folderId, "folder", userId,
                    diskAclInternalService.MASK_READ, "removePermission");
        }

        Result<DiskInfo> result = this.diskAclInternalService.removePermission(
                folderId, userId, aclId);

        return result;
    }

    // ~

    /**
     * 0113 根据文件夹查询子文件.
     */
    public Page findChildren(Long folderId, int pageNo, int pageSize,
            String orderBy, String order, String userId) {
        logger.info("findChildren {} {}", folderId, userId);

        // acl
        if (this.diskAclInternalService.lackPermission(folderId, userId,
                diskAclInternalService.MASK_READ)) {
            logger.info("lack permission : {} {} {} {} {}", folderId, "folder",
                    userId, diskAclInternalService.MASK_READ, "findChildren");
            throw new DiskAclException(folderId, "folder", userId,
                    diskAclInternalService.MASK_READ, "findChildren");
        }

        Page page = this.diskQueryInternalService.findChildren(folderId,
                pageNo, pageSize, orderBy, order, userId);

        return page;
    }

    /**
     * 0114 文件夹树形.
     */
    public String findFolderTree(Long folderId, String userId) throws Exception {
        logger.info("findFolderTree : {} {}", folderId, userId);

        // acl
        if (this.diskAclInternalService.lackPermission(folderId, userId,
                diskAclInternalService.MASK_READ)) {
            logger.info("lack permission : {} {} {} {} {}", folderId, "folder",
                    userId, diskAclInternalService.MASK_READ, "findFolderTree");
            throw new DiskAclException(folderId, "folder", userId,
                    diskAclInternalService.MASK_READ, "findFolderTree");
        }

        // DiskInfo folder = this.findRootFolder(folderOrFileId);
        Result<DiskInfo> result = this.diskBaseInternalService
                .findActive(folderId);

        if (result.isFailure()) {
            return "[]";
        }

        DiskInfo folder = result.getData();

        List<FolderTreeNode> nodes = new ArrayList<FolderTreeNode>();

        FolderTreeNode root = new FolderTreeNode();
        root.setId(Long.toString(folder.getId()));
        root.setName("文档空间");
        root.setOpen(true);
        nodes.add(root);

        List<DiskInfo> folders = this.diskQueryInternalService
                .findChildrenFolders(folder.getId(), userId);
        root.setChildren(this.convertJson(folders, userId));

        return jsonMapper.toJson(nodes);
    }

    /**
     * 0115 获取目录路径.
     */
    public List<DiskInfo> findFolderPath(Long folderId, String userId) {
        logger.info("findFolderPath : {} {}", folderId, userId);

        Result<DiskInfo> result = this.diskBaseInternalService
                .findActive(folderId);
        DiskInfo folder = result.getData();

        if (result.isFailure()) {
            logger.info("cannot find folder : {}", folderId);

            return Collections.emptyList();
        }

        List<DiskInfo> folders = new ArrayList<DiskInfo>();

        DiskInfo current = folder;

        while (current != null) {
            folders.add(current);
            current = current.getDiskInfo();
        }

        Collections.reverse(folders);

        return folders;
    }

    /**
     * 0116 压缩下载.
     */
    public void download(Long folderId, String userId) {
        logger.info("download : {} {}", folderId, userId);

        // acl
        if (this.diskAclInternalService.lackPermission(folderId, userId,
                diskAclInternalService.MASK_DOWNLOAD)) {
            logger.info("lack permission : {} {} {} {} {}", folderId, "folder",
                    userId, diskAclInternalService.MASK_DOWNLOAD, "download");
            throw new DiskAclException(folderId, "folder", userId,
                    diskAclInternalService.MASK_DOWNLOAD, "download");
        }
    }

    // ~

    /**
     * 根据当前文件夹或文件，获取根文件夹.
     */
    public DiskInfo findRootFolder(Long folderOrFileId) {
        Result<DiskInfo> result = diskBaseInternalService
                .findActive(folderOrFileId);

        if (result.isFailure()) {
            return null;
        }

        DiskInfo diskInfo = result.getData();

        DiskInfo folder = null;

        while (true) {
            DiskInfo parent = diskInfo.getDiskInfo();

            if (parent == null) {
                folder = diskInfo;

                break;
            }

            diskInfo = parent;
        }

        return folder;
    }

    public List<FolderTreeNode> convertJson(List<DiskInfo> diskInfos,
            String userId) {
        if (diskInfos.isEmpty()) {
            return Collections.emptyList();
        }

        // StringBuilder buff = new StringBuilder();
        // buff.append("[");
        List<FolderTreeNode> list = new ArrayList<FolderTreeNode>();

        for (DiskInfo diskInfo : diskInfos) {
            if (!"folder".equals(diskInfo.getType())) {
                continue;
            }

            if (!diskAclInternalService.hasPermission(diskInfo.getId(), userId)) {
                continue;
            }

            // buff.append(this.convertJson(diskInfo, userId)).append(",");
            list.add(this.convertJson(diskInfo, userId));
        }

        // if (buff.length() > 1) {
        // buff.deleteCharAt(buff.length() - 1);
        // }

        // buff.append("]");

        // return buff.toString();
        return list;
    }

    public FolderTreeNode convertJson(DiskInfo diskInfo, String userId) {
        if (!"folder".equals(diskInfo.getType())) {
            return null;
        }

        FolderTreeNode node = new FolderTreeNode();
        node.setId(Long.toString(diskInfo.getId()));
        node.setName(diskInfo.getName());

        List<DiskInfo> diskInfos = this.diskQueryInternalService
                .findChildrenFolders(diskInfo.getId(), userId);

        List<FolderTreeNode> children = this.convertJson(diskInfos, userId);

        if (children != null) {
            // buff.append(",\"open\":true,\"children\":").append(children);
            node.setOpen(true);
            node.setChildren(children);
        } else {
            // buff.append(",\"open\":false");
            node.setOpen(false);
            node.setChildren(Collections.<FolderTreeNode> emptyList());
        }

        return node;
    }

    /**
     * 0115 获取目录路径.
     */
    public List<DiskInfo> findFolderPathInner(Long folderId) {
        Result<DiskInfo> result = this.diskBaseInternalService
                .findActive(folderId);

        if (result.isFailure()) {
            logger.info("cannot find current : {}", folderId);

            return Collections.emptyList();
        }

        DiskInfo current = result.getData();

        List<DiskInfo> folders = new ArrayList<DiskInfo>();

        while (current != null) {
            current = current.getDiskInfo();

            if (current == null) {
                break;
            }

            folders.add(current);
        }

        Collections.reverse(folders);

        return folders;
    }

    // ~
    @Resource
    public void setDiskLogInternalService(
            DiskLogInternalService diskLogInternalService) {
        this.diskLogInternalService = diskLogInternalService;
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
    public void setDiskTagInternalService(
            DiskTagInternalService diskTagInternalService) {
        this.diskTagInternalService = diskTagInternalService;
    }

    @Resource
    public void setDiskQueryInternalService(
            DiskQueryInternalService diskQueryInternalService) {
        this.diskQueryInternalService = diskQueryInternalService;
    }
}
