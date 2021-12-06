package com.mossle.disk.web.view;

import javax.annotation.Resource;

import com.mossle.api.auth.CurrentUserHolder;

import com.mossle.disk.persistence.domain.DiskInfo;
import com.mossle.disk.persistence.domain.DiskSpace;
import com.mossle.disk.service.DiskSpaceService;
import com.mossle.disk.service.internal.DiskQueryInternalService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("disk/view")
public class DiskViewController {
    private static Logger logger = LoggerFactory
            .getLogger(DiskViewController.class);
    private CurrentUserHolder currentUserHolder;
    private DiskSpaceService diskSpaceService;
    private DiskQueryInternalService diskQueryInternalService;

    @RequestMapping("default")
    public String viewDefault(Model model) {
        logger.debug("debug");

        String userId = currentUserHolder.getUserId();

        DiskSpace diskSpace = this.diskSpaceService.findDefaultRepoSpace();
        DiskInfo diskInfo = this.diskQueryInternalService
                .findDefaultRepoSpaceRoot();

        return "redirect:/disk/folder/" + diskInfo.getId();
    }

    // ~ ======================================================================
    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setDiskSpaceService(DiskSpaceService diskSpaceService) {
        this.diskSpaceService = diskSpaceService;
    }

    @Resource
    public void setDiskQueryInternalService(
            DiskQueryInternalService diskQueryInternalService) {
        this.diskQueryInternalService = diskQueryInternalService;
    }
}
