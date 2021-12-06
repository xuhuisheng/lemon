package com.mossle.disk.web.rs;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.store.MultipartFileDataSource;

import com.mossle.disk.component.DiskInfoConverter;
import com.mossle.disk.persistence.domain.DiskInfo;
import com.mossle.disk.persistence.domain.DiskUpload;
import com.mossle.disk.service.DiskUploadService;
import com.mossle.disk.support.DiskAclException;
import com.mossle.disk.support.DiskInfoDTO;
import com.mossle.disk.support.Result;
import com.mossle.disk.support.UploadResult;
import com.mossle.disk.util.FileUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "文件")
@RestController
@RequestMapping("disk/rs/upload")
public class DiskUploadRestController {
    private static Logger logger = LoggerFactory
            .getLogger(DiskFileRestController.class);
    private TenantHolder tenantHolder;
    private CurrentUserHolder currentUserHolder;
    private DiskUploadService diskUploadService;
    private DiskInfoConverter diskInfoConverter;

    /**
     * 简单上传一个文件.
     */
    @Operation(summary = "简单上传一个文件")
    @RequestMapping(value = "upload", method = RequestMethod.POST)
    public Result singleUpload(
            @Parameter(description = "文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "上级文件夹") @RequestParam("folderCode") Long folderCode,
            @Parameter(description = "哈希摘要") @RequestParam(value = "hashCode", required = false) String hashCode,
            @Parameter(description = "最后修改时间") @RequestParam(value = "lastModified", required = false) Long lastModified)
            throws Exception {
        logger.debug("single upload");

        String userId = currentUserHolder.getUserId();
        String tenantId = tenantHolder.getTenantId();

        if (folderCode == null) {
            folderCode = 0L;
        }

        if (lastModified == null) {
            lastModified = 0L;
        }

        try {
            UploadResult uploadResult = this.diskUploadService.uploadFile(
                    folderCode, userId, new MultipartFileDataSource(file),
                    file.getOriginalFilename(), file.getSize(), hashCode,
                    lastModified, tenantId);

            DiskInfo diskInfo = uploadResult.getFile();
            DiskInfoDTO diskInfoDto = diskInfoConverter.convertOne(diskInfo);

            return Result.success(diskInfoDto);
        } catch (DiskAclException ex) {
            return Result.failure(403, ex.getAction());
        }
    }

    /**
     * 准备上传单个文件.
     */

    // @Operation(summary = "准备上传单个文件")
    @RequestMapping(value = "single/start", method = RequestMethod.POST)
    public Result singleStart(
            @Parameter(description = "文件名") @RequestParam("fileName") String fileName,
            @Parameter(description = "文件大小") @RequestParam("fileSize") long fileSize,
            @Parameter(description = "上级文件夹") @RequestParam("folderCode") Long folderCode,
            @Parameter(description = "哈希摘要") @RequestParam(value = "hashCode", required = false) String hashCode,
            @Parameter(description = "最后修改时间") @RequestParam(value = "lastModified", required = false) Long lastModified)
            throws Exception {
        String userId = currentUserHolder.getUserId();
        String tenantId = tenantHolder.getTenantId();
        String type = FileUtils.getSuffix(fileName);
        DiskUpload diskUpload = diskUploadService.startUploadSingle(userId,
                fileName, type, fileSize, folderCode, "", hashCode, 0L, userId);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("uploadCode", diskUpload.getId());

        return Result.success(diskUpload.getId());
    }

    /**
     * 上传单个文件.
     */

    // @Operation(summary = "上传单个文件")
    @RequestMapping(value = "single/upload", method = RequestMethod.POST)
    public Map<String, Object> singleUpload(
            @Parameter(description = "文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "上传任务code") @RequestParam("uploadCode") Long uploadCode)
            throws Exception {
        logger.debug("single upload");

        String userId = currentUserHolder.getUserId();
        String tenantId = tenantHolder.getTenantId();

        try {
            // UploadResult uploadResult = this.diskUploadService.uploadFile(
            // folderId, userId, new MultipartFileDataSource(file),
            // file.getOriginalFilename(), file.getSize(), tenantId);
            UploadResult uploadResult = this.diskUploadService
                    .uploadSingleFile(uploadCode, new MultipartFileDataSource(
                            file), tenantId);
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("code", uploadResult.getCode());
            result.put("message", uploadResult.getMessage());
            result.put("file", uploadResult.getFile().getId());

            if (uploadResult.getVersion() != null) {
                result.put("version", uploadResult.getVersion().getId());
            }

            return result;
        } catch (DiskAclException ex) {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("code", 403);
            result.put("message", ex.getAction());

            return result;
        }
    }

    /**
     * 单个文件上传完成.
     */

    // @Operation(summary = "单个文件上传完成")
    @RequestMapping(value = "single/end", method = RequestMethod.POST)
    public Result singleEnd(
            @Parameter(description = "上传任务code") @RequestParam("uploadCode") Long uploadCode,
            @RequestParam("status") String status,
            @RequestParam("reason") String reason) {
        this.diskUploadService.endUploadSingle(uploadCode, status, reason, 0L);

        return Result.success();
    }

    /**
     * 准备批量上传.
     */
    @Operation(summary = "准备批量上传")
    @RequestMapping(value = "part/start", method = RequestMethod.POST)
    public Result createBatch(
            @Parameter(description = "文件名") @RequestParam("fileName") String fileName,
            @Parameter(description = "文件大小") @RequestParam("fileSize") long fileSize,
            @Parameter(description = "文件夹code") @RequestParam(value = "folderCode", required = false) Long folderId,
            @Parameter(description = "文件夹路径") @RequestParam(value = "folderPath", required = false) String folderPath,
            @Parameter(description = "哈希值") @RequestParam(value = "hashCode", required = false) String hashCode,
            @Parameter(description = "最后修改时间") @RequestParam(value = "lastModified", required = false) Long lastModified) {
        String appId = "web";
        String userId = currentUserHolder.getUserId();
        DiskUpload diskUpload = diskUploadService.createBatch(fileName,
                fileSize, appId, userId, folderId, folderPath, hashCode,
                lastModified);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("uploadCode", diskUpload.getId());

        return Result.success(data);
    }

    /**
     * 上传一个分片.
     */
    @Operation(summary = "上传分片")
    @RequestMapping(value = "part/upload", method = RequestMethod.POST)
    public Result uploadPart(
            @Parameter(description = "分片") @RequestParam("part") MultipartFile file,
            @Parameter(description = "上传任务code") @RequestParam("uploadCode") long uploadCode,
            @Parameter(description = "分片序号") @RequestParam("partIndex") int partIndex)
            throws Exception {
        String appId = "web";
        String userId = currentUserHolder.getUserId();

        // file.transferTo(new java.io.File("test.txt"));
        DiskUpload diskUpload = diskUploadService.uploadPart(uploadCode,
                file.getOriginalFilename(), file.getSize(), appId, partIndex,
                new MultipartFileDataSource(file));
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("uploadCode", diskUpload.getId());

        return Result.success(data);
    }

    /**
     * 分片上传结束.
     */
    @Operation(summary = "分片上传完成")
    @RequestMapping("part/end")
    public Result uploadPartComplete(
            @Parameter(description = "上传任务code") @RequestParam("uploadCode") long uploadCode,
            @Parameter(description = "上传状态") @RequestParam("status") String status,
            @Parameter(description = "失败原因") @RequestParam("reason") String reason)
            throws Exception {
        String appId = "web";
        String userId = currentUserHolder.getUserId();
        DiskUpload diskUpload = diskUploadService.uploadPartComplete(
                uploadCode, status, reason);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("uploadCode", diskUpload.getId());

        return Result.success(data);
    }

    // ~ ======================================================================
    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setDiskUploadService(DiskUploadService diskUploadService) {
        this.diskUploadService = diskUploadService;
    }

    @Resource
    public void setDiskInfoConverter(DiskInfoConverter diskInfoConverter) {
        this.diskInfoConverter = diskInfoConverter;
    }
}
