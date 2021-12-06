package com.mossle.disk.web.rs;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserDTO;

import com.mossle.client.user.UserClient;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.page.Page;

import com.mossle.disk.component.DiskInfoConverter;
import com.mossle.disk.persistence.domain.DiskAcl;
import com.mossle.disk.persistence.domain.DiskInfo;
import com.mossle.disk.persistence.domain.DiskTagInfo;
import com.mossle.disk.persistence.manager.DiskTagInfoManager;
import com.mossle.disk.persistence.manager.DiskTagManager;
import com.mossle.disk.service.DiskFileService;
import com.mossle.disk.service.DiskFolderService;
import com.mossle.disk.service.DiskSpaceService;
import com.mossle.disk.service.internal.DiskAclInternalService;
import com.mossle.disk.service.internal.DiskLogInternalService;
import com.mossle.disk.service.internal.DiskQueryInternalService;
import com.mossle.disk.support.DiskAclException;
import com.mossle.disk.support.DiskInfoDTO;
import com.mossle.disk.support.Result;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "文件夹")
@RestController
@RequestMapping("disk/rs/folder")
public class DiskFolderRestController {
    private static Logger logger = LoggerFactory
            .getLogger(DiskFolderRestController.class);
    private TenantHolder tenantHolder;
    private UserClient userClient;
    private CurrentUserHolder currentUserHolder;
    private DiskAclInternalService diskAclInternalService;
    private DiskSpaceService diskSpaceService;
    private DiskFolderService diskFolderService;
    private DiskLogInternalService diskLogInternalService;
    private DiskTagManager diskTagManager;
    private DiskTagInfoManager diskTagInfoManager;
    private DiskFileService diskFileService;
    private DiskQueryInternalService diskQueryInternalService;
    private DiskInfoConverter diskInfoConverter;
    private JsonMapper jsonMapper = new JsonMapper();

    /**
     * 新建文件夹.
     */
    @Operation(summary = "新建文件夹")
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public Result create(
            @Parameter(description = "文件夹名称") @RequestParam("name") String name,
            @Parameter(description = "上级文件夹code") @RequestParam("folderCode") Long folderCode,
            @Parameter(description = "模板") @RequestParam(value = "templateCode", required = false) String templateCode)
            throws Exception {
        String userId = currentUserHolder.getUserId();

        try {
            DiskInfo diskInfo = this.diskFolderService.createFolder(folderCode,
                    userId, name, "");

            if (StringUtils.isNotBlank(templateCode)) {
                logger.info("template : {}", templateCode);
                this.diskFolderService.initFolderStructure(diskInfo.getId(),
                        userId, templateCode);
            }

            DiskInfoDTO diskInfoDto = this.diskInfoConverter
                    .convertOne(diskInfo);

            return Result.success(diskInfoDto);
        } catch (DiskAclException ex) {
            return Result.failure(403, ex.getAction());
        }
    }

    /**
     * 子文件列表.
     */
    @Operation(summary = "子文件列表")
    @RequestMapping(value = "children", method = RequestMethod.GET)
    public Result folderChildren(
            @Parameter(description = "上级文件夹") @RequestParam(value = "code", required = false) Long folderCode,
            Page page) throws Exception {
        try {
            String tenantId = tenantHolder.getTenantId();
            String userId = currentUserHolder.getUserId();

            if ((folderCode == null) || (folderCode == 0)) {
                this.diskSpaceService.findDefaultRepoSpace();

                DiskInfo folder = this.diskQueryInternalService
                        .findDefaultRepoSpaceRoot();
                folderCode = folder.getId();
            }

            page = this.diskFolderService.findChildren(folderCode,
                    page.getPageNo(), page.getPageSize(), page.getOrderBy(),
                    page.getOrder(), userId);

            List<DiskInfo> diskInfos = (List<DiskInfo>) page.getResult();
            List<DiskInfoDTO> diskInfoDtos = this.diskInfoConverter
                    .convertList(diskInfos, userId);
            page.setResult(diskInfoDtos);

            return Result.success(page);
        } catch (DiskAclException ex) {
            return Result.failure(403, ex.getAction());
        }
    }

