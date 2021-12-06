package com.mossle.disk.web.api;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.store.MultipartFileDataSource;
import com.mossle.core.util.BaseDTO;

import com.mossle.disk.persistence.domain.DiskUpload;
import com.mossle.disk.service.DiskUploadService;
import com.mossle.disk.support.DiskAclException;
import com.mossle.disk.support.UploadResult;

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

@Tag(name = "服务端")
@RestController
@RequestMapping("disk/api/upload")
public class DiskUploadApiController {
    private static Logger logger = LoggerFactory
            .getLogger(DiskUploadApiController.class);
    private TenantHolder tenantHolder;
    private DiskUploadService diskUploadService;

    /**
     * 上传文件.
     */
    @Operation(summary = "上传整个文件")
    @RequestMapping("single")
    public Map<String, Object> upload(
            @Parameter(description = "文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "文件夹code") @RequestParam("folderCode") Long folderId,
            @Parameter(description = "最后更新时间") @RequestParam("lastModified") long lastModified)
            throws Exception {
        logger.debug("lastModified : {}", lastModified);

        String tenantId = tenantHolder.getTenantId();

        try {
            String userId = "system";
            UploadResult uploadResult = this.diskUploadService.uploadInternal(
                    folderId, userId, new MultipartFileDataSource(file),
                    file.getOriginalFilename(), file.getSize(), tenantId);
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
     * 准备批量上传.
     */
    @Operation(summary = "准备批量上传")
    @RequestMapping(value = "part/start", method = RequestMethod.POST)
    public BaseDTO createBatch(
            @Parameter(description = "文件名") @RequestParam("fileName") String fileName,
            @Parameter(description = "文件大小") @RequestParam("fileSize") long fileSize,
            @Parameter(description = "应用id") @RequestParam("appId") String appId,
            @Parameter(description = "所有者") @RequestParam("userId") String userId,
            @Parameter(description = "文件夹code") @RequestParam(value = "folderCode", required = false) Long folderId,
            @Parameter(description = "文件夹路径") @RequestParam(value = "folderPath", required = false) String folderPath,
            @Parameter(description = "哈希值") @RequestParam(value = "hashCode", required = false) String hashCode,
            @Parameter(description = "最后修改时间") @RequestParam(value = "lastModified", required = false) Long lastModified) {
        DiskUpload diskUpload = diskUploadService.createBatch(fileName,
                fileSize, appId, userId, folderId, folderPath, hashCode,
                lastModified);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("uploadCode", diskUpload.getId());

        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(0);
        baseDto.setMessage("success");
        baseDto.setData(data);

        return baseDto;
    }

    /**
     * 上传一个分片.
     */
    @Operation(summary = "上传分片")
    @RequestMapping(value = "part/upload", method = RequestMethod.POST)
    public BaseDTO uploadPart(
            @Parameter(description = "分片") @RequestParam("part") MultipartFile file,
            @Parameter(description = "上传任务code") @RequestParam("uploadCode") long uploadCode,
            @Parameter(description = "分片序号") @RequestParam("partIndex") int partIndex,
            @Parameter(description = "应用id") @RequestParam("appId") String appId)
            throws Exception {
        // file.transferTo(new java.io.File("test.txt"));
        DiskUpload diskUpload = diskUploadService.uploadPart(uploadCode,
                file.getOriginalFilename(), file.getSize(), appId, partIndex,
                new MultipartFileDataSource(file));
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("uploadCode", diskUpload.getId());

        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(0);
        baseDto.setMessage("success");
        baseDto.setData(data);

        return baseDto;
    }

    /**
     * 分片上传结束.
     */
    @Operation(summary = "分片上传完成")
    @RequestMapping("part/end")
    public BaseDTO uploadPartComplete(
            @Parameter(description = "上传任务code") @RequestParam("uploadCode") long uploadCode,
            @Parameter(description = "应用id") @RequestParam("appId") String appId,
            @Parameter(description = "上传状态") @RequestParam("status") String status,
            @Parameter(description = "失败原因") @RequestParam("reason") String reason)
            throws Exception {
        DiskUpload diskUpload = diskUploadService.uploadPartComplete(
                uploadCode, status, reason);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("uploadCode", diskUpload.getId());

        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(0);
        baseDto.setMessage("success");
        baseDto.setData(data);

        return baseDto;
    }

    // ~ ======================================================================
    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }

    @Resource
    public void setDiskUploadService(DiskUploadService diskUploadService) {
        this.diskUploadService = diskUploadService;
    }
}
