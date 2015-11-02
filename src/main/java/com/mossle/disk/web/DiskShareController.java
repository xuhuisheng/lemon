package com.mossle.disk.web;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.core.auth.CurrentUserHolder;

import com.mossle.disk.persistence.domain.DiskInfo;
import com.mossle.disk.persistence.domain.DiskShare;
import com.mossle.disk.persistence.manager.DiskInfoManager;
import com.mossle.disk.persistence.manager.DiskShareManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("disk")
public class DiskShareController {
    private static Logger logger = LoggerFactory
            .getLogger(DiskShareController.class);
    private DiskShareManager diskShareManager;
    private DiskInfoManager diskInfoManager;
    private CurrentUserHolder currentUserHolder;

    /**
     * 列表显示.
     */
    @RequestMapping("disk-share-list")
    public String list(
            @RequestParam(value = "path", required = false) String path,
            Model model) {
        if (path == null) {
            path = "";
        }

        String userId = currentUserHolder.getUserId();

        // List<DiskShare> diskShares = diskService.listFiles(userId, path);
        List<DiskShare> diskShares = diskShareManager.findBy("creator", userId);
        model.addAttribute("diskShares", diskShares);
        model.addAttribute("path", path);

        return "disk/disk-share-list";
    }

    /**
     * 详情.
     */
    @RequestMapping("disk-share-view")
    public String view(@RequestParam("id") Long id, Model model) {
        DiskShare diskShare = diskShareManager.get(id);
        model.addAttribute("diskShare", diskShare);

        return "disk/disk-share-view";
    }

    /**
     * 分享.
     */
    @RequestMapping("disk-share-sharePublic")
    public String sharePublic(@RequestParam("id") Long id) {
        DiskInfo diskInfo = diskInfoManager.get(id);
        DiskShare diskShare = diskShareManager.findUniqueBy("diskInfo",
                diskInfo);

        if (diskShare != null) {
            return "redirect:/disk/disk-share-list.do";
        }

        diskShare = new DiskShare();
        diskShare.setShareType("public");
        diskShare.setShareTime(new Date());
        diskShare.setDiskInfo(diskInfo);
        diskShare.setName(diskInfo.getName());
        diskShare.setCreator(diskInfo.getCreator());
        diskShare.setType(diskInfo.getType());
        diskShare.setDirType(diskInfo.getDirType());
        diskShare.setCountView(0);
        diskShare.setCountSave(0);
        diskShare.setCountDownload(0);
        diskShareManager.save(diskShare);

        return "redirect:/disk/disk-share-list.do";
    }

    /**
     * 取消分享.
     */
    @RequestMapping("disk-share-remove")
    public String remove(@RequestParam("id") Long id) {
        diskShareManager.removeById(id);

        return "redirect:/disk/disk-share-list.do";
    }

    // ~ ======================================================================
    @Resource
    public void setDiskShareManager(DiskShareManager diskShareManager) {
        this.diskShareManager = diskShareManager;
    }

    @Resource
    public void setDiskInfoManager(DiskInfoManager diskInfoManager) {
        this.diskInfoManager = diskInfoManager;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }
}
