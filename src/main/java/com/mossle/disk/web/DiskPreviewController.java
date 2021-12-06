package com.mossle.disk.web;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.activation.DataSource;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.api.store.StoreDTO;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.client.store.StoreClient;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.store.ByteArrayDataSource;
import com.mossle.core.util.ServletUtils;

import com.mossle.disk.persistence.domain.DiskInfo;
import com.mossle.disk.service.DiskDownloadService;
import com.mossle.disk.service.DiskFileService;
import com.mossle.disk.service.internal.DiskBaseInternalService;
import com.mossle.disk.service.internal.DiskLogInternalService;
import com.mossle.disk.support.PreviewHelper;
import com.mossle.disk.support.Result;
import com.mossle.disk.support.TreeNode;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("disk/preview")
public class DiskPreviewController {
    private static Logger logger = LoggerFactory
            .getLogger(DiskPreviewController.class);
    private StoreClient storeClient;
    private TenantHolder tenantHolder;
    private CurrentUserHolder currentUserHolder;
    private DiskFileService diskFileService;
    private DiskDownloadService diskDownloadService;
    private DiskLogInternalService diskLogInternalService;
    private DiskBaseInternalService diskBaseInternalService;
    private PreviewHelper previewHelper;
    private JsonMapper jsonMapper = new JsonMapper();
    private long maxSize;
    private String previewBaseUrl;
    private String previewAppId;

    /**
     * 预览.
     */
    @RequestMapping("{fileId}")
    public String preview(@PathVariable("fileId") Long fileId, Model model)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        String userId = currentUserHolder.getUserId();

        DiskInfo diskInfo = this.diskFileService.findFile(fileId, userId);

        if (diskInfo.getFileSize() > maxSize) {
            logger.info("over max size : {} {}", maxSize,
                    diskInfo.getFileSize());

            return "redirect:/disk/file/" + fileId;
        }

        String previewType = this.previewHelper.findPreviewType(diskInfo
                .getType());
        logger.info("type : {}", diskInfo.getType());
        logger.info("preview type : {}", previewType);
        model.addAttribute("diskInfo", diskInfo);

        if (previewType == null) {
            return "redirect:/disk/file/" + fileId;
        }

        // this.convertPdf(diskInfo, tenantId, userId, previewType,
        // diskInfo.getType());
        // this.convertImage(diskInfo, previewType);
        // this.convertTxt(diskInfo, previewType);
        // this.convertZip(diskInfo, previewType);
        // this.convertDwg(diskInfo, tenantId, userId, previewType,
        // diskInfo.getType());

        // if ("txt".equals(previewType)) {
        // Result<InputStream> result = this.diskDownloadService
        // .findPreviewInputStream(fileId, userId, tenantId);

        // if (result.isSuccess()) {
        // ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // InputStream is = result.getData();
        // IOUtils.copy(is, baos);
        // model.addAttribute("text", new String(baos.toByteArray(),
        // "utf-8"));
        // }
        // }
        this.diskLogInternalService.recordOpen(fileId, userId);

