package com.mossle.disk.web;

import java.util.List;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserDTO;

import com.mossle.client.user.UserClient;

import com.mossle.core.page.Page;

import com.mossle.disk.persistence.domain.DiskInfo;
import com.mossle.disk.persistence.manager.DiskTagManager;
import com.mossle.disk.service.DiskFolderService;
import com.mossle.disk.service.internal.DiskAclInternalService;
import com.mossle.disk.service.internal.DiskBaseInternalService;
import com.mossle.disk.service.internal.DiskLogInternalService;
import com.mossle.disk.support.DiskAclException;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("disk/folder")
public class DiskFolderController {
    private static Logger logger = LoggerFactory
            .getLogger(DiskFolderController.class);
    private TenantHolder tenantHolder;
    private UserClient userClient;
    private CurrentUserHolder currentUserHolder;
    private DiskAclInternalService diskAclInternalService;
    private DiskFolderService diskFolderService;
    private DiskLogInternalService diskLogInternalService;
    private DiskTagManager diskTagManager;
    private DiskBaseInternalService diskBaseInternalService;

    /**
     * 文件夹内部操作.
     */

    // @ApiOperation(value = "folder", notes = "folder")
    // @ApiImplicitParams({
    // @ApiImplicitParam(name = "folderId", value = "folderId", paramType = "path", required = true, dataType = "long"),
    // @ApiImplicitParam(name = "pageNo", value = "pageNo", paramType = "query", required = false, dataType = "int"),
    // @ApiImplicitParam(name = "pageSize", value = "pageSize", paramType = "query", required = false, dataType = "int")
    // })
    @RequestMapping("{folderId}")
    public String folderView(@PathVariable("folderId") Long folderId,
            Page page, Model model) {
        String userId = currentUserHolder.getUserId();

        try {
            DiskInfo folder = this.diskFolderService.findFolder(folderId,
                    userId);

            if (folder == null) {
                logger.info("unexists folder : {}", folderId);

                return "redirect:/common/404.jsp";
            }

            page = this.diskFolderService.findChildren(folderId,
                    page.getPageNo(), page.getPageSize(), page.getOrderBy(),
                    page.getOrder(), userId);

            List<DiskInfo> folders = this.diskFolderService.findFolderPath(
                    folderId, userId);

            model.addAttribute("page", page);
            model.addAttribute("folderId", folderId);
            model.addAttribute("folder", folder);
            model.addAttribute("folders", folders);
            model.addAttribute("diskAcls",
                    diskAclInternalService.findPermissions(folderId, userId));
            model.addAttribute("diskTags", diskTagManager.getAll());
            model.addAttribute("treePath",
                    diskBaseInternalService.findTreePath(folderId));

            return "disk/folder/view";
        } catch (DiskAclException ex) {
            logger.info("acl exception");

            return "redirect:/common/403.jsp";
        }
    }

    /**
     * 文件夹树形.
     */
    @RequestMapping("tree")
    @ResponseBody
    public String folderTree(@RequestParam("id") Long id,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        try {
            String tenantId = tenantHolder.getTenantId();
            String userId = currentUserHolder.getUserId();
            DiskInfo folder = this.diskFolderService.findRootFolder(id);
            String json = this.diskFolderService.findFolderTree(folder.getId(),
                    userId);

            return json;
        } catch (DiskAclException ex) {
            return "[{\"name\":\"文档空间\"}]";
        }
    }

    /**
     * 创建文件夹.
     */
    @RequestMapping("create")
    public String create(
            @RequestParam("name") String name,
            @RequestParam("folderId") Long folderId,
            @RequestParam(value = "templateId", required = false) String templateId)
            throws Exception {
        String userId = currentUserHolder.getUserId();
        DiskInfo diskInfo = this.diskFolderService.createFolder(folderId,
                userId, name, "");

        if (StringUtils.isNotBlank(templateId)) {
            logger.info("template : {}", templateId);
            this.diskFolderService.initFolderStructure(diskInfo.getId(),
                    userId, templateId);
        }

        return "redirect:/disk/folder/" + folderId;
    }

