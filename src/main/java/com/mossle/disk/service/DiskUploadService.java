package com.mossle.disk.service;

import java.io.InputStream;
import java.io.SequenceInputStream;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.activation.DataSource;

import javax.annotation.Resource;

import com.mossle.api.store.StoreDTO;

import com.mossle.client.store.StoreClient;

import com.mossle.core.store.ByteArrayDataSource;
import com.mossle.core.store.InputStreamDataSource;

import com.mossle.disk.persistence.domain.DiskFile;
import com.mossle.disk.persistence.domain.DiskInfo;
import com.mossle.disk.persistence.domain.DiskUpload;
import com.mossle.disk.persistence.domain.DiskVersion;
import com.mossle.disk.persistence.manager.DiskFileManager;
import com.mossle.disk.persistence.manager.DiskInfoManager;
import com.mossle.disk.persistence.manager.DiskUploadManager;
import com.mossle.disk.service.internal.DiskAclInternalService;
import com.mossle.disk.service.internal.DiskBaseInternalService;
import com.mossle.disk.service.internal.DiskLogInternalService;
import com.mossle.disk.service.internal.DiskQueryInternalService;
import com.mossle.disk.service.internal.DiskVersionInternalService;
import com.mossle.disk.support.DiskAclException;
import com.mossle.disk.support.DiskFileBuilder;
import com.mossle.disk.support.Result;
import com.mossle.disk.support.TreeNode;
import com.mossle.disk.support.UploadResult;
import com.mossle.disk.support.ZipProcessor;
import com.mossle.disk.util.FileUtils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DiskUploadService {
    private static Logger logger = LoggerFactory
            .getLogger(DiskUploadService.class);
    private DiskInfoManager diskInfoManager;
    private DiskFileManager diskFileManager;
    private DiskUploadManager diskUploadManager;
    private StoreClient storeClient;
    private DiskLogInternalService diskLogInternalService;
    private DiskVersionInternalService diskVersionInternalService;
    private DiskAclInternalService diskAclInternalService;
    private DiskBaseInternalService diskBaseInternalService;
    private DiskQueryInternalService diskQueryInternalService;
    private DiskFolderService diskFolderService;
    private boolean autoUncompress = true;

    /**
     * 0201 文件上传.
     */
    public UploadResult uploadFile(Long folderId, String userId,
            DataSource dataSource, String name, long size, String hashCode,
            Long lastModified, String tenantId) throws Exception {
        logger.info("uploadFile {} {} {}", folderId, userId, name);

        // acl
        if (this.diskAclInternalService.lackPermission(folderId, userId,
                diskAclInternalService.MASK_CREATE)) {
            logger.info("lack permission : {} {} {} {} {}", folderId, "folder",
                    userId, diskAclInternalService.MASK_CREATE, "uploadFile");
            throw new DiskAclException(folderId, "folder", userId,
                    diskAclInternalService.MASK_CREATE, "uploadFile");
        }

        tenantId = "1";

        DiskUpload diskUpload = this.startUploadSingle(userId, name, "", size,
                folderId, "", hashCode, lastModified, userId);
        UploadResult uploadResult = this.uploadSingleFile(diskUpload.getId(),
                dataSource, tenantId);

        return uploadResult;
    }

    /**
     * 上传单个文件.
     */
    public UploadResult uploadSingleFile(Long uploadCode,
            DataSource dataSource, String tenantId) throws Exception {
        DiskUpload diskUpload = diskUploadManager.get(uploadCode);
        long folderId = diskUpload.getFolderId();
        String userId = diskUpload.getUserId();
        String name = diskUpload.getName();
        long size = diskUpload.getSize();

        // acl
        if (this.diskAclInternalService.lackPermission(folderId, userId,
                diskAclInternalService.MASK_CREATE)) {
            logger.info("lack permission : {} {} {} {} {}", folderId, "folder",
                    userId, diskAclInternalService.MASK_CREATE, "uploadFile");
            throw new DiskAclException(folderId, "folder", userId,
                    diskAclInternalService.MASK_CREATE, "uploadFile");
        }

        tenantId = "1";

        UploadResult uploadResult = this.uploadInternal(folderId, userId,
                dataSource, name, size, tenantId);
        DiskInfo diskInfo = uploadResult.getFile();
        this.endUploadSingle(uploadCode, "success", "", diskInfo.getId());

        return uploadResult;
    }

    /**
     * 内部上传文件，不校验权限.
     */
    public UploadResult uploadInternal(Long folderId, String userId,
            DataSource dataSource, String name, long size, String tenantId)
            throws Exception {
        logger.info("upload internal {} {} {}", folderId, userId, name);

        // validate
        UploadResult validateResult = this.validateParameters(folderId, userId,
                name, size);

        if (validateResult != null) {
            return validateResult;
        }

        // DiskInfo folder = this.diskInfoManager.get(folderId);
        tenantId = "1";

        // store
        DiskFile diskFile = this.createDiskFile(userId, dataSource, name, size,
                tenantId);

        return this.createDiskInfoByDiskFile(diskFile, name, size, folderId,
                userId);
    }

    /**
     * 通过一个已经上传好的DiskFile创建DiskInfo.
     */
    public UploadResult createDiskInfoByDiskFile(DiskFile diskFile,
            String fileName, long fileSize, long folderId, String userId) {
        String storeKey = diskFile.getValue();

        String fileType = FileUtils.getSuffix(fileName);

        // dumplicate
        DiskInfo dumplicatedFile = this.diskBaseInternalService
                .findDumplicatedFileCreate(fileName, folderId);

        if (dumplicatedFile != null) {
            Date now = new Date();
            dumplicatedFile.setCreator(userId);
            dumplicatedFile.setCreateTime(now);
            dumplicatedFile.setLastModifier(userId);
            dumplicatedFile.setLastModifiedTime(now);
            dumplicatedFile.setFileSize(fileSize);
            dumplicatedFile.setRef(storeKey);
            dumplicatedFile.setType(fileType);

            DiskVersion diskVersion = this.diskVersionInternalService
                    .createVersion(dumplicatedFile);
            // log
            this.diskLogInternalService.recordLog(dumplicatedFile, userId,
                    DiskLogInternalService.CATALOG_UPLOAD);

            DiskInfo diskInfo = dumplicatedFile;

            // zip
            // if (name.endsWith(".zip")) {
            // this.removeFile(diskInfo.getId(), userId);

            // InputStream is = this.findDownloadInputStream(diskInfo.getId(),
            // userId, "1");
            // TreeNode root = new ZipProcessor().processTree(is, name);
            // this.visitTreeNode(root, diskInfo.getDiskInfo(), userId,
            // diskInfo);
            // }

            // return dumplicatedFile;
            UploadResult uploadResult = new UploadResult();
            uploadResult.setFile(dumplicatedFile);
            uploadResult.setVersion(diskVersion);

            return uploadResult;
        }

        Result<DiskInfo> result = this.diskBaseInternalService.create(userId,
                fileName, fileSize, storeKey, fileType, 1, folderId);

        if (result.isFailure()) {
            logger.info("upload failure : {}", result.getMessage());

            UploadResult uploadResult = new UploadResult();

            return uploadResult;
        }

        DiskInfo folder = result.getData();

        DiskInfo diskInfo = result.getData();
        // version
        this.diskVersionInternalService.createVersion(diskInfo);
        // log
        this.diskLogInternalService.recordLog(diskInfo, userId,
                DiskLogInternalService.CATALOG_UPLOAD);

        // zip
        // if (name.endsWith(".zip")) {
        // this.removeFile(diskInfo.getId(), userId);

        // InputStream is = this.findDownloadInputStream(diskInfo.getId(),
        // userId, "1");
        // TreeNode root = new ZipProcessor().processTree(is, name);
        // this.visitTreeNode(root, diskInfo.getDiskInfo(), userId, diskInfo);
        // }
        UploadResult uploadResult = new UploadResult();
        uploadResult.setFile(diskInfo);

        return uploadResult;
    }

    /**
     * 根据diskFile判断重复文件名创建版本.
     */
    public DiskInfo checkDumplicated(DiskFile diskFile, long folderId) {
        String fileName = diskFile.getName();
        DiskInfo dumplicatedFile = this.diskBaseInternalService
                .findDumplicatedFileCreate(fileName, folderId);

        if (dumplicatedFile == null) {
            return null;
        }

        String userId = diskFile.getUserId();
        long fileSize = diskFile.getFileSize();
        String storeKey = diskFile.getValue();
        String type = FileUtils.getSuffix(fileName);

        Date now = new Date();
        dumplicatedFile.setCreator(userId);
        dumplicatedFile.setCreateTime(now);
        dumplicatedFile.setLastModifier(userId);
        dumplicatedFile.setLastModifiedTime(now);
        dumplicatedFile.setFileSize(fileSize);
        dumplicatedFile.setRef(storeKey);
        dumplicatedFile.setType(type);

        DiskVersion diskVersion = this.diskVersionInternalService
                .createVersion(dumplicatedFile);
        // log
        this.diskLogInternalService.recordLog(dumplicatedFile, userId,
                DiskLogInternalService.CATALOG_UPLOAD);

        DiskInfo diskInfo = dumplicatedFile;

        return diskInfo;
    }

    /**
     * 判断hashCode是否已经存在.
     */
    public DiskFile findFileByHashCode(String hashCode) {
        logger.info("check hashCode : {}", hashCode);

        String hql = "from DiskFile where hashcode=? and status='active'";
        DiskFile diskFile = diskFileManager.findUnique(hql, hashCode);

        if (diskFile != null) {
            logger.info("check hashCode success : {}", diskFile.getId());
        }

        return diskFile;
    }

    /**
     * 使用hashCode创建一个新的文件.
     */
    public UploadResult createByHashCode(Long folderId, String userId,
            String hashCode, String name, String tenantId) {
        logger.info("create by hashCode : {} {} {} {} {}", folderId, userId,
                hashCode, name, tenantId);

        DiskFile diskFile = findFileByHashCode(hashCode);
        diskFile.setRefCount(diskFile.getRefCount() + 1);
        diskFileManager.save(diskFile);

        String storeKey = diskFile.getValue();
        long fileSize = diskFile.getFileSize();
        String type = FileUtils.getSuffix(name);
        Result<DiskInfo> result = this.diskBaseInternalService.create(userId,
                name, fileSize, storeKey, type, 1, folderId);

        if (result.isFailure()) {
            logger.info("create by hashCode failure : {}", result.getMessage());

            UploadResult uploadResult = new UploadResult();

            return uploadResult;
        }

        DiskInfo diskInfo = result.getData();
        // version
        this.diskVersionInternalService.createVersion(diskInfo);
        // log
        this.diskLogInternalService.recordLog(diskInfo, userId,
                DiskLogInternalService.CATALOG_UPLOAD);

        UploadResult uploadResult = new UploadResult();
        uploadResult.setFile(diskInfo);

        return uploadResult;
    }

    /**
     * 创建diskFile.
     */
    public DiskFile createDiskFile(String userId, DataSource dataSource,
            String name, long size, String tenantId) throws Exception {
        tenantId = "1";

        String modelName = "disk";

        // String keyName = parentPath + "/" + name;
        // String keyName = name;
        StoreDTO storeDto = storeClient.saveStore(modelName, dataSource,
                tenantId);
        String storeKey = storeDto.getKey();
        String hashCode = calculateHashCode(modelName, storeKey, tenantId);

        DiskFile diskFile = this.createDiskFileMetadata(userId, name, size);
        diskFile.setValue(storeKey);
        diskFile.setHashCode(hashCode);
        diskFileManager.save(diskFile);

        return diskFile;
    }

    /**
     * 创建文件基本信息.
     */
    public DiskFile createDiskFileMetadata(String userId, String fileName,
            long fileSize) {
        DiskFile diskFile = new DiskFileBuilder().build();
        diskFile.setName(fileName);
        diskFile.setFileSize(fileSize);
        diskFile.setCreator(userId);
        diskFile.setUpdater(userId);
        diskFile.setUserId(userId);
        diskFileManager.save(diskFile);

        return diskFile;
    }

    /**
     * 计算hashCode.
     */
    public String calculateHashCode(String modelName, String storeKey,
            String tenantId) throws Exception {
        StoreDTO storeDto = storeClient.getStore(modelName, storeKey, tenantId);
        String hashCode = DigestUtils.md5Hex(storeDto.getDataSource()
                .getInputStream());

        // storeDto = storeClient.getStore(modelName, storeKey, tenantId);
        // logger.info("{}", org.apache.commons.io.IOUtils.toString(storeDto.getDataSource().getInputStream(),
        // "UTF-8"));
        return hashCode;
    }

    /**
     * 准备分片上传.
     */
    public DiskUpload createBatch(String fileName, long fileSize,
            String creator, String userId, Long folderId, String folderPath,
            String hashCode, Long lastModified) {
        logger.info("create batch : {} {}", fileName, fileSize);

        if (folderId == null) {
            folderId = 0L;
        }

        if (lastModified == null) {
            lastModified = 0L;
        }

        String type = FileUtils.getSuffix(fileName);
        DiskUpload diskUpload = startUploadSingle(creator, fileName, type,
                fileSize, folderId, folderPath, hashCode, lastModified, userId);

        return diskUpload;
    }

    /**
     * 上传一个分片.
     */
    public DiskUpload uploadPart(long uploadCode, String fileName,
            long fileSize, String userId, int partIndex, DataSource dataSource)
            throws Exception {
        logger.info("upload part : {} {}", uploadCode, fileName);

        String tenantId = "1";

        // store
        DiskFile diskFile = this.createDiskFile(userId, dataSource, fileName,
                fileSize, tenantId);
        diskFile.setType("part");
        diskFile.setPartIndex(partIndex);
        diskFileManager.save(diskFile);
        logger.info("value : {}", diskFile.getValue());

        DiskUpload diskUpload = endUploadPartChild(userId, uploadCode,
                partIndex, diskFile.getId());

        return diskUpload;
    }

    /**
     * 分片上传结束.
     */
    public DiskUpload uploadPartComplete(long uploadCode, String status,
            String reason) throws Exception {
        logger.info("upload part complete : {} {} {}", uploadCode, status,
                reason);

        DiskUpload diskUpload = diskUploadManager.get(uploadCode);
        String uploadHql = "from DiskUpload where status='success' and partParent=? order by partIndex";
        List<DiskUpload> children = diskUploadManager.find(uploadHql,
                uploadCode);
        List<DiskFile> diskFiles = new ArrayList<DiskFile>();

        for (DiskUpload child : children) {
            DiskFile diskFile = diskFileManager.get(child.getFileId());
            diskFiles.add(diskFile);
        }

        Vector<InputStream> inputStreams = new Vector<InputStream>();

        String tenantId = "1";
        String modelName = "disk";

        for (DiskFile diskFile : diskFiles) {
            String storeKey = diskFile.getValue();
            StoreDTO storeDto = storeClient.getStore(modelName, storeKey,
                    tenantId);
            inputStreams.add(storeDto.getDataSource().getInputStream());
        }

        DataSource dataSource = new InputStreamDataSource(
                new SequenceInputStream(inputStreams.elements()));

        // System.out.println(org.apache.commons.io.IOUtils.toString(dataSource.getInputStream(), "utf-8"));
        // StoreDTO storeDto = storeClient.saveStore(modelName, dataSource,
        // tenantId);
        // String storeKey = storeDto.getKey();
        // String hashCode = this.calculateHashCode(modelName, storeKey, tenantId);
        // logger.info("store key : {}", storeKey);

        // file上传完成后，初始化文件夹路径
        long folderId = diskUpload.getFolderId();
        String userId = diskUpload.getUserId();
        String fileName = diskUpload.getName();
        long fileSize = diskUpload.getSize();

        if (folderId == 0) {
            folderId = this.createFolderByPath(diskUpload.getFolderPath(),
                    userId);
        }

        UploadResult uploadResult = this.uploadInternal(folderId, userId,
                dataSource, fileName, fileSize, "");

        DiskInfo diskInfo = uploadResult.getFile();

        diskUpload = endUploadPartParent(uploadCode, status, reason,
                diskInfo.getId());

        return diskUpload;
    }

    // ~
    /**
     * 根据路径创建目录结构.
     */
    public long createFolderByPath(String path, String userId) throws Exception {
        logger.info("createFolderByPath : {} {}", path, userId);

        DiskInfo currentFolder = this.diskQueryInternalService
                .findUserSpaceRootFolder(userId);

        for (String folderName : path.split("/")) {
            logger.info("folderName : {}", folderName);

            if (StringUtils.isBlank(folderName)) {
                continue;
            }

            String hql = "from DiskInfo where status='active' and diskInfo.id=? and name=?";
            DiskInfo folder = diskInfoManager.findUnique(hql,
                    currentFolder.getId(), folderName);

            if (folder == null) {
                folder = diskFolderService.createFolder(currentFolder.getId(),
                        userId, folderName, "");
            }

            currentFolder = folder;
        }

        return currentFolder.getId();
    }

    /**
     * 遍历zip目录结构.
     */
    public void visitTreeNode(TreeNode treeNode, DiskInfo parentFolder,
            String userId, DiskInfo zipFile) throws Exception {
        if ("folder".equals(treeNode.getType())) {
            String name = treeNode.getName();
            Long folderId = parentFolder.getId();
            Result<DiskInfo> result = this.diskBaseInternalService.create(
                    userId, name, 0, null, "folder", 0, folderId);

            if (result.isFailure()) {
                logger.info("visit tree node failure : {}", result.getMessage());

                return;
            }

            DiskInfo folder = result.getData();

            for (TreeNode child : treeNode.getChildren()) {
                this.visitTreeNode(child, folder, userId, zipFile);
            }
        } else {
            Result<InputStream> result = this.diskBaseInternalService
                    .findInputStream(zipFile.getId());

            if (result.isFailure()) {
                return;
            }

            InputStream is = result.getData();

            logger.info("path : {}", treeNode.getId());

            Map<String, Object> resultMap = new ZipProcessor().readEntry(is,
                    treeNode.getId());

            DiskInfo folder = parentFolder;
            Long folderId = folder.getId();
            String modelName = "disk/" + folder.getDiskSpace().getId();
            DataSource dataSource = new ByteArrayDataSource(
                    (byte[]) resultMap.get("bytes"));
            String tenantId = "1";
            int size = (Integer) resultMap.get("size");
            String name = treeNode.getName();
            StoreDTO storeDto = storeClient.saveStore(modelName, dataSource,
                    tenantId);
            String type = FileUtils.getSuffix(name);
            Result<DiskInfo> fileResult = this.diskBaseInternalService.create(
                    userId, name, size, storeDto.getKey(), type, 1, folderId);

            if (fileResult.isFailure()) {
                logger.info("visit failure : {}", fileResult.getMessage());

                return;
            }

            DiskInfo diskInfo = fileResult.getData();
            // version
            this.diskVersionInternalService.createVersion(diskInfo);
            // log
            this.diskLogInternalService.recordLog(diskInfo, userId,
                    DiskLogInternalService.CATALOG_UPLOAD);
        }
    }

    /**
     * 校验参数.
     */
    public UploadResult validateParameters(Long folderId, String userId,
            String name, long size) {
        if (StringUtils.isBlank(userId)) {
            logger.info("userId cannot be blank");

            return UploadResult.validateFail("userId cannot be blank");
        }

        if (StringUtils.isBlank(name)) {
            logger.info("name cannot be blank");

            return UploadResult.validateFail("name cannot be blank");
        }

        if (folderId == null) {
            logger.info("folderId cannot be null");

            return UploadResult.validateFail("folderId cannot be blank");
        }

        DiskInfo folder = this.diskInfoManager.get(folderId);

        if (folder == null) {
            logger.info("cannot find folder : {}", folderId);

            return UploadResult
                    .validateFail("cannot find folder : " + folderId);
        }

        if (folder.getDiskSpace() == null) {
            logger.info("cannot find space : {}", folderId);

            return UploadResult.validateFail("cannot find space : " + folderId);
        }

        return null;
    }

    // ~
    /**
     * 记录上传整个文件开始.
     */
    public DiskUpload startUploadSingle(String creator, String name,
            String type, long size, long folderId, String folderPath,
            String hashCode, long lastModified, String userId) {
        if (folderPath == null) {
            folderPath = "";
        }

        Date now = new Date();
        DiskUpload diskUpload = new DiskUpload();
        diskUpload.setName(name);
        diskUpload.setType(type);
        diskUpload.setSize(size);
        diskUpload.setLastModified(lastModified);
        diskUpload.setCatalog("root");
        diskUpload.setUserId(userId);
        diskUpload.setInfoId(0L);
        diskUpload.setFileId(0L);
        diskUpload.setFolderId(folderId);
        diskUpload.setFolderPath(folderPath);
        diskUpload.setPartType("single");
        diskUpload.setPartIndex(0);
        diskUpload.setPartParent(0L);
        diskUpload.setRangeStart(0L);
        diskUpload.setRangeEnd(0L);
        diskUpload.setStartTime(now);
        diskUpload.setEndTime(now);
        diskUpload.setReason("");
        diskUpload.setCreator(creator);
        diskUpload.setCreateTime(now);
        diskUpload.setUpdater(creator);
        diskUpload.setUpdateTime(now);
        diskUpload.setStatus("active");
        diskUpload.setTenantId("1");
        diskUploadManager.save(diskUpload);

        // check hashCode
        // DiskFile diskFile = this.findFileByHashCode(hashCode);

        // if (diskFile == null) {
        // return diskUpload;
        // }

        // diskUpload.setFileId(diskFile.getId());
        // diskUploadManager.save(diskUpload);
        return diskUpload;
    }

    /**
     * 记录上传整个文件结束.
     */
    public DiskUpload endUploadSingle(long uploadId, String status,
            String reason, long infoId) {
        if (reason == null) {
            reason = "";
        }

        DiskInfo diskInfo = diskInfoManager.get(infoId);
        DiskFile diskFile = diskFileManager.findUniqueBy("value",
                diskInfo.getRef());
        DiskUpload diskUpload = diskUploadManager.get(uploadId);
        Date now = new Date();
        diskUpload.setInfoId(infoId);
        diskUpload.setFileId(diskFile.getId());

        if (diskUpload.getFolderId() == 0L) {
            diskUpload.setFolderId(diskInfo.getDiskInfo().getId());
        }

        diskUpload.setEndTime(now);
        diskUpload.setStatus(status);
        diskUpload.setReason(reason);
        diskUploadManager.save(diskUpload);

        return diskUpload;
    }

    /**
     * 记录分片上传文件开始.
     */
    public DiskUpload startUploadPartParent(String creator, String name,
            String type, long size, long folderId, String folderPath,
            long lastModified, String userId) {
        if (folderPath == null) {
            folderPath = "";
        }

        Date now = new Date();
        DiskUpload diskUpload = new DiskUpload();
        diskUpload.setName(name);
        diskUpload.setType(type);
        diskUpload.setSize(size);
        diskUpload.setCatalog("root");
        diskUpload.setUserId(userId);
        diskUpload.setInfoId(0L);
        diskUpload.setFileId(0L);
        diskUpload.setFolderId(folderId);
        diskUpload.setFolderPath(folderPath);
        diskUpload.setPartType("part");
        diskUpload.setPartIndex(0);
        diskUpload.setPartParent(0L);
        diskUpload.setRangeStart(0L);
        diskUpload.setRangeEnd(0L);
        diskUpload.setStartTime(now);
        diskUpload.setEndTime(now);
        diskUpload.setReason("");
        diskUpload.setCreator(creator);
        diskUpload.setCreateTime(now);
        diskUpload.setUpdater(creator);
        diskUpload.setUpdateTime(now);
        diskUpload.setStatus("active");
        diskUpload.setTenantId("1");
        diskUploadManager.save(diskUpload);

        return diskUpload;
    }

    /**
     * 记录上传一个分片完成.
     */
    public DiskUpload endUploadPartChild(String userId, long parentUploadId,
            int partIndex, long fileId) {
        DiskFile diskFile = diskFileManager.get(fileId);
        Date now = new Date();
        DiskUpload diskUpload = new DiskUpload();
        diskUpload.setName(diskFile.getName());
        diskUpload.setType(diskFile.getType());
        diskUpload.setSize(diskFile.getFileSize());
        diskUpload.setLastModified(0L);
        diskUpload.setCatalog("part");
        diskUpload.setUserId("");
        diskUpload.setInfoId(0L);
        diskUpload.setFileId(fileId);
        diskUpload.setFolderId(0L);
        diskUpload.setFolderPath("");
        diskUpload.setPartType("part");
        diskUpload.setPartIndex(partIndex);
        diskUpload.setPartParent(parentUploadId);
        diskUpload.setRangeStart(0L);
        diskUpload.setRangeEnd(0L);
        diskUpload.setStartTime(now);
        diskUpload.setEndTime(now);
        diskUpload.setReason("");
        diskUpload.setCreator(userId);
        diskUpload.setCreateTime(now);
        diskUpload.setUpdater(userId);
        diskUpload.setUpdateTime(now);
        diskUpload.setStatus("success");
        diskUpload.setTenantId("1");
        diskUploadManager.save(diskUpload);

        return diskUpload;
    }

    /**
     * 记录上传分片整体完成.
     */
    public DiskUpload endUploadPartParent(long uploadId, String status,
            String reason, long infoId) {
        if (reason == null) {
            reason = "";
        }

        DiskInfo diskInfo = diskInfoManager.get(infoId);
        DiskFile diskFile = diskFileManager.findUniqueBy("value",
                diskInfo.getRef());
        DiskUpload diskUpload = diskUploadManager.get(uploadId);
        Date now = new Date();
        diskUpload.setInfoId(infoId);
        diskUpload.setFileId(diskFile.getId());

        if (diskUpload.getFolderId() == 0L) {
            diskUpload.setFolderId(diskInfo.getDiskInfo().getId());
        }

        diskUpload.setEndTime(now);
        diskUpload.setStatus(status);
        diskUpload.setReason(reason);
        diskUploadManager.save(diskUpload);

        return diskUpload;
    }

    // ~
    @Resource
    public void setDiskInfoManager(DiskInfoManager diskInfoManager) {
        this.diskInfoManager = diskInfoManager;
    }

    @Resource
    public void setDiskFileManager(DiskFileManager diskFileManager) {
        this.diskFileManager = diskFileManager;
    }

    @Resource
    public void setDiskUploadManager(DiskUploadManager diskUploadManager) {
        this.diskUploadManager = diskUploadManager;
    }

    @Resource
    public void setStoreClient(StoreClient storeClient) {
        this.storeClient = storeClient;
    }

    @Resource
    public void setDiskLogInternalService(
            DiskLogInternalService diskLogInternalService) {
        this.diskLogInternalService = diskLogInternalService;
    }

    @Resource
    public void setDiskVersionInternalService(
            DiskVersionInternalService diskVersionInternalService) {
        this.diskVersionInternalService = diskVersionInternalService;
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

    public void setAutoUncompress(boolean autoUncompress) {
        this.autoUncompress = autoUncompress;
    }
}
