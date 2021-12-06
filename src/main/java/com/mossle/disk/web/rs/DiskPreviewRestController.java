package com.mossle.disk.web.rs;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import java.util.HashMap;
import java.util.Map;

import javax.activation.DataSource;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.api.store.StoreDTO;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.client.store.StoreClient;

import com.mossle.core.store.ByteArrayDataSource;
import com.mossle.core.util.ServletUtils;

import com.mossle.disk.persistence.domain.DiskInfo;
import com.mossle.disk.service.DiskDownloadService;
import com.mossle.disk.service.DiskFileService;
import com.mossle.disk.service.internal.DiskBaseInternalService;
import com.mossle.disk.service.internal.DiskLogInternalService;
import com.mossle.disk.support.DiskAclException;
import com.mossle.disk.support.PreviewHelper;
import com.mossle.disk.support.Result;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "文件")
@RestController
@RequestMapping("disk/rs/preview")
public class DiskPreviewRestController {
    private static Logger logger = LoggerFactory
            .getLogger(DiskPreviewRestController.class);
    private TenantHolder tenantHolder;
    private CurrentUserHolder currentUserHolder;
    private StoreClient storeClient;
    private DiskFileService diskFileService;
    private DiskDownloadService diskDownloadService;
    private DiskBaseInternalService diskBaseInternalService;
    private DiskLogInternalService diskLogInternalService;
    private PreviewHelper previewHelper;

    /**
     * 检查预览状态.
     */
    @Operation(summary = "检查预览状态")
    @RequestMapping(value = "check", method = RequestMethod.GET)
    public Result checkPreview(
            @Parameter(description = "文件code") @RequestParam("code") Long fileCode)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        String userId = currentUserHolder.getUserId();

        try {
            DiskInfo diskInfo = this.diskFileService.findFile(fileCode, userId);

            String previewType = this.previewHelper.findPreviewType(diskInfo
                    .getType());
            logger.info("type : {}", diskInfo.getType());
            logger.info("preview type : {}", previewType);

            // model.addAttribute("diskInfo", diskInfo);
            if (previewType == null) {
                Map<String, Object> data = new HashMap<String, Object>();
                data.put("previewAllow", false);
                data.put("previewType", "");
                data.put("type", diskInfo.getType());

                return Result.success(data);
            }

            this.diskLogInternalService.recordOpen(fileCode, userId);

            this.convertPreview(diskInfo, tenantId, userId, previewType,
                    diskInfo.getType());

            // if ("txt".equals(previewType)) {
            // InputStream is = this.diskFileService.findPreviewInputStream(
            // fileId, userId, tenantId);
            // ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // IOUtils.copy(is, baos);
            // // model.addAttribute("text", new String(baos.toByteArray(), "utf-8"));
            // }
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("previewAllow", true);
            data.put("previewType", previewType);
            data.put("type", diskInfo.getType());

            return Result.success(data);
        } catch (DiskAclException ex) {
            return Result.failure(403, ex.getAction());
        }
    }

    /**
     * 下载预览文件.
     */
    @Operation(summary = "下载预览文件")
    @RequestMapping(value = "download/{code}", method = RequestMethod.GET)
    public void previewDownloadPdf(
            @Parameter(description = "文件code") @PathVariable("code") Long fileCode,
            @Parameter(description = "访问帐号") @RequestParam(value = "username", required = false) String username,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        String userId = currentUserHolder.getUserId();

        if (StringUtils.isBlank(userId)) {
            userId = username;
        }

        DiskInfo diskInfo = this.diskFileService.findFile(fileCode, userId);
        InputStream is = null;

        try {
            String previewType = this.previewHelper.findPreviewType(diskInfo
                    .getType());

            if ("pdf".equals(previewType)) {
                ServletUtils.setFileDownloadHeader(request, response,
                        diskInfo.getName() + ".pdf");
            } else {
                ServletUtils.setFileDownloadHeader(request, response,
                        diskInfo.getName());
            }

            Result<InputStream> result = this.diskDownloadService
                    .findPreviewInputStream(fileCode, userId, tenantId);

            if (result.isFailure()) {
                return;
            }

            is = result.getData();

            if (is == null) {
                logger.info("cannot find preview inputstream : {}", fileCode);

                return;
            }

            IOUtils.copy(is, response.getOutputStream());
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public void convertPreview(DiskInfo diskInfo, String tenantId,
            String userId, String previewType, String type) {
        if ("success".equals(diskInfo.getPreviewStatus())) {
            logger.debug("already convert preview : {}", diskInfo.getId());

            return;
        }

        try {
            Long fileId = diskInfo.getId();
            Result<InputStream> result = this.diskBaseInternalService
                    .findInputStream(fileId);

            if (result.isFailure()) {
                return;
            }

            InputStream is = result.getData();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            boolean success = previewHelper.convertPreview(type, is, baos);

            if (success) {
                // String modelName = "disk/" + diskInfo.getDiskSpace().getId();
                String modelName = "disk";

                // String keyName = diskInfo.getName() + ".pdf";
                DataSource dataSource = new ByteArrayDataSource(
                        baos.toByteArray());
                StoreDTO storeDto = storeClient.saveStore(modelName,
                        dataSource, tenantId);
                diskInfo.setPreviewRef(storeDto.getKey());
                diskInfo.setPreviewStatus("success");
                diskFileService.save(diskInfo);
            } else {
                diskInfo.setPreviewRef(diskInfo.getRef());
                diskInfo.setPreviewStatus("success");
                diskFileService.save(diskInfo);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
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
    public void setStoreClient(StoreClient storeClient) {
        this.storeClient = storeClient;
    }

    @Resource
    public void setDiskFileService(DiskFileService diskFileService) {
        this.diskFileService = diskFileService;
    }

    @Resource
    public void setDiskDownloadService(DiskDownloadService diskDownloadService) {
        this.diskDownloadService = diskDownloadService;
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
    public void setPreviewHelper(PreviewHelper previewHelper) {
        this.previewHelper = previewHelper;
    }
}
