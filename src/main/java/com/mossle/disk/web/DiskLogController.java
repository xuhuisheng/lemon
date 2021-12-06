package com.mossle.disk.web;

import javax.annotation.Resource;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.page.Page;

import com.mossle.disk.service.internal.DiskLogInternalService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("disk/log")
public class DiskLogController {
    private static Logger logger = LoggerFactory
            .getLogger(DiskLogController.class);
    private TenantHolder tenantHolder;
    private CurrentUserHolder currentUserHolder;
    private DiskLogInternalService diskLogInternalService;

    /**
     * 查看当前文件或文件夹日志.
     */
    @RequestMapping("source")
    public String sourceLogs(@RequestParam("sourceId") Long sourceId,
            Page page, Model model) throws Exception {
        logger.debug("source");

        String userId = currentUserHolder.getUserId();
        String tenantId = tenantHolder.getTenantId();

        page = this.diskLogInternalService.findSourceLogs(sourceId, page);
        model.addAttribute("page", page);

        return "disk/log/source";
    }

    /**
     * 查看一个文件夹下的所有日志.
     */
    @RequestMapping("parent")
    public String parentLogs(@RequestParam("parentId") Long parentId,
            Page page, Model model) throws Exception {
        String userId = currentUserHolder.getUserId();
        String tenantId = tenantHolder.getTenantId();

        page = this.diskLogInternalService.findParentLogs(parentId, page);
        model.addAttribute("page", page);

        return "disk/log/parent";
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
    public void setDiskLogInternalService(
            DiskLogInternalService diskLogInternalService) {
        this.diskLogInternalService = diskLogInternalService;
    }
}
