package com.mossle.disk.web;

import java.io.InputStream;

import java.util.List;
import java.util.zip.*;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.page.Page;
import com.mossle.core.store.MultipartFileDataSource;
import com.mossle.core.util.ServletUtils;

import com.mossle.disk.persistence.domain.DiskInfo;
import com.mossle.disk.persistence.domain.DiskSpace;
import com.mossle.disk.persistence.domain.DiskVersion;
import com.mossle.disk.service.DiskDownloadService;
import com.mossle.disk.service.DiskFileService;
import com.mossle.disk.service.DiskSpaceService;
import com.mossle.disk.service.DiskUploadService;
import com.mossle.disk.service.internal.DiskLogInternalService;
import com.mossle.disk.service.internal.DiskQueryInternalService;
import com.mossle.disk.service.internal.DiskVersionInternalService;
import com.mossle.disk.support.Result;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("disk/file")
public class DiskFileController {
    private static Logger logger = LoggerFactory
            .getLogger(DiskFileController.class);
    private TenantHolder tenantHolder;
    private CurrentUserHolder currentUserHolder;
    private DiskSpaceService diskSpaceService;
    private DiskFileService diskFileService;
    private DiskVersionInternalService diskVersionInternalService;
    private DiskUploadService diskUploadService;
    private DiskDownloadService diskDownloadService;
    private DiskQueryInternalService diskQueryInternalService;
    private DiskLogInternalService diskLogInternalService;