        // return "disk/preview/preview-" + previewType;
        return "redirect:" + previewBaseUrl + "?appKey=" + previewAppId
                + "&fileToken=" + diskInfo.getId();
    }

    /**
     * 预览zip.
     */
    @RequestMapping("zip/tree")
    @ResponseBody
    public String previewZipTree(@RequestParam("id") Long id, Model model)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        String userId = currentUserHolder.getUserId();

        DiskInfo diskInfo = this.diskFileService.findFile(id, userId);
        Result<InputStream> result = this.diskDownloadService
                .findPreviewInputStream(id, userId, tenantId);

        if (result.isFailure()) {
            return "[]";
        }

        InputStream is = result.getData();

        ZipInputStream zis = new ZipInputStream(is);
        List<String> list = new ArrayList<String>();

        while (true) {
            ZipEntry ze = zis.getNextEntry();

            if (ze == null) {
                break;
            }

            // System.out.println(ze.getName());
            list.add(ze.getName());
        }

        Collections.sort(list);

        TreeNode root = new TreeNode();
        root.setId("");
        root.setName(diskInfo.getName());
        root.setType("folder");

        for (String text : list) {
            // System.out.println(text);
            String[] array = text.split("/");

            TreeNode current = root;
            String path = "";

            for (String part : array) {
                path += (part + "/");
                current = current.createOrFindChild(part);
                // System.out.println("part : " + part);
                current.setId(path);
            }

            if (!text.endsWith("/")) {
                current.setType("file");
                current.setId(current.getId().substring(0,
                        current.getId().length() - 1));
            }
        }

        return jsonMapper.toJson(root);
    }

    /**
     * 预览zip，下载.
     */
    @RequestMapping("zip/download")
    public void previewZipDownload(@RequestParam("id") Long id,
            @RequestParam("path") String path, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String tenantId = tenantHolder.getTenantId();
        String userId = currentUserHolder.getUserId();

        DiskInfo diskInfo = this.diskFileService.findFile(id, userId);
        Result<InputStream> result = this.diskDownloadService
                .findPreviewInputStream(id, userId, tenantId);

        if (result.isFailure()) {
            return;
        }

        InputStream is = result.getData();
        ZipInputStream zis = new ZipInputStream(is);
        List<String> list = new ArrayList<String>();

        ZipEntry ze = zis.getNextEntry();

        while (ze != null) {
            if (path.equals(ze.getName())) {
                break;
            }

            // zis.closeEntry();
            ze = zis.getNextEntry();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int len = -1;
        byte[] b = new byte[1024];

        while ((len = zis.read(b, 0, 1024)) != -1) {
            baos.write(b, 0, len);
        }

        baos.flush();

        ServletUtils.setFileDownloadHeader(request, response, path);
        response.getOutputStream().write(baos.toByteArray());
    }

    /**
     * 预览pdf.
     */
    @RequestMapping("download/{id}")
    public void previewDownloadPdf(@PathVariable("id") Long id,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        String userId = currentUserHolder.getUserId();
        DiskInfo diskInfo = this.diskFileService.findFile(id, userId);
        InputStream is = null;

        try {
            ServletUtils.setFileDownloadHeader(request, response,
                    diskInfo.getName() + ".pdf");

            Result<InputStream> result = this.diskDownloadService
                    .findPreviewInputStream(id, userId, tenantId);

            if (result.isFailure()) {
                return;
            }

            is = result.getData();

            if (is == null) {
                logger.info("cannot find preview inputstream : {}", id);

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

    /**
     * 预览下载.
     */
    @RequestMapping("preview-download/{id}")
    public void previewDownload(@PathVariable("id") Long id,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        String userId = currentUserHolder.getUserId();
        DiskInfo diskInfo = this.diskFileService.findFile(id, userId);
        InputStream is = null;

        try {
            ServletUtils.setFileDownloadHeader(request, response,
                    diskInfo.getName());

            Result<InputStream> result = this.diskDownloadService
                    .findPreviewInputStream(id, userId, tenantId);

            if (result.isFailure()) {
                return;
            }

            is = result.getData();
            IOUtils.copy(is, response.getOutputStream());
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    // ~ ======================================================================
    @Resource
    public void setStoreClient(StoreClient storeClient) {
        this.storeClient = storeClient;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
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
    public void setDiskLogInternalService(
            DiskLogInternalService diskLogInternalService) {
        this.diskLogInternalService = diskLogInternalService;
    }

    @Resource
    public void setDiskBaseInternalService(
            DiskBaseInternalService diskBaseInternalService) {
        this.diskBaseInternalService = diskBaseInternalService;
    }

    @Resource
    public void setPreviewHelper(PreviewHelper previewHelper) {
        this.previewHelper = previewHelper;
    }

    @Value("${disk.preview.maxSize}")
    public void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
    }

    @Value("${disk.preview.baseUrl}")
    public void setPreviewBaseUrl(String previewBaseUrl) {
        this.previewBaseUrl = previewBaseUrl;
    }

    @Value("${disk.preview.appId}")
    public void setPreviewAppId(String previewAppId) {
        this.previewAppId = previewAppId;
    }
}