    /**
     * 文件夹树形.
     */
    @Operation(summary = "文件夹树形")
    @RequestMapping(value = "tree", method = RequestMethod.GET)
    public String folderTree() throws Exception {
        try {
            String tenantId = tenantHolder.getTenantId();
            String userId = currentUserHolder.getUserId();
            this.diskSpaceService.findDefaultRepoSpace();

            DiskInfo folder = this.diskQueryInternalService
                    .findDefaultRepoSpaceRoot();
            String json = this.diskFolderService.findFolderTree(folder.getId(),
                    userId);

            return json;
        } catch (DiskAclException ex) {
            logger.error(ex.getMessage(), ex);

            return "[{\"id\":0,\"name\":\"文档空间\"}]";
        }
    }

    /**
     * 文件夹路径.
     */
    @Operation(summary = "文件夹路径")
    @RequestMapping(value = "path", method = RequestMethod.GET)
    public Map<String, Object> folderPath(
            @Parameter(description = "文件夹") @RequestParam("code") Long folderCode) {
        String userId = currentUserHolder.getUserId();

        try {
            List<DiskInfo> folders = this.diskFolderService.findFolderPath(
                    folderCode, userId);

            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

            for (DiskInfo diskInfo : folders) {
                Map<String, Object> item = new HashMap<String, Object>();
                item.put("code", diskInfo.getId());
                item.put("name", diskInfo.getName());
                list.add(item);
            }

            Map<String, Object> resultMap = new HashMap<String, Object>();
            resultMap.put("code", 0);
            resultMap.put("message", "success");
            resultMap.put("data", list);

            return resultMap;
        } catch (DiskAclException ex) {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("code", 403);
            result.put("message", ex.getAction());

            return result;
        }
    }

    // ~

