package com.mossle.disk.web.rs;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserDTO;

import com.mossle.client.user.UserClient;

import com.mossle.core.page.Page;
import com.mossle.core.util.ServletUtils;

import com.mossle.disk.persistence.domain.DiskAcl;
import com.mossle.disk.persistence.domain.DiskInfo;
import com.mossle.disk.persistence.domain.DiskSpace;
import com.mossle.disk.persistence.domain.DiskTag;
import com.mossle.disk.persistence.domain.DiskTagInfo;
import com.mossle.disk.persistence.domain.DiskVersion;
import com.mossle.disk.persistence.manager.DiskTagInfoManager;
import com.mossle.disk.persistence.manager.DiskTagManager;
import com.mossle.disk.service.DiskDownloadService;
import com.mossle.disk.service.DiskFileService;
import com.mossle.disk.service.DiskSpaceService;
import com.mossle.disk.service.internal.DiskAclInternalService;
import com.mossle.disk.service.internal.DiskQueryInternalService;
import com.mossle.disk.service.internal.DiskVersionInternalService;
import com.mossle.disk.support.DiskAclException;
import com.mossle.disk.support.Result;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "文件")
@RestController
@RequestMapping("disk/rs/file")
public class DiskFileRestController {
    private static Logger logger = LoggerFactory
            .getLogger(DiskFileRestController.class);
    private DiskAclInternalService diskAclInternalService;
    private DiskVersionInternalService diskVersionInternalService;
    private DiskQueryInternalService diskQueryInternalService;
    private DiskSpaceService diskSpaceService;
    private DiskFileService diskFileService;
    private DiskDownloadService diskDownloadService;
    private TenantHolder tenantHolder;
    private UserClient userClient;
    private CurrentUserHolder currentUserHolder;
    private DiskTagManager diskTagManager;
    private DiskTagInfoManager diskTagInfoManager;

