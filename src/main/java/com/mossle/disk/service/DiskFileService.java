package com.mossle.disk.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.client.store.StoreClient;

import com.mossle.core.page.Page;

import com.mossle.disk.persistence.domain.DiskAcl;
import com.mossle.disk.persistence.domain.DiskInfo;
import com.mossle.disk.persistence.domain.DiskTag;
import com.mossle.disk.persistence.manager.DiskInfoManager;
import com.mossle.disk.service.internal.DiskAclInternalService;
import com.mossle.disk.service.internal.DiskBaseInternalService;
import com.mossle.disk.service.internal.DiskLogInternalService;
import com.mossle.disk.service.internal.DiskQueryInternalService;
import com.mossle.disk.service.internal.DiskTagInternalService;
import com.mossle.disk.support.DiskAclException;
import com.mossle.disk.support.Result;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DiskFileService {
    private static Logger logger = LoggerFactory
            .getLogger(DiskFileService.class);
    private DiskLogInternalService diskLogInternalService;
    private DiskAclInternalService diskAclInternalService;
    private DiskBaseInternalService diskBaseInternalService;
    private DiskTagInternalService diskTagInternalService;
    private DiskQueryInternalService diskQueryInternalService;
    private StoreClient storeClient;
    @Resource
    private DiskInfoManager diskInfoManager;

    /**
     * 0202 删除.
     */
    public DiskInfo removeFile(Long fileId, String userId) {
        logger.info("removeFile {} {}", fileId, userId);

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

            return diskInfo;
        } else {
            return null;
        }
    }

    /**
     * 0203 详情.
     */
    public DiskInfo findFile(Long fileId, String userId) {
        logger.info("findFile {} {}", fileId, userId);

        // acl
        if (this.diskAclInternalService.lackPermission(fileId, userId,
                diskAclInternalService.MASK_READ)) {
            logger.info("lack permission : {} {} {} {} {}", fileId, "file",
                    userId, diskAclInternalService.MASK_READ, "findFile");
            throw new DiskAclException(fileId, "file", userId,
                    diskAclInternalService.MASK_READ, "findFile");
        }

        Result<DiskInfo> result = this.diskBaseInternalService.findById(fileId);

        if (result.isFailure()) {
            return null;
        }

        return result.getData();
    }

    /**
     * 0204 重命名.
     */
    public DiskInfo rename(Long fileId, String userId, String name) {
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
            return null;
        }

        return result.getData();
    }

    /**
     * 0205 移动.
     */
    public DiskInfo move(Long fileId, String userId, Long parentId) {
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
            return null;
        }

        return result.getData();
    }

    /**
     * 0206 复制.
     */
    public DiskInfo copyFile(Long fileId, String userId, Long parentId) {
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
            return null;
        }

        return result.getData();
    }

    /**
     * 0207 恢复.
     */
    public DiskInfo recover(Long fileId, String userId, Long parentId) {
        logger.info("recover : {} {} {}", fileId, userId, parentId);

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
            return null;
        }

        return result.getData();
    }

    /**
     * 0208 彻底删除.
     */
    public DiskInfo delete(Long fileId, String userId) {
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
            return null;
        }

        return result.getData();
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
    public DiskInfo saveTags(Long fileId, String userId, String tags) {
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
    public List<DiskAcl> findPermissions(Long fileId, String userId) {
        return this.diskAclInternalService.findPermissions(fileId, userId);
    }

    /**
     * 0212 添加权限.
     */
    public DiskInfo addPermission(Long fileId, String userId, String memberId,
            int mask) {
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
            return null;
        }

        return result.getData();
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

        return result;
    }

    /**
     * 0212 自动解压.
     */
    public void uncompress(Long fileId, String userId, String tenantId)
            throws Exception {
        logger.info("uncompress : {} {}", fileId, userId);

        // acl
        if (this.diskAclInternalService.lackPermission(fileId, userId,
                diskAclInternalService.MASK_READ)) {
            logger.info("lack permission : {} {} {} {} {}", fileId, "file",
                    userId, diskAclInternalService.MASK_READ, "uncompress");
            throw new DiskAclException(fileId, "file", userId,
                    diskAclInternalService.MASK_READ, "uncompress");
        }
    }

    /**
     * 0213 版本化.
     */
    public void findVersions(Long fileId, String userId, String tenantId)
            throws Exception {
        logger.info("findVersions : {} {}", fileId, userId);

        // acl
        if (this.diskAclInternalService.lackPermission(fileId, userId,
                diskAclInternalService.MASK_READ)) {
            logger.info("lack permission : {} {} {} {} {}", fileId, "file",
                    userId, diskAclInternalService.MASK_READ, "findVersions");
            throw new DiskAclException(fileId, "file", userId,
                    diskAclInternalService.MASK_READ, "findVersions");
        }
    }

    /**
     * 0301 搜索.
     */
    public Page search(String query, String startDate, String endDate,
            String userId, Page page) {
        logger.info("search : {} {} {} {}", userId, query, startDate, endDate);
        page = diskQueryInternalService.search(query, startDate, endDate,
                userId, page);

        // log
        this.diskLogInternalService.recordLogSearch(query, userId,
                DiskLogInternalService.CATALOG_SEARCH);

        return page;
    }

    public Page searchAdvanced(String query, String sort, String filterValue,
            String searchType, String userId, Page page) {
        page = diskQueryInternalService.searchAdvanced(query, sort,
                filterValue, searchType, userId, page);

        // log
        this.diskLogInternalService.recordLogSearch(query, userId,
                DiskLogInternalService.CATALOG_SEARCH);

        return page;
    }

    // ~

    /**
     * 获取目录路径.
     */
    public String findFolderPath(Long fileId) {
        Result<DiskInfo> result = this.diskBaseInternalService
                .findActive(fileId);

        if (result.isFailure()) {
            return "";
        }

        DiskInfo current = result.getData();

        if (current == null) {
            logger.info("cannot find current : {}", fileId);

            return "";
        }

        StringBuilder buff = new StringBuilder();

        while (current != null) {
            current = current.getDiskInfo();

            if (current == null) {
                break;
            }

            buff.insert(0, "/" + current.getName());
        }

        return buff.toString();
    }

    /**
     * 回收站.
     */
    public Page findTrash(String userId, int pageNo, int pageSize) {
        return this.diskQueryInternalService
                .findTrash(userId, pageNo, pageSize);
    }

    /**
     * 更新.
     */
    public void save(DiskInfo diskInfo) {
        this.diskBaseInternalService.save(diskInfo);
    }

    /**
     * 有多少文件需要从回收站自动删除.
     */
    public long findNeedMarkDeletedFileCount() {
        String hql = " select count(*) from DiskInfo "
                + " where status='trash' and deleteTime<? ";

        return diskInfoManager.getCount(hql, new Date());
    }

    public Long markDeleted(Long previousDiskInfoId) {
        String hql = " from DiskInfo where status='trash' and deleteTime<? "
                + " and id>?";
        DiskInfo diskInfo = diskInfoManager.findUnique(hql, new Date(),
                previousDiskInfoId);

        if (diskInfo == null) {
            return null;
        }

        diskInfo.setStatus("deleted");
        diskInfoManager.save(diskInfo);

        return diskInfo.getId();
    }

    /**
     * 有多少文件需要真实删除.
     */
    public long findNeedRealDeleteFileCount() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 30);

        String hql = " select count(*) from DiskInfo "
                + " where dirType=1 and status='deleted' and deleteTime<? ";

        return diskInfoManager.getCount(hql, calendar.getTime());
    }

    public Long markDone(Long previousDiskInfoId) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 30);

        String hql = " from DiskInfo where dirType=1 and status='deleted' "
                + " and deleteTime<? and id>?";
        DiskInfo diskInfo = diskInfoManager.findUnique(hql, calendar.getTime(),
                previousDiskInfoId);

        if (diskInfo == null) {
            return null;
        }

        String model = "disk";
        String key = diskInfo.getRef();

        // boolean success = storeClient.removeStore(model, key, "1");
        // if (!success) {
        // logger.info("real delete failure : {}", diskInfo.getId());
        // return diskInfo.getId();
        // }
        try {
            storeClient.removeStore(model, key, "1");
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return diskInfo.getId();
        }

        diskInfo.setStatus("done");
        diskInfoManager.save(diskInfo);

        return diskInfo.getId();
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

    @Resource
    public void setStoreClient(StoreClient storeClient) {
        this.storeClient = storeClient;
    }
}