    /**
     * 上传文件.
     */
    @RequestMapping("upload")
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile file,
            @RequestParam("folderId") Long folderId,
            @RequestParam("lastModified") long lastModified) throws Exception {
        logger.debug("lastModified : {}", lastModified);

        String userId = currentUserHolder.getUserId();
        String tenantId = tenantHolder.getTenantId();

        this.diskUploadService.uploadFile(folderId, userId,
                new MultipartFileDataSource(file), file.getOriginalFilename(),
                file.getSize(), "", lastModified, tenantId);

        return "{\"success\":true}";
    }

    /**
     * 下载.
     */
    @RequestMapping("download/{id}")
    public void fileDownload(@PathVariable("id") Long id,
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
                    .findDownloadInputStream(id, userId, tenantId);

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

    /**
     * 改名.
     */
    @RequestMapping("rename")
    public String fileRename(@RequestParam("id") Long id,
            @RequestParam("name") String name, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String tenantId = tenantHolder.getTenantId();
        String userId = currentUserHolder.getUserId();

        if (id == null) {
            logger.info("id cannot be null");

            return "redirect:/disk/view/default.do";
        }

        if (StringUtils.isBlank(name)) {
            logger.info("name cannot be blank");

            return "redirect:/disk/view/default.do";
        }

        DiskInfo diskInfo = this.diskFileService.rename(id, userId, name);

        Long folderId = null;

        // check root
        if (diskInfo.getDiskInfo() == null) {
            folderId = diskInfo.getId();
        } else {
            folderId = diskInfo.getDiskInfo().getId();
        }

        return "redirect:/disk/folder/" + folderId;
    }

    /**
     * 移动.
     */
    @RequestMapping("move")
    public String fileMove(@RequestParam("id") Long id,
            @RequestParam("parentId") Long parentId,
            @RequestParam("folderId") Long folderId,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();

        String userId = currentUserHolder.getUserId();
        DiskInfo diskInfo = this.diskFileService.move(id, userId, parentId);

        return "redirect:/disk/folder/" + folderId;
    }

    /**
     * 复制.
     */
    @RequestMapping("copy")
    public String fileCopy(@RequestParam("id") Long id,
            @RequestParam("folderId") Long folderId,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();

        String userId = currentUserHolder.getUserId();
        DiskInfo diskInfo = this.diskFileService.copyFile(id, userId, folderId);

        return "redirect:/disk/folder/" + folderId;
    }

    /**
     * 删除.
     */
    @RequestMapping("remove")
    public String remove(@RequestParam("id") Long id) {
        String userId = currentUserHolder.getUserId();
        DiskInfo diskInfo = this.diskFileService.removeFile(id, userId);

        Long folderId = null;

        // check root
        if (diskInfo.getDiskInfo() == null) {
            logger.info("file must have folder : {}", id);
            folderId = diskInfo.getId();
        } else {
            folderId = diskInfo.getDiskInfo().getId();
        }

        return "redirect:/disk/folder/" + folderId;
    }

    /**
     * 详情.
     */
    @RequestMapping("{fileId}")
    public String view(@PathVariable("fileId") long fileId, Model model) {
        String userId = currentUserHolder.getUserId();
        DiskInfo diskInfo = this.diskFileService.findFile(fileId, userId);
        this.diskLogInternalService.recordOpen(fileId, userId);
        model.addAttribute("diskInfo", diskInfo);

        return "disk/file/view";
    }

    /**
     * 保存标签.
     */
    @RequestMapping("save-tags")
    public String saveTags(@RequestParam("infoId") Long infoId,
            @RequestParam("tags") String tags, Model model) {
        String tenantId = tenantHolder.getTenantId();
        String userId = currentUserHolder.getUserId();

        Long id = infoId;
        DiskInfo diskInfo = this.diskFileService.findFile(id, userId);

        this.diskFileService.saveTags(diskInfo.getId(), userId, tags);

        Long folderId = null;

        if (diskInfo.getDiskInfo() != null) {
            folderId = diskInfo.getDiskInfo().getId();
        } else {
            folderId = diskInfo.getId();
        }

        return "redirect:/disk/folder/" + folderId;
    }

    /**
     * 保存标签.
     */
    @RequestMapping("save-tags-batch")
    public String saveTagsBatch(@RequestParam("ids") String ids,
            @RequestParam("tags") String tags,
            @RequestParam("folderId") Long folderId, Model model) {
        String tenantId = tenantHolder.getTenantId();
        String userId = currentUserHolder.getUserId();

        if (StringUtils.isBlank(ids)) {
            logger.info("ids cannot be blank");

            return "redirect:/disk/folder/" + folderId;
        }

        for (String textId : ids.split(",")) {
            Long id = Long.parseLong(textId);
            DiskInfo diskInfo = this.diskFileService.findFile(id, userId);

            this.diskFileService.saveTags(diskInfo.getId(), userId, tags);
        }

        return "redirect:/disk/folder/" + folderId;
    }

    /**
     * 搜索.
     */
    @RequestMapping("search")
    public String search(@RequestParam("query") String query, Page page,
            Model model) {
        String userId = currentUserHolder.getUserId();
        page = this.diskFileService.search(query, "", "", userId, page);
        model.addAttribute("page", page);

        // model.addAttribute("diskTags", diskTagManager.getAll());
        // model.addAttribute("folder",
        // diskFolderService.findDefaultRepoSpaceRoot());
        return "disk/file/search";
    }

    /**
     * 归档.
     */
    @RequestMapping("trash")
    public String trash(Page page, Model model) {
        String userId = currentUserHolder.getUserId();
        page = this.diskFileService.findTrash(userId, page.getPageNo(),
                page.getPageSize());
        model.addAttribute("page", page);

        DiskSpace diskSpace = this.diskSpaceService.findDefaultRepoSpace();
        DiskInfo diskInfo = this.diskQueryInternalService
                .findDefaultRepoSpaceRoot();
        model.addAttribute("folder", diskInfo);
        model.addAttribute("folderId", diskInfo.getId());

        return "disk/file/trash";
    }

    /**
     * 历史版本.
     */
    @RequestMapping("versions")
    public String versions(@RequestParam("id") Long id, Page page, Model model) {
        String userId = currentUserHolder.getUserId();
        DiskInfo diskInfo = this.diskFileService.findFile(id, userId);
        List<DiskVersion> diskVersions = this.diskVersionInternalService
                .findVersions(diskInfo);
        model.addAttribute("diskVersions", diskVersions);

        model.addAttribute("diskInfo", diskInfo);

        if (diskInfo != null) {
            DiskInfo folder = diskInfo.getDiskInfo();
            model.addAttribute("folder", folder);
            model.addAttribute("folderId", folder.getId());
        } else {
            return "redirect:/disk/view/default.do";
        }

        return "disk/file/versions";
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
    public void setDiskSpaceService(DiskSpaceService diskSpaceService) {
        this.diskSpaceService = diskSpaceService;
    }

    @Resource
    public void setDiskFileService(DiskFileService diskFileService) {
        this.diskFileService = diskFileService;
    }

    @Resource
    public void setDiskVersionInternalService(
            DiskVersionInternalService diskVersionInternalService) {
        this.diskVersionInternalService = diskVersionInternalService;
    }

    @Resource
    public void setDiskUploadService(DiskUploadService diskUploadService) {
        this.diskUploadService = diskUploadService;
    }

    @Resource
    public void setDiskDownloadService(DiskDownloadService diskDownloadService) {
        this.diskDownloadService = diskDownloadService;
    }

    @Resource
    public void setDiskQueryInternalService(
            DiskQueryInternalService diskQueryInternalService) {
        this.diskQueryInternalService = diskQueryInternalService;
    }

    @Resource
    public void setDiskLogInternalService(
            DiskLogInternalService diskLogInternalService) {
        this.diskLogInternalService = diskLogInternalService;
    }
}
