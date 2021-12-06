package com.mossle.disk.service;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.disk.persistence.domain.DiskAcl;
import com.mossle.disk.persistence.domain.DiskInfo;
import com.mossle.disk.persistence.domain.DiskTag;
import com.mossle.disk.service.internal.DiskAclInternalService;
import com.mossle.disk.service.internal.DiskBaseInternalService;
import com.mossle.disk.service.internal.DiskLogInternalService;
import com.mossle.disk.service.internal.DiskShareInternalService;
import com.mossle.disk.service.internal.DiskTagInternalService;
import com.mossle.disk.support.DiskAclException;
import com.mossle.disk.support.Result;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DiskInfoService {
    private static Logger logger = LoggerFactory
            .getLogger(DiskInfoService.class);
    private DiskAclInternalService diskAclInternalService;
    private DiskBaseInternalService diskBaseInternalService;
    private DiskLogInternalService diskLogInternalService;
    private DiskTagInternalService diskTagInternalService;
    private DiskShareInternalService diskShareInternalService;

    /**
     * 0202 删除.
     */
    public Result<DiskInfo> remove(Long fileId, String userId) {
        logger.info("remove {} {}", fileId, userId);

        // acl
        if (this.diskAclInternalService.lackPermission(fileId, userId,
                diskAclInternalService.MASK_DELETE)) {
            logger.info("lack permission : {} {} {} {} {}", fileId, "file",
                    userId, diskAclInternalService.MASK_DELETE, "removeFile");
            throw new DiskAclException(fileId, "file", userId,
                    diskAclInternalService.MASK_DELETE, "removeFile");
        }

        Result<DiskInfo> result = this.diskBaseInternalService.remove(fileId,
                userId);

        if (result.isSuccess()) {
            DiskInfo diskInfo = result.getData();
            // log
            this.diskLogInternalService.recordLog(diskInfo, userId,
                    DiskLogInternalService.CATALOG_REMOVE_FILE);

            return result;
        }

        return result;
    }

    /**
     * 0203 详情.
     */
    public Result<DiskInfo> findById(Long fileId, String userId) {
        logger.info("findFile {} {}", fileId, userId);

        // acl
        if (this.diskAclInternalService.lackPermission(fileId, userId,
                diskAclInternalService.MASK_READ)) {
            logger.info("lack permission : {} {} {} {} {}", fileId, "file",
                    userId, diskAclInternalService.MASK_READ, "findFile");
            throw new DiskAclException(fileId, "file", userId,
                    diskAclInternalService.MASK_READ, "findFile");
        }

        Result<DiskInfo> result = this.diskBaseInternalService
                .findActive(fileId);

        if (result.isFailure()) {
            return result;
        }

        return result;
    }

    /**
     * 0204 重命名.
     */
    public Result<DiskInfo> rename(Long fileId, String userId, String name) {
        logger.info("rename : {} {} {}", fileId, userId, name);

        // acl
        if (this.diskAclInternalService.lackPermission(fileId, userId,
                diskAclInternalService.MASK_EDIT)) {
            logger.info("lack permission : {} {} {} {} {}", fileId, "file",
                    userId, diskAclInternalService.MASK_EDIT, "rename");
            throw new DiskAclException(fileId, "file", userId,
                    diskAclInternalService.MASK_EDIT, "rename");
        }

        Result<DiskInfo> result = this.diskBaseInternalService.rename(fileId,
                userId, name);

        if (result.isFailure()) {
            return result;
        }

        return result;
    }

    /**
     * 0205 移动.
     */
    public Result<DiskInfo> move(Long fileId, String userId, Long parentId) {
        logger.info("move : {} {} {}", fileId, userId, parentId);

        // acl
        if (this.diskAclInternalService.lackPermission(fileId, userId,
                diskAclInternalService.MASK_DELETE)) {
            logger.info("lack permission : {} {} {} {} {}", fileId, "file",
                    userId, diskAclInternalService.MASK_DELETE, "move");
            throw new DiskAclException(fileId, "file", userId,
                    diskAclInternalService.MASK_DELETE, "move");
        }

        if (this.diskAclInternalService.lackPermission(parentId, userId,
                diskAclInternalService.MASK_CREATE)) {
            logger.info("lack permission : {} {} {} {} {}", parentId, "file",
                    userId, diskAclInternalService.MASK_CREATE, "move");
            throw new DiskAclException(parentId, "file", userId,
                    diskAclInternalService.MASK_CREATE, "move");
        }

        Result<DiskInfo> result = this.diskBaseInternalService.move(fileId,
                userId, parentId);

        if (result.isFailure()) {
            return result;
        }

        DiskInfo diskInfo = result.getData();
        this.diskLogInternalService.recordLog(diskInfo, userId,
                DiskLogInternalService.CATALOG_MOVE);

        return result;
    }

    /**
     * 0206 复制.
     */
    public Result<DiskInfo> copy(Long fileId, String userId, Long parentId) {
        logger.info("copy : {} {} {}", fileId, userId, parentId);

        // acl
        if (this.diskAclInternalService.lackPermission(fileId, userId,
                diskAclInternalService.MASK_COPY)) {
            logger.info("lack permission : {} {} {} {} {}", fileId, "file",
                    userId, diskAclInternalService.MASK_COPY, "copy");
            throw new DiskAclException(fileId, "file", userId,
                    diskAclInternalService.MASK_COPY, "copy");
        }

        if (this.diskAclInternalService.lackPermission(parentId, userId,
                diskAclInternalService.MASK_CREATE)) {
            logger.info("lack permission : {} {} {} {} {}", parentId, "folder",
                    userId, diskAclInternalService.MASK_CREATE, "copy");
            throw new DiskAclException(parentId, "folder", userId,
                    diskAclInternalService.MASK_CREATE, "copy");
        }

        Result<DiskInfo> result = this.diskBaseInternalService.copy(fileId,
                userId, parentId);

        if (result.isFailure()) {
            return result;
        }

        DiskInfo diskInfo = result.getData();

        this.diskLogInternalService.recordLog(diskInfo, userId,
                DiskLogInternalService.CATALOG_COPY);

        return result;
    }

    /**
     * 链接.
     */
    public Result<DiskInfo> link(Long fileId, String userId, Long parentId) {
        logger.info("link : {} {} {}", fileId, userId, parentId);

        // acl
        if (this.diskAclInternalService.lackPermission(fileId, userId,
                diskAclInternalService.MASK_COPY)) {
            logger.info("lack permission : {} {} {} {} {}", fileId, "file",
                    userId, diskAclInternalService.MASK_READ, "link");
            throw new DiskAclException(fileId, "file", userId,
                    diskAclInternalService.MASK_READ, "link");
        }

        if (this.diskAclInternalService.lackPermission(parentId, userId,
                diskAclInternalService.MASK_CREATE)) {
            logger.info("lack permission : {} {} {} {} {}", parentId, "folder",
                    userId, diskAclInternalService.MASK_CREATE, "link");
            throw new DiskAclException(parentId, "folder", userId,
                    diskAclInternalService.MASK_CREATE, "link");
        }

        Result<DiskInfo> result = this.diskBaseInternalService.link(fileId,
                userId, parentId);

        if (result.isFailure()) {
            return result;
        }

        DiskInfo diskInfo = result.getData();

        this.diskLogInternalService.recordLog(diskInfo, userId,
                DiskLogInternalService.CATALOG_LINK);

        return result;
    }

    /**
     * 0207 恢复.
     */
    public Result<DiskInfo> recover(Long fileId, String userId, Long parentId) {
        logger.info("recover : {} {} {}", fileId, userId, parentId);

        if (parentId == null) {
            Result<DiskInfo> result = diskBaseInternalService.findById(fileId);

            if (result.isFailure()) {
                return result;
            }

            DiskInfo file = result.getData();

            if (file != null) {
                parentId = file.getOriginalParentId();
            }
        }

        // acl
        if (this.diskAclInternalService.lackPermission(fileId, userId,
                diskAclInternalService.MASK_READ)) {
            logger.info("lack permission : {} {} {} {} {}", fileId, "file",
                    userId, diskAclInternalService.MASK_READ, "recover");
            throw new DiskAclException(fileId, "file", userId,
                    diskAclInternalService.MASK_READ, "recover");
        }

        if (this.diskAclInternalService.lackPermission(parentId, userId,
                diskAclInternalService.MASK_CREATE)) {
            logger.info("lack permission : {} {} {} {} {}", parentId, "folder",
                    userId, diskAclInternalService.MASK_CREATE, "recover");
            throw new DiskAclException(parentId, "folder", userId,
                    diskAclInternalService.MASK_CREATE, "recover");
        }

        Result<DiskInfo> result = this.diskBaseInternalService.recover(fileId,
                userId, parentId);

        if (result.isFailure()) {
            return result;
        }

        return result;
    }

    /**
     * 0208 彻底删除.
     */
    public Result<DiskInfo> delete(Long fileId, String userId) {
        logger.info("delete : {} {}", fileId, userId);

        // acl
        if (this.diskAclInternalService.lackPermission(fileId, userId,
                diskAclInternalService.MASK_DELETE)) {
            logger.info("lack permission : {} {} {} {} {}", fileId, "file",
                    userId, diskAclInternalService.MASK_DELETE, "delete");
            throw new DiskAclException(fileId, "file", userId,
                    diskAclInternalService.MASK_DELETE, "delete");
        }

        Result<DiskInfo> result = this.diskBaseInternalService.delete(fileId,
                userId);

        if (result.isFailure()) {
            return result;
        }

        return result;
    }

    /**
     * 0209 获取标签.
     */
    public List<DiskTag> findTags(Long fileId, String userId) {
        logger.info("findTags : {} {}", fileId, userId);

        // acl
        if (this.diskAclInternalService.lackPermission(fileId, userId,
                diskAclInternalService.MASK_READ)) {
            logger.info("lack permission : {} {} {} {} {}", fileId, "file",
                    userId, diskAclInternalService.MASK_READ, "findTags");
            throw new DiskAclException(fileId, "file", userId,
                    diskAclInternalService.MASK_READ, "findTags");
        }

        List<DiskTag> diskTags = this.diskTagInternalService.findTags(fileId);

        return diskTags;
    }

    /**
     * 0210 保存标签.
     */
    public Result<DiskInfo> saveTags(Long fileId, String userId, String tags) {
        logger.info("saveTags : {} {} {}", fileId, userId, tags);

        // acl
        if (this.diskAclInternalService.lackPermission(fileId, userId,
                diskAclInternalService.MASK_EDIT)) {
            logger.info("lack permission : {} {} {} {} {}", fileId, "file",
                    userId, diskAclInternalService.MASK_EDIT, "saveTags");
            throw new DiskAclException(fileId, "file", userId,
                    diskAclInternalService.MASK_EDIT, "saveTags");
        }

        Result<DiskInfo> result = this.diskTagInternalService.updateTags(
                fileId, userId, tags);

        if (result.isFailure()) {
            return result;
        }

        DiskInfo diskInfo = result.getData();

        // log
        this.diskLogInternalService.recordLogEditTag(diskInfo, userId,
                DiskLogInternalService.CATALOG_EDIT_TAGS, tags);

        return result;
    }

    /**
     * 0211 获取权限.
     */
    public List<DiskAcl> findPermissions(Long fileId, String userId) {
        return this.diskAclInternalService.findPermissions(fileId, userId);
    }

    /**
     * 0212 添加权限.
     */
    public Result<DiskInfo> addPermission(Long fileId, String userId,
            String memberId, int mask) {
        logger.info("addPermission : {} {} {} {}", fileId, userId, memberId,
                mask);

        // acl
        if (this.diskAclInternalService.lackPermission(fileId, userId,
                diskAclInternalService.MASK_READ)) {
            logger.info("lack permission : {} {} {} {} {}", fileId, "file",
                    userId, diskAclInternalService.MASK_READ, "addPermission");
            throw new DiskAclException(fileId, "fild", userId,
                    diskAclInternalService.MASK_READ, "addPermission");
        }

        Result<DiskInfo> result = this.diskAclInternalService.addPermission(
                fileId, userId, memberId, mask);

        if (result.isFailure()) {
            return result;
        }

        diskShareInternalService.addMember(fileId, userId, memberId, "user");

        return result;
    }

    // /**
    // * 0212 删除权限.
    // */
    // public void removePermission(Long fileId, String userId, String memberId) {
    // // TODO: 应该是自己自己添加的权限才能删除吧
    // // TODO: 或者只要有修改权限，就能删除所有权限
    // // 目前是只要有read权限，就可以删除所有权限，能量有点儿大，回头细化
    // logger.info("removePermission : {} {} {}", fileId, userId, memberId);

    // // acl
    // if (this.diskAclInternalService.lackPermission(fileId, userId,
    // diskAclInternalService.MASK_READ)) {
    // logger.info("lack permission : {} {} {} {} {}", fileId, "file",
    // userId, diskAclInternalService.MASK_READ,
    // "removePermission");
    // throw new DiskAclException(fileId, "file", userId,
    // diskAclInternalService.MASK_READ, "removePermission");
    // }

    // String entityCatalog = "user";
    // String entityRef = "user:" + memberId;
    // this.diskAclInternalService.removePermission(entityCatalog, entityRef,
    // fileId);
    // }

    // /**
    // * 0212 删除权限.
    // */
    // public void removePermission(Long fileId, String userId,
    // String entityCatalog, String entityRef) {
    // // TODO: 应该是自己自己添加的权限才能删除吧
    // // TODO: 或者只要有修改权限，就能删除所有权限
    // // 目前是只要有read权限，就可以删除所有权限，能量有点儿大，回头细化
    // logger.info("removePermission : {} {} {} {}", fileId, userId,
    // entityCatalog, entityRef);

    // // acl
    // if (this.diskAclInternalService.lackPermission(fileId, userId,
    // diskAclInternalService.MASK_READ)) {
    // logger.info("lack permission : {} {} {} {} {}", fileId, "file",
    // userId, diskAclInternalService.MASK_READ,
    // "removePermission");
    // throw new DiskAclException(fileId, "file", userId,
    // diskAclInternalService.MASK_READ, "removePermission");
    // }

    // this.diskAclInternalService.removePermission(entityCatalog, entityRef,
    // fileId);
    // }

    /**
     * 0212 删除权限.
     */
    public Result<DiskInfo> removePermission(Long fileId, String userId,
            Long aclId) {
        // TODO: 应该是自己自己添加的权限才能删除吧
        // TODO: 或者只要有修改权限，就能删除所有权限
        // 目前是只要有read权限，就可以删除所有权限，能量有点儿大，回头细化
        logger.info("removePermission : {} {} {}", fileId, userId, aclId);

        // acl
        if (this.diskAclInternalService.lackPermission(fileId, userId,
                diskAclInternalService.MASK_READ)) {
            logger.info("lack permission : {} {} {} {} {}", fileId, "file",
                    userId, diskAclInternalService.MASK_READ,
                    "removePermission");
            throw new DiskAclException(fileId, "file", userId,
                    diskAclInternalService.MASK_READ, "removePermission");
        }

        Result<DiskInfo> result = this.diskAclInternalService.removePermission(
                fileId, userId, aclId);

        if (result.isFailure()) {
            return result;
        }

        try {
            DiskAcl diskAcl = diskAclInternalService.findAcl(aclId);
            String memberId = diskAcl.getDiskSid().getValue();
            String memberCatalog = diskAcl.getDiskSid().getCatalog();
            diskShareInternalService.removeMember(fileId, userId, memberId,
                    memberCatalog);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return result;
    }

    /**
     * 修改密级.
     */
    public Result<DiskInfo> updateSecurityLevel(long infoId, String userId,
            String securityLevel) {
        logger.info("updateSecurityLevel : {} {}", infoId, securityLevel);

        // acl
        if (this.diskAclInternalService.lackPermission(infoId, userId,
                diskAclInternalService.MASK_EDIT)) {
            logger.info("lack permission : {} {} {} {} {}", infoId, "file",
                    userId, diskAclInternalService.MASK_EDIT,
                    "updateSecurityLevel");
            throw new DiskAclException(infoId, "file", userId,
                    diskAclInternalService.MASK_EDIT, "updateSecurityLevel");
        }

        Result<DiskInfo> result = diskBaseInternalService.findActive(infoId);

        if (result.isFailure()) {
            return result;
        }

        DiskInfo diskInfo = result.getData();
        diskInfo.setSecurityLevel(securityLevel);
        diskBaseInternalService.save(diskInfo);

        return Result.success(diskInfo);
    }

    public Result<DiskInfo> save(DiskInfo diskInfo) {
        diskBaseInternalService.save(diskInfo);

        return Result.success(diskInfo);
    }

    // ~
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
    public void setDiskLogInternalService(
            DiskLogInternalService diskLogInternalService) {
        this.diskLogInternalService = diskLogInternalService;
    }

    @Resource
    public void setDiskTagInternalService(
            DiskTagInternalService diskTagInternalService) {
        this.diskTagInternalService = diskTagInternalService;
    }

    @Resource
    public void setDiskShareInternalService(
            DiskShareInternalService diskShareInternalService) {
        this.diskShareInternalService = diskShareInternalService;
    }
}
