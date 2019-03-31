package com.mossle.disk.web;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.auth.CurrentUserHolder;

import com.mossle.disk.persistence.domain.DiskInfo;
import com.mossle.disk.persistence.domain.DiskShare;
import com.mossle.disk.persistence.manager.DiskInfoManager;
import com.mossle.disk.persistence.manager.DiskShareManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

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
    private String baseUrl;

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
        model.addAttribute("baseUrl", baseUrl);

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
        diskShare.setCatalog("public");
        diskShareManager.save(diskShare);

        return "redirect:/disk/disk-share-list.do";
    }

    /**
     * 分享，私密.
     */
    @RequestMapping("disk-share-sharePrivate")
    public String sharePrivate(@RequestParam("id") Long id) {
        DiskInfo diskInfo = diskInfoManager.get(id);
        DiskShare diskShare = diskShareManager.findUniqueBy("diskInfo",
                diskInfo);

        if (diskShare != null) {
            return "redirect:/disk/disk-share-list.do";
        }

        diskShare = new DiskShare();
        diskShare.setShareType("private");
        diskShare.setShareTime(new Date());
        diskShare.setDiskInfo(diskInfo);
        diskShare.setName(diskInfo.getName());
        diskShare.setCreator(diskInfo.getCreator());
        diskShare.setType(diskInfo.getType());
        diskShare.setDirType(diskInfo.getDirType());
        diskShare.setCountView(0);
        diskShare.setCountSave(0);
        diskShare.setCountDownload(0);
        diskShare.setSharePassword(this.generatePassword());
        diskShare.setCatalog("external");
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

    public String generatePassword() {
        int value = (int) (((Math.random() * 9) + 1) * 1679616);
        char[] c = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
                'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
                'y', 'z' };
        StringBuilder buff = new StringBuilder();
        buff.append(c[(value / 36 / 36 / 36) % 36]);
        buff.append(c[(value / 36 / 36) % 36]);
        buff.append(c[(value / 36) % 36]);
        buff.append(c[value % 36]);

        return buff.toString();
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

    @Value("${application.baseUrl}")
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