    /**
     * 下载.
     */
    @Operation(summary = "下载")
    @RequestMapping(value = "download/{code}", method = RequestMethod.GET)
    public void fileDownload(
            @Parameter(description = "文件code") @PathVariable("code") Long fileCode,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        String userId = currentUserHolder.getUserId();

        try {
            DiskInfo diskInfo = this.diskFileService.findFile(fileCode, userId);
            InputStream is = null;

            try {
                ServletUtils.setFileDownloadHeader(request, response,
                        diskInfo.getName());

                Result<InputStream> result = this.diskDownloadService
                        .findDownloadInputStream(fileCode, userId, tenantId);

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
        } catch (DiskAclException ex) {
            logger.error(ex.getMessage(), ex);
            response.sendError(403);
        }
    }

    /**
     * 历史版本.
     */
    @Operation(summary = "历史版本")
    @RequestMapping(value = "version/list", method = RequestMethod.GET)
    public Result versions(
            @Parameter(description = "文件code") @RequestParam("code") Long fileCode,
            Page page, Model model) {
        String userId = currentUserHolder.getUserId();

        try {
            DiskInfo diskInfo = this.diskFileService.findFile(fileCode, userId);
            List<DiskVersion> diskVersions = this.diskVersionInternalService
                    .findVersions(diskInfo);

            List<Map> list = new ArrayList<Map>();

            for (DiskVersion diskVersion : diskVersions) {
                Map map = new HashMap();
                map.put("name", diskVersion.getName());
                map.put("type", diskVersion.getType());
                map.put("createTime", diskVersion.getCreateTime());
                map.put("creator", diskVersion.getCreator());
                map.put("fileVersion", diskVersion.getFileVersion());
                list.add(map);
            }

            return Result.success(Collections.singletonMap("result", list));
        } catch (DiskAclException ex) {
            return Result.failure(403, ex.getAction());
        }
    }

    // ~

    /**
     * 删除.
     */
    @RequestMapping("remove")
    public Map<String, Object> remove(@RequestParam("id") Long id) {
        String userId = currentUserHolder.getUserId();

        try {
            DiskInfo diskInfo = this.diskFileService.removeFile(id, userId);

            Long folderId = null;

            // check root
            if (diskInfo.getDiskInfo() == null) {
                logger.info("file must have folder : {}", id);
                folderId = diskInfo.getId();
            } else {
                folderId = diskInfo.getDiskInfo().getId();
            }

            Map<String, Object> resultMap = new HashMap<String, Object>();
            resultMap.put("code", 0);
            resultMap.put("message", "success");
            resultMap.put("data", folderId);

            return resultMap;
        } catch (DiskAclException ex) {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("code", 403);
            result.put("message", ex.getAction());

            return result;
        }
    }

    /**
     * 详情.
     */
    @RequestMapping("view")
    public Map<String, Object> view(@RequestParam("fileId") Long fileId) {
        String userId = currentUserHolder.getUserId();

        try {
            DiskInfo diskInfo = this.diskFileService.findFile(fileId, userId);

            // model.addAttribute("diskInfo", diskInfo);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("code", 0);
            map.put("fileId", diskInfo.getId());
            map.put("name", diskInfo.getName());
            map.put("creator", diskInfo.getCreator());
            map.put("creatorTime", diskInfo.getCreateTime());
            map.put("creatorName", diskInfo.getCreator());

            return map;
        } catch (DiskAclException ex) {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("code", 403);
            result.put("message", ex.getAction());

            return result;
        }
    }

    /**
     * 显示标签.
     */
    @RequestMapping("tags")
    public Map<String, Object> viewTags(@RequestParam("infoId") Long infoId) {
        try {
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            List<DiskTagInfo> diskTagInfos = diskTagInfoManager.find(
                    "from DiskTagInfo where diskInfo.id=?0 order by priority",
                    infoId);

            for (DiskTagInfo diskTagInfo : diskTagInfos) {
                Map<String, Object> item = new HashMap<String, Object>();
                item.put("id", diskTagInfo.getDiskTag().getId());
                item.put("code", diskTagInfo.getDiskTag().getCode());
                item.put("name", diskTagInfo.getDiskTag().getName());
                list.add(item);
            }

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("code", 0);
            map.put("message", "success");
            map.put("data", list);

            return map;
        } catch (DiskAclException ex) {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("code", 403);
            result.put("message", ex.getAction());

            return result;
        }
    }

    /**
     * 返回所有标签.
     */
    @RequestMapping("tags-all")
    public Map<String, Object> viewTagsAll() {
        try {
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            List<DiskTag> diskTags = diskTagManager.getAll();

            for (DiskTag diskTag : diskTags) {
                Map<String, Object> item = new HashMap<String, Object>();
                item.put("id", diskTag.getId());
                item.put("code", diskTag.getCode());
                item.put("name", diskTag.getName());
                list.add(item);
            }

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("code", 0);
            map.put("message", "success");
            map.put("data", list);

            return map;
        } catch (DiskAclException ex) {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("code", 403);
            result.put("message", ex.getAction());

            return result;
        }
    }

    /**
     * 保存标签.
     */
    @RequestMapping("save-tags")
    public Map<String, Object> saveTags(@RequestParam("infoId") Long infoId,
            @RequestParam("tags") String tags) {
        String tenantId = tenantHolder.getTenantId();
        String userId = currentUserHolder.getUserId();

        try {
            DiskInfo diskInfo = this.diskFileService.findFile(infoId, userId);

            this.diskFileService.saveTags(infoId, userId, tags);

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("code", 0);
            map.put("message", "success");
            map.put("data", infoId);

            return map;
        } catch (DiskAclException ex) {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("code", 403);
            result.put("message", ex.getAction());

            return result;
        }
    }

    /**
     * 权限列表.
     */
    @RequestMapping("permissions")
    public Map<String, Object> permissions(@RequestParam("infoId") Long infoId) {
        String userId = currentUserHolder.getUserId();

        try {
            List<DiskAcl> diskAcls = this.diskAclInternalService.findAcls(
                    infoId, userId);

            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

            for (DiskAcl diskAcl : diskAcls) {
                Map<String, Object> item = new HashMap<String, Object>();
                item.put("id", diskAcl.getId());
                item.put("entityCatalog", diskAcl.getEntityCatalog());
                item.put("entityRef", diskAcl.getEntityRef());
                item.put("mask", diskAcl.getMask());
                list.add(item);
            }

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("code", 0);
            map.put("message", "success");
            map.put("data", infoId);

            return map;
        } catch (DiskAclException ex) {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("code", 403);
            result.put("message", ex.getAction());

            return result;
        }
    }

    /**
     * 添加权限.
     */
    @RequestMapping("add-permission")
    public Map<String, Object> addPermission(
            @RequestParam("infoId") Long infoId,
            @RequestParam("username") List<String> usernames,
            @RequestParam("mask") int mask) {
        String userId = currentUserHolder.getUserId();

        try {
            for (String username : usernames) {
                UserDTO userDto = userClient.findByUsername(username, "1");

                if (userDto == null) {
                    logger.info("cannot find user : {}", username);

                    continue;
                }

                String memberUserId = userDto.getId();

                this.diskFileService.addPermission(infoId, userId,
                        memberUserId, mask);
            }

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("code", 0);
            map.put("message", "success");
            map.put("data", infoId);

            return map;
        } catch (DiskAclException ex) {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("code", 403);
            result.put("message", ex.getAction());

            return result;
        }
    }

    // /**
    // * 删除权限.
    // */
    // @RequestMapping("remove-permission")
    // public Map<String, Object> removePermission(
    // @RequestParam("entityCatalog") String entityCatalog,
    // @RequestParam("entityRef") String entityRef,
    // @RequestParam("folderId") Long folderId) {
    // String userId = currentUserHolder.getUserId();

    // try {
    // // this.diskAclService
    // // .removePermission(entityCatalog, entityRef, folderId);
    // this.diskFileService.removePermission(folderId, userId,
    // entityCatalog, entityRef);

    // Map<String, Object> map = new HashMap<String, Object>();
    // map.put("code", 0);
    // map.put("message", "success");
    // map.put("data", folderId);

    // return map;
    // } catch (DiskAclException ex) {
    // Map<String, Object> result = new HashMap<String, Object>();
    // result.put("code", 403);
    // result.put("message", ex.getAction());

    // return result;
    // }
    // }

    /**
     * 改名.
     */
    @RequestMapping("rename")
    public Map<String, Object> fileRename(@RequestParam("id") Long id,
            @RequestParam("name") String name, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String tenantId = tenantHolder.getTenantId();
        String userId = currentUserHolder.getUserId();

        if (id == null) {
            logger.info("id cannot be null");

            // return "redirect:/disk/view/default.do";
            Map<String, Object> resultMap = new HashMap<String, Object>();
            resultMap.put("code", 400);
            resultMap.put("message", "id cannot be null");

            return resultMap;
        }

        if (StringUtils.isBlank(name)) {
            logger.info("name cannot be blank");

            // return "redirect:/disk/view/default.do";
            Map<String, Object> resultMap = new HashMap<String, Object>();
            resultMap.put("code", 400);
            resultMap.put("message", "name cannot be blank");

            return resultMap;
        }

        try {
            DiskInfo diskInfo = this.diskFileService.rename(id, userId, name);

            Long folderId = null;

            // check root
            if (diskInfo.getDiskInfo() == null) {
                folderId = diskInfo.getId();
            } else {
                folderId = diskInfo.getDiskInfo().getId();
            }

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("code", 0);
            map.put("message", "success");
            map.put("data", folderId);

            return map;
        } catch (DiskAclException ex) {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("code", 403);
            result.put("message", ex.getAction());

            return result;
        }
    }

    /**
     * 移动.
     */
    @RequestMapping("move")
    public Map<String, Object> fileMove(@RequestParam("id") Long id,
            @RequestParam("parentId") Long parentId,
            @RequestParam("folderId") Long folderId,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();

        String userId = currentUserHolder.getUserId();

        try {
            DiskInfo diskInfo = this.diskFileService.move(id, userId, parentId);

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("code", 0);
            map.put("message", "success");
            map.put("data", folderId);

            return map;
        } catch (DiskAclException ex) {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("code", 403);
            result.put("message", ex.getAction());

            return result;
        }
    }

    /**
     * 复制.
     */
    @RequestMapping("copy")
    public Map<String, Object> fileCopy(@RequestParam("id") Long id,
            @RequestParam("folderId") Long folderId,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();

        String userId = currentUserHolder.getUserId();

        try {
            DiskInfo diskInfo = this.diskFileService.copyFile(id, userId,
                    folderId);

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("code", 0);
            map.put("message", "success");
            map.put("data", folderId);

            return map;
        } catch (DiskAclException ex) {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("code", 403);
            result.put("message", ex.getAction());

            return result;
        }
    }

    // ~

    /**
     * 搜索.
     */
    @RequestMapping("search")
    public Map<String, Object> search(
            @RequestParam("query") String query,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            Page page, Model model) {
        String userId = currentUserHolder.getUserId();

        try {
            page = this.diskFileService.search(query, startDate, endDate,
                    userId, page);

            // model.addAttribute("page", page);
            // model.addAttribute("diskTags", diskTagManager.getAll());
            // model.addAttribute("folder",
            // diskFolderService.findDefaultRepoSpaceRoot());

            // return "disk/file/search";
            List<DiskInfo> diskInfos = (List<DiskInfo>) page.getResult();
            List<Map<String, Object>> list = this.convertDiskInfos(diskInfos);
            page.setResult(list);

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("code", 0);
            map.put("message", "success");
            map.put("data", page);

            return map;
        } catch (DiskAclException ex) {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("code", 403);
            result.put("message", ex.getAction());

            return result;
        }
    }

    /**
     * 归档.
     */
    @RequestMapping("trash")
    public Map<String, Object> trash(Page page, Model model) {
        String userId = currentUserHolder.getUserId();

        try {
            page = this.diskFileService.findTrash(userId, page.getPageNo(),
                    page.getPageSize());
            model.addAttribute("page", page);

            DiskSpace diskSpace = this.diskSpaceService.findDefaultRepoSpace();
            DiskInfo diskInfo = this.diskQueryInternalService
                    .findDefaultRepoSpaceRoot();

            // model.addAttribute("folder", diskInfo);
            // model.addAttribute("folderId", diskInfo.getId());

            // return "disk/file/trash";
            List<DiskInfo> diskInfos = (List<DiskInfo>) page.getResult();
            List<Map<String, Object>> list = this.convertDiskInfos(diskInfos);
            page.setResult(list);

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("code", 0);
            map.put("message", "success");
            map.put("data", page);

            return map;
        } catch (DiskAclException ex) {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("code", 403);
            result.put("message", ex.getAction());

            return result;
        }
    }

    // ~
    public List<Map<String, Object>> convertDiskInfos(List<DiskInfo> diskInfos) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (DiskInfo diskInfo : diskInfos) {
            list.add(this.convertDiskInfo(diskInfo));
        }

        return list;
    }

