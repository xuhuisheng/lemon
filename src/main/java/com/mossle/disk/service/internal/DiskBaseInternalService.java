package com.mossle.disk.service.internal;

import java.io.FileNotFoundException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.activation.DataSource;

import javax.annotation.Resource;

import com.mossle.api.store.StoreDTO;

import com.mossle.client.store.StoreClient;

import com.mossle.disk.persistence.domain.DiskInfo;
import com.mossle.disk.persistence.domain.DiskSpace;
import com.mossle.disk.persistence.manager.DiskInfoManager;
import com.mossle.disk.persistence.manager.DiskSpaceManager;
import com.mossle.disk.support.DiskInfoBuilder;
import com.mossle.disk.support.Result;
import com.mossle.disk.util.FileUtils;

import org.apache.commons.lang3.StringUtils;

import org.hibernate.LockOptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DiskBaseInternalService {
    public static final int DIR_TYPE_FOLDER = 0;
    public static final int DIR_TYPE_FILE = 1;
    private static Logger logger = LoggerFactory
            .getLogger(DiskBaseInternalService.class);
    private DiskInfoManager diskInfoManager;
    private DiskSpaceManager diskSpaceManager;
    private StoreClient storeClient;

    /**
     * 创建空间对应的根文件夹.
     */
    public Result<DiskInfo> createRoot(String name, long spaceId) {
        logger.info("create root : {} {}", name, spaceId);

        // validate
        if (StringUtils.isBlank(name)) {
            logger.info("name cannot be blank");

            return Result.failure(400, "name cannot blank");
        }

        DiskSpace diskSpace = diskSpaceManager.get(spaceId);

        if (diskSpace == null) {
            logger.info("cannot find space : {}", spaceId);

            return Result.failure(404, "no space " + spaceId);
        }

        DiskInfo folder = diskInfoManager.findUniqueBy("diskSpace", diskSpace);

        if (folder != null) {
            logger.info("exists root folder : {}", spaceId);

            return Result.failure(409, "conflict " + spaceId, folder);
        }

        String userId = diskSpace.getCreator();

        DiskInfo diskInfo = new DiskInfoBuilder().build();
        diskInfo.setName(name);
        diskInfo.setType("folder");
        diskInfo.setDirType(DIR_TYPE_FOLDER);
        diskInfo.setOwnerId(userId);
        diskInfo.setCreator(userId);
        diskInfo.setLastModifier(userId);
        diskInfo.setDiskSpace(diskSpace);
        diskInfo.setDiskRule(diskSpace.getDiskRule());
        diskInfo.setInherit("false");
        diskInfoManager.save(diskInfo);

        // success
        return Result.success(diskInfo);
    }

    // 0001
    /**
     * 创建一个文件或文件夹的实体信息.
     * 
     * @param userId
     *            创建人
     * @param name
     *            文件或文件夹名称
     * @param size
     *            文件大小，文件夹为0
     * @param ref
     *            文件oss key
     * @param type
     *            类型，比如folder, jpg
     * @param dirType
     *            文件夹0，文件1，用于排序文件夹在文件之上，还是叫fileType好些
     * @param folderId
     *            父文件夹
     */

    // @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Result<DiskInfo> create(String userId, String name, long size,
            String ref, String type, int dirType, long folderId) {
        logger.info("create : {} {} {} {} {} {} {}", userId, name, size, ref,
                type, dirType, folderId);

        // validate
        if (StringUtils.isBlank(name)) {
            logger.info("name cannot be blank");

            return Result.failure(400, "name cannot blank");
        }

        if (ref == null) {
            ref = "";
        }

        // lock parent
        // DiskInfo folder = (DiskInfo) diskInfoManager.getSession().get(
        // DiskInfo.class, folderId, LockOptions.UPGRADE);
        DiskInfo folder = diskInfoManager.get(folderId);

        if (folder == null) {
            logger.info("cannot find folder : {}", folderId);

            return Result.failure(404, "no folder " + folderId);
        }

        DiskInfo dumplicatedFile = this.findDumplicatedFileCreate(name,
                folderId);

        if (dumplicatedFile != null) {
            logger.info("name conflict : {} {}", name, folderId);

            return Result.failure(409, "conflict " + name, dumplicatedFile);
        }

        DiskInfo diskInfo = new DiskInfoBuilder().build();
        diskInfo.setName(name);
        diskInfo.setType(type);
        diskInfo.setFileSize(size);
        diskInfo.setOwnerId(userId);
        diskInfo.setCreator(userId);
        diskInfo.setLastModifier(userId);
        diskInfo.setDirType(dirType);
        diskInfo.setRef(ref);
        diskInfo.setDiskInfo(folder);
        diskInfo.setDiskSpace(folder.getDiskSpace());
        diskInfo.setDiskRule(folder.getDiskRule());

        diskInfoManager.save(diskInfo);

        // success
        return Result.success(diskInfo);
    }

    // 0003
    /**
     * 此为底层方法，只判断是否存在，外层还需要判断状态.
     */
    public Result<DiskInfo> findById(long infoId) {
        logger.info("findById {}", infoId);

        DiskInfo diskInfo = diskInfoManager.get(infoId);

        if (diskInfo == null) {
            return Result.failure(404, "cannot find " + infoId);
        }

        return Result.success(diskInfo);
    }

    /**
     * 只返回正常状态的节点.
     */
    public Result<DiskInfo> findActive(long infoId) {
        logger.info("findById {}", infoId);

        DiskInfo diskInfo = diskInfoManager.get(infoId);

        if (diskInfo == null) {
            return Result.failure(404, "cannot find " + infoId);
        }

        if (!"active".equals(diskInfo.getStatus())) {
            logger.info("unsupport status : {} {}", diskInfo.getId(),
                    diskInfo.getStatus());

            return Result.failure(405,
                    "unsupport status " + diskInfo.getStatus());
        }

        return Result.success(diskInfo);
    }

    // 0002
    // active -> trash -> deleted
    public Result<DiskInfo> remove(long infoId, String userId) {
        logger.info("remove {}", infoId);

        DiskInfo diskInfo = diskInfoManager.get(infoId);

        if (diskInfo == null) {
            logger.info("cannot find file : {}", infoId);

            return Result.failure(404, "cannot find " + infoId);
        }

        if (!"active".equals(diskInfo.getStatus())) {
            logger.info("unsupport status : {} {}", diskInfo.getId(),
                    diskInfo.getStatus());

            return Result.failure(405,
                    "unsupport status " + diskInfo.getStatus());
        }

        this.removeInternal(infoId, userId, "root");

        return Result.success(diskInfo);
    }

    public void removeInternal(long infoId, String userId, String deleteStatus) {
        DiskInfo diskInfo = diskInfoManager.get(infoId);

        if (diskInfo == null) {
            logger.info("cannot find : {}", infoId);

            return;
        }

        // 考虑过不判断权限，一直迭代下去，但是怕死循环，还是没敢
        if (!"active".equals(diskInfo.getStatus())) {
            logger.info("skip : {}", diskInfo.getId());

            return;
        }

        Calendar calendar = Calendar.getInstance();
        diskInfo.setLastModifier(userId);
        diskInfo.setLastModifiedTime(calendar.getTime());
        diskInfo.setStatus("trash");
        diskInfo.setDeleteStatus(deleteStatus);
        calendar.add(Calendar.DAY_OF_YEAR, 30);
        diskInfo.setDeleteTime(calendar.getTime());

        if (diskInfo.getDiskInfo() != null) {
            diskInfo.setOriginalParentId(diskInfo.getDiskInfo().getId());
        }

        diskInfoManager.save(diskInfo);

        // recursive
        this.removeChildrenInternal(infoId, userId, "child");
    }

    // TODO: check cycle
    public void removeChildrenInternal(long infoId, String userId,
            String deleteStatus) {
        DiskInfo parent = diskInfoManager.get(infoId);

        for (DiskInfo child : parent.getDiskInfos()) {
            this.removeInternal(child.getId(), userId, deleteStatus);
        }
    }

    // 0008
    // active -> trash -> deleted
    public Result<DiskInfo> delete(long infoId, String userId) {
        logger.info("delete : {} {}", infoId, userId);

        DiskInfo diskInfo = diskInfoManager.get(infoId);

        if (diskInfo == null) {
            logger.info("cannot find file : {}", infoId);

            return Result.failure(404, "no file " + infoId);
        }

        if (!"trash".equals(diskInfo.getStatus())) {
            logger.info("unsupport status : {} {}", diskInfo.getId(),
                    diskInfo.getStatus());

            return Result.failure(405,
                    "unsupport status " + diskInfo.getStatus());
        }

        this.deleteInternal(infoId, userId);

        diskInfo.setStatus("deleted");
        diskInfo.setLastModifier(userId);
        diskInfo.setLastModifiedTime(new Date());
        diskInfoManager.save(diskInfo);

        return Result.success(diskInfo);
    }

    public void deleteInternal(long infoId, String userId) {
        DiskInfo diskInfo = diskInfoManager.get(infoId);

        if (diskInfo == null) {
            logger.info("cannot find : {}", infoId);

            return;
        }

        if (!"trash".equals(diskInfo.getStatus())) {
            logger.info("skip : {}", diskInfo.getId());

            return;
        }

        Calendar calendar = Calendar.getInstance();
        diskInfo.setLastModifier(userId);
        diskInfo.setLastModifiedTime(calendar.getTime());
        diskInfo.setStatus("deleted");

        diskInfoManager.save(diskInfo);

        // recursive
        this.deleteChildrenInternal(infoId, userId);
    }

    // TODO: check cycle
    public void deleteChildrenInternal(long infoId, String userId) {
        DiskInfo parent = diskInfoManager.get(infoId);

        for (DiskInfo child : parent.getDiskInfos()) {
            // 如果遇到回收站中标记为根的文件夹，跳过
            if ("root".equals(child.getDeleteStatus())) {
                logger.info("skip root : {}", child.getId());

                return;
            }

            this.deleteInternal(child.getId(), userId);
        }
    }

    // 0007
    // active -> trash -> deleted
    public Result<DiskInfo> recover(long infoId, String userId,
            long targetFolderId) {
        logger.info("recover : {} {} {}", infoId, userId, targetFolderId);

        DiskInfo diskInfo = diskInfoManager.get(infoId);

        if (diskInfo == null) {
            logger.info("cannot find file : {}", infoId);

            return Result.failure(404, "no file " + infoId);
        }

        if (!"trash".equals(diskInfo.getStatus())) {
            logger.info("unsupport status : {} {}", diskInfo.getId(),
                    diskInfo.getStatus());

            return Result.failure(405,
                    "unsupport status " + diskInfo.getStatus());
        }

        long originalParentId = diskInfo.getOriginalParentId();
        DiskInfo folder = diskInfoManager.get(originalParentId);

        if (folder == null) {
            logger.info("cannot find folder : {}", originalParentId);

            return Result.failure(404, "no folder " + originalParentId);
        }

        if (!"active".equals(folder.getStatus())) {
            logger.info("unsupport status : {} {}", folder.getId(),
                    folder.getStatus());

            return Result
                    .failure(405, "unsupport status " + folder.getStatus());
        }

        String name = diskInfo.getName();
        long parentId = originalParentId;
        DiskInfo dumplicatedFile = this.findDumplicatedFileUpdate(name,
                parentId, infoId);

        if (dumplicatedFile != null) {
            logger.info("name conflict : {} {}", name, parentId);

            return Result.failure(409, "conflict " + name);
        }

        this.recoverInternal(infoId, userId);

        return Result.success(diskInfo);
    }

    public void recoverInternal(long infoId, String userId) {
        DiskInfo diskInfo = diskInfoManager.get(infoId);

        if (diskInfo == null) {
            logger.info("cannot find : {}", infoId);

            return;
        }

        if (!"trash".equals(diskInfo.getStatus())) {
            logger.info("skip : {}", diskInfo.getId());

            return;
        }

        Calendar calendar = Calendar.getInstance();
        diskInfo.setLastModifier(userId);
        diskInfo.setLastModifiedTime(calendar.getTime());
        diskInfo.setStatus("active");

        diskInfoManager.save(diskInfo);

        // recursive
        this.recoverChildrenInternal(infoId, userId);
    }

    // TODO: check cycle
    public void recoverChildrenInternal(long infoId, String userId) {
        DiskInfo parent = diskInfoManager.get(infoId);

        for (DiskInfo child : parent.getDiskInfos()) {
            // 如果遇到回收站中标记为根的文件夹，跳过
            if ("root".equals(child.getDeleteStatus())) {
                logger.info("skip root : {}", child.getId());

                return;
            }

            this.recoverInternal(child.getId(), userId);
        }
    }

    // 0004
    public Result<DiskInfo> rename(long infoId, String userId, String name) {
        logger.info("rename : {} {} {}", infoId, userId, name);

        // validate
        if (StringUtils.isBlank(userId)) {
            logger.info("userId cannot be blank");

            return Result.failure(400, "userId cannot blank");
        }

        if (StringUtils.isBlank(name)) {
            logger.info("name cannot be blank");

            return Result.failure(400, "name cannot blank");
        }

        DiskInfo diskInfo = diskInfoManager.get(infoId);

        if (diskInfo == null) {
            logger.info("cannot find file : {}", infoId);

            return Result.failure(404, "cannot find " + infoId);
        }

        if (!"active".equals(diskInfo.getStatus())) {
            logger.info("unsupport status : {} {}", diskInfo.getId(),
                    diskInfo.getStatus());

            return Result.failure(405,
                    "unsupport status " + diskInfo.getStatus());
        }

        if (name.equals(diskInfo.getName())) {
            logger.info("unmodified : {} {}", infoId, name);

            return Result.failure(304, "no modified");
        }

        DiskInfo folder = diskInfo.getDiskInfo();

        if (folder == null) {
            // 根文件夹
            logger.info(
                    "cannot find folder : {}, it should be root folder of space",
                    infoId);

            // 应该只有一个根文件夹，不用判断重名，或者就不应该让根文件夹改名
            // TODO: 考虑到共享空间的根文件夹可以改名，但是要考虑和其他空间是否重名
            // return Result
            // .failure(405, "root folder shouldnot rename " + infoId);
        } else {
            long parentId = folder.getId();
            DiskInfo dumplicatedFile = this.findDumplicatedFileUpdate(name,
                    parentId, infoId);

            if (dumplicatedFile != null) {
                logger.info("name conflict : {} {}", name, parentId);

                return Result.failure(409, "conflict " + name);
            }
        }

        diskInfo.setName(name);
        diskInfo.setLastModifier(userId);
        diskInfo.setLastModifiedTime(new Date());

        if (!"folder".equals(diskInfo.getType())) {
            String type = FileUtils.getSuffix(name);

            if (!type.equals(diskInfo.getType())) {
                diskInfo.setType(type);

                // TODO: preview refresh?
            }
        }

        diskInfoManager.save(diskInfo);
        logger.info("rename success : {} {}", diskInfo.getId(),
                diskInfo.getName());

        return Result.success(diskInfo);
    }

    // 0005
    public Result<DiskInfo> move(long infoId, String userId, long targetFolderId) {
        logger.info("move : {} {} {}", infoId, userId, targetFolderId);

        if (infoId == targetFolderId) {
            logger.info("{} is equals {}", infoId, targetFolderId);

            return Result.failure(304, "target folder is self");
        }

        DiskInfo diskInfo = diskInfoManager.get(infoId);

        if (!"active".equals(diskInfo.getStatus())) {
            logger.info("unsupport status : {} {}", diskInfo.getId(),
                    diskInfo.getStatus());

            return Result.failure(405,
                    "unsupport status " + diskInfo.getStatus());
        }

        if (diskInfo.getDiskInfo() == null) {
            // 空间根目录
            logger.info("root folder " + infoId);
        } else if (diskInfo.getDiskInfo().getId() == targetFolderId) {
            logger.info("unmodified folder : {} {}", infoId, targetFolderId);

            return Result.failure(304, "no modified");
        }

        DiskInfo targetFolder = diskInfoManager.get(targetFolderId);

        if (targetFolder == null) {
            logger.info("cannot find target folder : {}", targetFolderId);

            return Result.failure(404, "cannot find target folder "
                    + targetFolderId);
        }

        if (!"active".equals(targetFolder.getStatus())) {
            logger.info("unsupport status : {} {}", targetFolderId,
                    targetFolder.getStatus());

            return Result.failure(405,
                    "unsupport status " + targetFolder.getStatus());
        }

        if (!"folder".equals(targetFolder.getType())) {
            logger.info(
                    "{}({}) is not folder",
                    this.findFolderPath(targetFolderId) + "/"
                            + targetFolder.getName(), targetFolderId);

            return Result
                    .failure(304, "target is not folder " + targetFolderId);
        }

        String checkedParentPath = this.findFolderPath(targetFolderId) + "/"
                + targetFolder.getName();
        String currentPath = this.findFolderPath(diskInfo.getId()) + "/"
                + diskInfo.getName();

        if ("folder".equals(diskInfo.getType())
                && checkedParentPath.startsWith(currentPath)) {
            logger.info("{}({}) is sub folder of {}({})", checkedParentPath,
                    targetFolderId, currentPath, infoId);

            return Result.failure(304, "target is sub folder");
        }

        String name = diskInfo.getName();

        DiskInfo dumplicatedFile = this.findDumplicatedFileUpdate(name,
                targetFolderId, infoId);

        if (dumplicatedFile != null) {
            logger.info("name conflict : {} {}", name, targetFolderId);

            return Result.failure(409, "conflict " + name);
        }

        diskInfo.setDiskInfo(targetFolder);
        diskInfo.setLastModifier(userId);
        diskInfo.setLastModifiedTime(new Date());
        diskInfo.setDiskSpace(targetFolder.getDiskSpace());

        diskInfoManager.save(diskInfo);

        return Result.success(diskInfo);
    }

    // 0006
    public Result<DiskInfo> copy(long infoId, String userId, long targetFolderId) {
        logger.info("copy : {} {} {}", infoId, userId, targetFolderId);

        DiskInfo source = diskInfoManager.get(infoId);

        if (source == null) {
            logger.info("cannot find source file : {}", source);

            return Result.failure(404, "cannot find source " + infoId);
        }

        if (!"active".equals(source.getStatus())) {
            logger.info("unsupport status : {} {}", infoId, source.getStatus());

            return Result
                    .failure(405, "unsupport status " + source.getStatus());
        }

        DiskInfo targetFolder = diskInfoManager.get(targetFolderId);

        if (targetFolder == null) {
            logger.info("cannot find folder : {}", targetFolderId);

            return Result.failure(404, "cannot find folder " + targetFolderId);
        }

        if (!"active".equals(targetFolder.getStatus())) {
            logger.info("unsupport status : {} {}", targetFolderId,
                    targetFolder.getStatus());

            return Result.failure(405,
                    "unsupport status " + targetFolder.getStatus());
        }

        String newName = FileUtils.modifyFileName(source.getName(), " copy");
        Result<DiskInfo> result = this.create(userId, newName,
                source.getFileSize(), source.getRef(), source.getType(),
                source.getDirType(), targetFolder.getId());

        if (result.isFailure()) {
            logger.info("copy failure : {}", result.getMessage());

            return result;
        }

        DiskInfo target = result.getData();
        target.setInherit(source.getInherit());
        // TODO: inherit = false?
        target.setDiskRule(targetFolder.getDiskRule());
        // preview copy
        target.setPreviewStatus(source.getPreviewStatus());
        target.setPreviewRef(source.getPreviewRef());

        target.setDiskSpace(targetFolder.getDiskSpace());
        diskInfoManager.save(target);

        return Result.success(target);
    }

    // 000x
    public Result<DiskInfo> link(long infoId, String userId, long targetFolderId) {
        logger.info("link : {} {} {}", infoId, userId, targetFolderId);

        DiskInfo source = diskInfoManager.get(infoId);

        if (source == null) {
            logger.info("cannot find source file : {}", source);

            return Result.failure(404, "cannot find source " + infoId);
        }

        if (!"active".equals(source.getStatus())) {
            logger.info("unsupport status : {} {}", infoId, source.getStatus());

            return Result
                    .failure(405, "unsupport status " + source.getStatus());
        }

        DiskInfo targetFolder = diskInfoManager.get(targetFolderId);

        if (targetFolder == null) {
            logger.info("cannot find folder : {}", targetFolderId);

            return Result.failure(404, "cannot find folder " + targetFolderId);
        }

        if (!"active".equals(targetFolder.getStatus())) {
            logger.info("unsupport status : {} {}", targetFolderId,
                    targetFolder.getStatus());

            return Result.failure(405,
                    "unsupport status " + targetFolder.getStatus());
        }

        String newName = FileUtils.modifyFileName(source.getName(), " copy");
        Result<DiskInfo> result = this.create(userId, newName,
                source.getFileSize(), source.getRef(), source.getType(),
                source.getDirType(), targetFolder.getId());

        if (result.isFailure()) {
            logger.info("ilnk failure : {}", result.getMessage());

            return result;
        }

        DiskInfo target = result.getData();
        target.setLinkType(1);
        target.setLinkId(infoId);
        target.setInherit(source.getInherit());
        // TODO: inherit = false?
        target.setDiskRule(targetFolder.getDiskRule());
        target.setDiskSpace(targetFolder.getDiskSpace());

        diskInfoManager.save(target);

        return Result.success(target);
    }

    // ~

    /**
     * 获取目录路径.
     */
    public String findFolderPath(long fileId) {
        DiskInfo current = this.diskInfoManager.get(fileId);

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
     * 获取左侧树形目录需要的路径.
     */
    public List<String> findTreePath(long infoId) {
        DiskInfo current = this.diskInfoManager.get(infoId);

        if (current == null) {
            logger.info("cannot find {}", infoId);

            return Collections.emptyList();
        }

        List<DiskInfo> diskInfos = new ArrayList<DiskInfo>();

        while (current != null) {
            diskInfos.add(current);
            current = current.getDiskInfo();
        }

        Collections.reverse(diskInfos);

        DiskSpace diskSpace = diskInfos.get(0).getDiskSpace();
        List<String> result = new ArrayList<String>();

        if ("group".equals(diskSpace.getCatalog())) {
            result.add("group");
        }

        for (DiskInfo diskInfo : diskInfos) {
            result.add(Long.toString(diskInfo.getId()));
        }

        return result;
    }

    /**
     * 获取文件inputstream.
     */
    public Result<InputStream> findInputStream(long infoId) throws Exception {
        DiskInfo diskInfo = this.diskInfoManager.get(infoId);

        if (diskInfo == null) {
            logger.info("cannot find info : {}", infoId);

            return Result.failure(404, "no file " + infoId);
        }

        String modelName = "disk";
        String keyName = diskInfo.getRef();

        String tenantId = "1";

        StoreDTO storeDto = storeClient.getStore(modelName, keyName, tenantId);

        if (storeDto == null) {
            logger.info("cannot find file : {} {} {}", modelName, keyName,
                    tenantId);

            return Result.failure(404, "no store " + modelName + "/" + keyName);
        }

        DataSource dataSource = storeDto.getDataSource();

        if (dataSource == null) {
            logger.info("cannot find file : {} {} {}", modelName, keyName,
                    tenantId);

            return Result.failure(404, "no content " + modelName + "/"
                    + keyName);
        }

        try {
            InputStream is = dataSource.getInputStream();

            return Result.success(is);
        } catch (FileNotFoundException ex) {
            logger.error(ex.getMessage(), ex);

            return Result.failure(404, "no content " + modelName + "/"
                    + keyName);
        }
    }

    /**
     * 判断是否存在同名文件.
     */
    public DiskInfo findDumplicatedFileCreate(String name, long folderId) {
        if (StringUtils.isBlank(name)) {
            logger.info("name cannot be blank");

            return null;
        }

        String hql = "from DiskInfo where status='active' and diskInfo.id=? and name=?";

        // String hql = "from DiskInfo where status='active' and diskInfo.id=?0 and name=?1";
        DiskInfo dumplicatedFile = this.diskInfoManager.findUnique(hql,
                folderId, name);

        return dumplicatedFile;
    }

    /**
     * 判断是否存在同名文件，排除自己.
     */
    public DiskInfo findDumplicatedFileUpdate(String name, long folderId,
            long infoId) {
        if (StringUtils.isBlank(name)) {
            logger.info("name cannot be blank");

            return null;
        }

        String hql = "from DiskInfo where status='active' and diskInfo.id=? and name=? and id!=?";

        // String hql = "from DiskInfo where status='active' and diskInfo.id=?0 and name=?1 and id!=?2";
        DiskInfo dumplicatedFile = this.diskInfoManager.findUnique(hql,
                folderId, name, infoId);

        return dumplicatedFile;
    }

    // 计算节点名称，自动避免重名
    public String calculateName(String name, long folderId, long infoId) {
        String hql = "select name from DiskInfo where status='active' and diskInfo.id=? and id!=?";

        // String hql = "select name from DiskInfo where status='active' and diskInfo.id=?0 and id!=?1";
        List<String> currentNames = diskInfoManager.find(hql, folderId, infoId);

        if (currentNames.isEmpty()) {
            return name;
        }

        String targetName = FileUtils.calculateName(name, currentNames);
        logger.info("conflict : {} {} {}", name, folderId, targetName);

        return targetName;
    }

    /**
     * 获取目录路径.
     */
    public List<DiskInfo> findFolderPath(Long infoId) {
        DiskInfo current = this.diskInfoManager.get(infoId);

        if (current == null) {
            logger.info("cannot find current : {}", infoId);

            return Collections.emptyList();
        }

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

    /**
     * 更新.
     */
    public void save(DiskInfo diskInfo) {
        diskInfoManager.save(diskInfo);
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
    public void setStoreClient(StoreClient storeClient) {
        this.storeClient = storeClient;
    }
}
