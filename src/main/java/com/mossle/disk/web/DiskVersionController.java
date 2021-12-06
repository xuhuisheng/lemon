package com.mossle.disk.web;

import java.io.InputStream;

import java.util.zip.*;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.util.ServletUtils;

import com.mossle.disk.persistence.domain.DiskVersion;
import com.mossle.disk.persistence.manager.DiskVersionManager;
import com.mossle.disk.service.internal.DiskVersionInternalService;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("disk/version")
public class DiskVersionController {
    private static Logger logger = LoggerFactory
            .getLogger(DiskVersionController.class);
    private TenantHolder tenantHolder;
    private CurrentUserHolder currentUserHolder;
    private DiskVersionInternalService diskVersionInternalService;
    private DiskVersionManager diskVersionManager;

    /**
     * 详情.
     */
    @RequestMapping("view")
    public String view(@RequestParam("versionId") Long versionId, Model model) {
        logger.debug("view");

        String userId = currentUserHolder.getUserId();
        DiskVersion diskVersion = diskVersionManager.get(versionId);
        model.addAttribute("diskVersion", diskVersion);

        return "disk/version/view";
    }

    /**
     * 下载.
     */
    @RequestMapping("download")
    public void fileDownload(@RequestParam("versionId") Long versionId,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        String userId = currentUserHolder.getUserId();
        DiskVersion diskVersion = diskVersionManager.get(versionId);
        InputStream is = null;

        try {
            ServletUtils.setFileDownloadHeader(request, response,
                    diskVersion.getName());

            is = this.diskVersionInternalService.findDownloadInputStream(
                    versionId, userId, tenantId);
            IOUtils.copy(is, response.getOutputStream());
        } finally {
            if (is != null) {
                is.close();
            }
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
    public void setDiskVersionInternalService(
            DiskVersionInternalService diskVersionInternalService) {
        this.diskVersionInternalService = diskVersionInternalService;
    }

    @Resource
    public void setDiskVersionManager(DiskVersionManager diskVersionManager) {
        this.diskVersionManager = diskVersionManager;
    }
}
