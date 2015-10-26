package com.mossle.disk.web;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.store.StoreConnector;
import com.mossle.api.store.StoreDTO;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.auth.CurrentUserHolder;
import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;
import com.mossle.core.store.MultipartFileDataSource;
import com.mossle.core.util.IoUtils;
import com.mossle.core.util.ServletUtils;

import com.mossle.disk.persistence.domain.DiskInfo;
import com.mossle.disk.persistence.domain.DiskShare;
import com.mossle.disk.persistence.manager.DiskInfoManager;
import com.mossle.disk.persistence.manager.DiskShareManager;
import com.mossle.disk.service.DiskService;
import com.mossle.disk.util.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("disk")
public class DiskShareController {
    private static Logger logger = LoggerFactory
            .getLogger(DiskShareController.class);
    private DiskShareManager diskShareManager;
    private DiskInfoManager diskInfoManager;
    private CurrentUserHolder currentUserHolder;
    private StoreConnector storeConnector;
    private DiskService diskService;
    private TenantHolder tenantHolder;

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

    @Resource
    public void setStoreConnector(StoreConnector storeConnector) {
        this.storeConnector = storeConnector;
    }

    @Resource
    public void setDiskService(DiskService diskService) {
        this.diskService = diskService;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