    /**
     * 删除文件夹.
     */
    @RequestMapping("remove")
    public String remove(@RequestParam("folderId") Long folderId) {
        String userId = currentUserHolder.getUserId();
        DiskInfo folder = this.diskFolderService.removeFolder(folderId, userId);

        // check root
        if (folder.getDiskInfo() == null) {
            folderId = folder.getId();
        } else {
            folderId = folder.getDiskInfo().getId();
        }

        return "redirect:/disk/folder/" + folderId;
    }

    /**
     * 添加权限.
     */
    @RequestMapping("add-permission")
    public String addPermission(@RequestParam("infoId") Long infoId,
            @RequestParam("username") List<String> usernames,
            @RequestParam("mask") int mask) {
        String userId = currentUserHolder.getUserId();

        for (String username : usernames) {
            UserDTO userDto = userClient.findByUsername(username, "1");

            if (userDto == null) {
                logger.info("cannot find user : {}", username);

                continue;
            }

            String memberUserId = userDto.getId();

            this.diskFolderService.addPermission(infoId, userId, memberUserId,
                    mask);
        }

        return "redirect:/disk/folder/" + infoId;
    }

    /**
     * 删除权限.
     */
    @RequestMapping("remove-permission")
    public String removePermission(@RequestParam("id") Long id,
            @RequestParam("folderId") Long folderId) {
        String userId = currentUserHolder.getUserId();
        // this.diskAclInternalService
        // .removePermission(entityCatalog, entityRef, folderId);
        this.diskFolderService.removePermission(folderId, userId, id);

        return "redirect:/disk/folder/" + folderId;
    }

    /**
     * 操作记录.
     */
    @RequestMapping("log")
    public String log(@RequestParam("folderId") Long folderId, Page page,
            Model model) {
        String userId = currentUserHolder.getUserId();
        page = this.diskLogInternalService.findLogs(page);
        model.addAttribute("page", page);

        model.addAttribute("folderId", folderId);
        model.addAttribute("folder",
                diskFolderService.findFolder(folderId, userId));

        return "disk/folder/log";
    }

    /**
     * 改名.
     */
    @RequestMapping("rename")
    public String folderRename(
            @RequestParam("id") Long id,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
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

        if (description == null) {
            description = "";
        }

        DiskInfo diskInfo = this.diskFolderService.rename(id, userId, name,
                description);

        if (diskInfo == null) {
            logger.info("diskInfo cannot be null");

            return "redirect:/disk/folder/" + id;
        }

        Long folderId = diskInfo.getId();

        return "redirect:/disk/folder/" + folderId;
    }

    /**
     * 保存标签.
     */
    @RequestMapping("save-tags")
    public String saveTags(@RequestParam("infoId") Long infoId,
            @RequestParam("tags") String tags, Model model) {
        String tenantId = tenantHolder.getTenantId();
        String userId = currentUserHolder.getUserId();

        DiskInfo diskInfo = this.diskFolderService.findFolder(infoId, userId);

        this.diskFolderService.saveTags(infoId, userId, tags);

        return "redirect:/disk/folder/" + infoId;
    }

    // ~ ======================================================================
    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }

    @Resource
    public void setUserClient(UserClient userClient) {
        this.userClient = userClient;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setDiskAclInternalService(
            DiskAclInternalService diskAclInternalService) {
        this.diskAclInternalService = diskAclInternalService;
    }

    @Resource
    public void setDiskFolderService(DiskFolderService diskFolderService) {
        this.diskFolderService = diskFolderService;
    }

    @Resource
    public void setDiskLogInternalService(
            DiskLogInternalService diskLogInternalService) {
        this.diskLogInternalService = diskLogInternalService;
    }

    @Resource
    public void setDiskTagManager(DiskTagManager diskTagManager) {
        this.diskTagManager = diskTagManager;
    }

    @Resource
    public void setDiskBaseInternalService(
            DiskBaseInternalService diskBaseInternalService) {
        this.diskBaseInternalService = diskBaseInternalService;
    }
}