    public Map<String, Object> convertDiskInfo(DiskInfo diskInfo) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", diskInfo.getId());
        map.put("name", diskInfo.getName());
        map.put("type", diskInfo.getType());
        map.put("fileSize", diskInfo.getFileSize());
        map.put("createTime", diskInfo.getCreateTime());
        map.put("creator", diskInfo.getCreator());

        StringBuilder buff = new StringBuilder();

        for (DiskTagInfo diskTagInfo : diskInfo.getDiskTagInfos()) {
            buff.append(diskTagInfo.getDiskTag().getName()).append(" ");
        }

        map.put("tags", buff.toString());

        return map;
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
    public void setDiskSpaceService(DiskSpaceService diskSpaceService) {
        this.diskSpaceService = diskSpaceService;
    }

    @Resource
    public void setDiskTagManager(DiskTagManager diskTagManager) {
        this.diskTagManager = diskTagManager;
    }

    @Resource
    public void setDiskFileService(DiskFileService diskFileService) {
        this.diskFileService = diskFileService;
    }

    @Resource
    public void setDiskTagInfoManager(DiskTagInfoManager diskTagInfoManager) {
        this.diskTagInfoManager = diskTagInfoManager;
    }

    @Resource
    public void setDiskVersionInternalService(
            DiskVersionInternalService diskVersionInternalService) {
        this.diskVersionInternalService = diskVersionInternalService;
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
}