    /**
     * 删除文件夹.
     */
    @RequestMapping("removeFolder")
    public Map<String, Object> removeFolder(
            @RequestParam("folderId") Long folderId) {
        String userId = currentUserHolder.getUserId();

        try {
            DiskInfo folder = this.diskFolderService.removeFolder(folderId,
                    userId);

            // check root
            if (folder.getDiskInfo() == null) {
                folderId = folder.getId();
            } else {
                folderId = folder.getDiskInfo().getId();
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
     * 文件夹详情.
     */
    @RequestMapping("view")
    public Object folderView(
            @RequestParam(value = "folderId", required = false) Long folderId)
            throws Exception {
        try {
            String tenantId = tenantHolder.getTenantId();
            String userId = currentUserHolder.getUserId();
            DiskInfo folder = null;

            if (folderId == null) {
                this.diskSpaceService.findDefaultRepoSpace();
                folder = this.diskQueryInternalService
                        .findDefaultRepoSpaceRoot();
            } else {
                folder = this.diskFolderService.findFolder(folderId, userId);
            }

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("code", 0);
            map.put("folderId", folder.getId());
            map.put("name", folder.getName());
            map.put("creator", folder.getCreator());
            map.put("creatorName", folder.getCreator());
            map.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm")
                    .format(folder.getCreateTime()));

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
                    "from DiskTagInfo where diskInfo.id=?:0 order by priority",
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
     * 保存标签.
     */
    @RequestMapping("save-tags")
    public Map<String, Object> saveTags(@RequestParam("infoId") Long infoId,
            @RequestParam("tags") String tags, Model model) {
        String tenantId = tenantHolder.getTenantId();
        String userId = currentUserHolder.getUserId();

        try {
            DiskInfo diskInfo = this.diskFolderService.findFolder(infoId,
                    userId);

            this.diskFolderService.saveTags(infoId, userId, tags);

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
     * 批量保存标签.
     */
    @RequestMapping("save-tags-batch")
    public Map<String, Object> saveTagsBatch(@RequestBody String requestBody)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        String userId = currentUserHolder.getUserId();

        try {
            Map<String, Object> requestMap = jsonMapper.fromJson(requestBody,
                    Map.class);
            List<Long> ids = (List<Long>) requestMap.get("ids");
            String tags = (String) requestMap.get("tags");

            for (Long infoId : ids) {
                DiskInfo diskInfo = this.diskFolderService.findFolder(infoId,
                        userId);

                this.diskFolderService.saveTags(infoId, userId, tags);
            }

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("code", 0);
            map.put("message", "success");
            map.put("data", ids);

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
            List<DiskAcl> diskAcls = this.diskAclInternalService
                    .findPermissions(infoId, userId);

            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

            for (DiskAcl diskAcl : diskAcls) {
                Map<String, Object> item = new HashMap<String, Object>();
                item.put("id", diskAcl.getId());
                item.put("entityCatalog", diskAcl.getEntityCatalog());
                item.put("entityRef", diskAcl.getEntityRef());
                item.put("ref", diskAcl.getRef());
                item.put("mask", diskAcl.getMask());
                list.add(item);

                String entityCatalog = diskAcl.getEntityCatalog();
                String entityRef = diskAcl.getEntityRef();

                if ("owner".equals(entityCatalog)) {
                    String targetUserId = entityRef.substring(6);
                    UserDTO userDto = userClient.findById(targetUserId, "1");

                    if (userDto != null) {
                        String displayName = userDto.getDisplayName();
                        item.put("displayName", displayName);
                    }
                } else if ("user".equals(entityCatalog)) {
                    String targetUserId = entityRef.substring(5);
                    UserDTO userDto = userClient.findById(targetUserId, "1");

                    if (userDto != null) {
                        String displayName = userDto.getDisplayName();
                        item.put("displayName", displayName);
                    }
                }
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
     * 添加权限.
     */
    @RequestMapping("add-permission")
    public Map<String, Object> addPermission(@RequestBody String body)
            throws Exception {
        String userId = currentUserHolder.getUserId();

        try {
            Map<String, Object> bodyMap = jsonMapper.fromJson(body, Map.class);
            Long infoId = (Long) bodyMap.get("infoId");
            List<String> usernames = (List<String>) bodyMap.get("username");

            // logger.info("usernames : {}", usernames);
            for (String username : usernames) {
                UserDTO userDto = userClient.findByUsername(username, "1");

                if (userDto == null) {
                    logger.info("cannot find user : {}", username);

                    continue;
                }

                String memberUserId = userDto.getId();

                int mask = 127;
                this.diskFolderService.addPermission(infoId, userId,
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

    /**
     * 删除权限.
     */

    // @RequestMapping("remove-permission")
    // public Map<String, Object> removePermission(
    // @RequestParam("entityCatalog") String entityCatalog,
    // @RequestParam("entityRef") String entityRef,
    // @RequestParam("folderId") Long folderId) {
    // String userId = currentUserHolder.getUserId();
    // // this.diskAclService
    // // .removePermission(entityCatalog, entityRef, folderId);
    // this.diskFolderService.removePermission(folderId, userId,
    // entityCatalog, entityRef);

    // Map<String, Object> map = new HashMap<String, Object>();
    // map.put("code", 0);
    // map.put("message", "success");
    // map.put("data", folderId);
    // return map;
    // }

    /**
     * 删除权限.
     */
    @RequestMapping("remove-permission")
    public Map<String, Object> removePermission(@RequestParam("id") Long id,
            @RequestParam("folderId") Long folderId) {
        String userId = currentUserHolder.getUserId();

        try {
            // this.diskAclService
            // .removePermission(entityCatalog, entityRef, folderId);
            this.diskFolderService.removePermission(folderId, userId, id);

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
     * 改名.
     */
    @RequestMapping("renameFolder")
    public Map<String, Object> folderRename(@RequestParam("id") Long id,
            @RequestParam("name") String name) throws Exception {
        String tenantId = tenantHolder.getTenantId();
        String userId = currentUserHolder.getUserId();

        if (id == null) {
            logger.info("id cannot be null");

            Map<String, Object> resultMap = new HashMap<String, Object>();
            resultMap.put("code", 400);
            resultMap.put("message", "id cannot be null");

            return resultMap;
        }

        if (StringUtils.isBlank(name)) {
            logger.info("name cannot be blank");

            Map<String, Object> resultMap = new HashMap<String, Object>();
            resultMap.put("code", 400);
            resultMap.put("message", "name cannot be blank");

            return resultMap;
        }

        try {
            DiskInfo diskInfo = this.diskFolderService.rename(id, userId, name,
                    "");

            if (diskInfo == null) {
                logger.info("diskInfo cannot be null");

                Map<String, Object> resultMap = new HashMap<String, Object>();
                resultMap.put("code", 400);
                resultMap.put("message", "cannot find diskInfo");

                return resultMap;
            }

            Long folderId = diskInfo.getId();

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
     * 移动.
     */
    @RequestMapping("move")
    public Map<String, Object> folderMove(@RequestParam("id") Long id,
            @RequestParam("parentId") Long parentId,
            @RequestParam("folderId") Long folderId) throws Exception {
        String tenantId = tenantHolder.getTenantId();

        String userId = currentUserHolder.getUserId();

        try {
            DiskInfo diskInfo = this.diskFileService.move(id, userId, parentId);

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
     * 复制.
     */
    @RequestMapping("copy")
    public Map<String, Object> folderCopy(@RequestParam("id") Long id,
            @RequestParam("folderId") Long folderId) throws Exception {
        String tenantId = tenantHolder.getTenantId();

        String userId = currentUserHolder.getUserId();

        try {
            DiskInfo diskInfo = this.diskFileService.copyFile(id, userId,
                    folderId);

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
     * 恢复.
     */
    @RequestMapping("recover")
    public Map<String, Object> folderRecover(
            @RequestParam("folderId") Long folderId) throws Exception {
        String tenantId = tenantHolder.getTenantId();

        String userId = currentUserHolder.getUserId();

        try {
            DiskInfo diskInfo = this.diskFolderService
                    .recover(folderId, userId);
            Long parentId = null;

            if (diskInfo.getDiskInfo() != null) {
                parentId = diskInfo.getDiskInfo().getId();
            }

            Map<String, Object> resultMap = new HashMap<String, Object>();
            resultMap.put("code", 0);
            resultMap.put("message", "success");
            resultMap.put("data", parentId);

            return resultMap;
        } catch (DiskAclException ex) {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("code", 403);
            result.put("message", ex.getAction());

            return result;
        }
    }

    /**
     * 彻底删除.
     */
    @RequestMapping("delete")
    public Map<String, Object> folderDelete(
            @RequestParam("folderId") Long folderId) throws Exception {
        String tenantId = tenantHolder.getTenantId();

        String userId = currentUserHolder.getUserId();

        try {
            DiskInfo diskInfo = this.diskFolderService.delete(folderId, userId);
            Long parentId = null;

            if (diskInfo.getDiskInfo() != null) {
                parentId = diskInfo.getDiskInfo().getId();
            }

            Map<String, Object> resultMap = new HashMap<String, Object>();
            resultMap.put("code", 0);
            resultMap.put("message", "success");
            resultMap.put("data", parentId);

            return resultMap;
        } catch (DiskAclException ex) {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("code", 403);
            result.put("message", ex.getAction());

            return result;
        }
    }

    /**
     * 日志.
     */
    @RequestMapping("log")
    public Map<String, Object> folderLog(@RequestParam("folderId") Long folderId)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();

        String userId = currentUserHolder.getUserId();

        try {
            Page page = new Page();
            page = this.diskLogInternalService.findLogs(page);

            Map<String, Object> resultMap = new HashMap<String, Object>();
            resultMap.put("code", 0);
            resultMap.put("message", "success");
            resultMap.put("data", page);

            return resultMap;
        } catch (DiskAclException ex) {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("code", 403);
            result.put("message", ex.getAction());

            return result;
        }
    }

    // ~

    /**
     * 文件夹内部操作.
     */
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

            return "disk/folder/view";
        } catch (DiskAclException ex) {
            logger.info("acl exception");

            return "redirect:/common/403.jsp";
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
    public void setDiskFileService(DiskFileService diskFileService) {
        this.diskFileService = diskFileService;
    }

    @Resource
    public void setDiskTagInfoManager(DiskTagInfoManager diskTagInfoManager) {
        this.diskTagInfoManager = diskTagInfoManager;
    }

    @Resource
    public void setDiskQueryInternalService(
            DiskQueryInternalService diskQueryInternalService) {
        this.diskQueryInternalService = diskQueryInternalService;
    }

    @Resource
    public void setDiskInfoConverter(DiskInfoConverter diskInfoConverter) {
        this.diskInfoConverter = diskInfoConverter;
    }
}
