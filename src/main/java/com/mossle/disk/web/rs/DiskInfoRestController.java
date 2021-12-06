package com.mossle.disk.web.rs;

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

import com.mossle.disk.component.DiskInfoConverter;
import com.mossle.disk.persistence.domain.DiskAcl;
import com.mossle.disk.persistence.domain.DiskInfo;
import com.mossle.disk.persistence.domain.DiskSid;
import com.mossle.disk.persistence.domain.DiskTagInfo;
import com.mossle.disk.persistence.manager.DiskTagInfoManager;
import com.mossle.disk.service.DiskInfoService;
import com.mossle.disk.service.internal.DiskAclInternalService;
import com.mossle.disk.service.internal.DiskLogInternalService;
import com.mossle.disk.support.DiskAclException;
import com.mossle.disk.support.DiskInfoDTO;
import com.mossle.disk.support.Result;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "通用")
@RestController
@RequestMapping("disk/rs/info")
public class DiskInfoRestController {
    private static Logger logger = LoggerFactory
            .getLogger(DiskInfoRestController.class);
    private TenantHolder tenantHolder;
    private UserClient userClient;
    private CurrentUserHolder currentUserHolder;
    private DiskAclInternalService diskAclInternalService;
    private DiskLogInternalService diskLogInternalService;
    private DiskTagInfoManager diskTagInfoManager;
    private DiskInfoService diskInfoService;
    private DiskInfoConverter diskInfoConverter;

    /**
     * 删除.
     */
    @Operation(summary = "删除")
    @RequestMapping(value = "remove", method = RequestMethod.POST)
    public Result remove(
            @Parameter(description = "节点code") @RequestParam("code") Long infoCode) {
        String userId = currentUserHolder.getUserId();

        try {
            Result<DiskInfo> result = this.diskInfoService.remove(infoCode,
                    userId);

            if (result.isFailure()) {
                return result;
            }

            DiskInfo diskInfo = result.getData();
            DiskInfoDTO diskInfoDto = diskInfoConverter.convertOne(diskInfo);

            return Result.success(diskInfoDto);
        } catch (DiskAclException ex) {
            return Result.failure(403, ex.getAction());
        }
    }

    /**
     * 详情.
     */
    @Operation(summary = "详情")
    @RequestMapping(value = "detail", method = RequestMethod.GET)
    public Result detail(
            @Parameter(description = "节点code") @RequestParam("code") Long infoCode) {
        String userId = currentUserHolder.getUserId();

        try {
            Result<DiskInfo> result = this.diskInfoService.findById(infoCode,
                    userId);

            if (result.isFailure()) {
                return result;
            }

            DiskInfo diskInfo = result.getData();
            this.diskLogInternalService.recordOpen(infoCode, userId);

            DiskInfoDTO diskInfoDto = diskInfoConverter.convertOne(diskInfo);

            if (diskInfoDto.getDirType() == 1) {
                diskInfoDto.setUserCount(diskLogInternalService
                        .findUserCountByInfoId(infoCode));
                diskInfoDto.setClickCount(diskLogInternalService
                        .findClickCountByInfoId(infoCode));
                diskInfoDto.setDownloadCount(diskLogInternalService
                        .findDownloadCountByInfoId(infoCode));
            }

            return Result.success(diskInfoDto);
        } catch (DiskAclException ex) {
            return Result.failure(403, ex.getAction());
        }
    }

    /**
     * 重命名.
     */
    @Operation(summary = "重命名")
    @RequestMapping(value = "rename", method = RequestMethod.POST)
    public Result fileRename(
            @Parameter(description = "节点code") @RequestParam("code") Long infoCode,
            @Parameter(description = "节点名称") @RequestParam("name") String name)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        String userId = currentUserHolder.getUserId();

        if (infoCode == null) {
            logger.info("infoCode cannot be null");

            return Result.failure(400, "infoCode cannot be null");
        }

        if (StringUtils.isBlank(name)) {
            logger.info("name cannot be blank");

            return Result.failure(400, "name cannot be blank");
        }

        try {
            Result<DiskInfo> result = this.diskInfoService.rename(infoCode,
                    userId, name);

            if (result.isFailure()) {
                return result;
            }

            DiskInfo diskInfo = result.getData();
            DiskInfoDTO diskInfoDto = diskInfoConverter.convertOne(diskInfo);

            return Result.success(diskInfoDto);
        } catch (DiskAclException ex) {
            return Result.failure(403, ex.getAction());
        }
    }

    /**
     * 移动.
     */
    @Operation(summary = "移动")
    @RequestMapping(value = "move", method = RequestMethod.POST)
    public Result fileMove(
            @Parameter(description = "节点code") @RequestParam("code") Long infoCode,
            @Parameter(description = "目的文件夹") @RequestParam("folderCode") Long folderCode)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();

        String userId = currentUserHolder.getUserId();

        try {
            Result<DiskInfo> result = this.diskInfoService.move(infoCode,
                    userId, folderCode);

            if (result.isFailure()) {
                return result;
            }

            DiskInfo diskInfo = result.getData();

            DiskInfoDTO diskInfoDto = diskInfoConverter.convertOne(diskInfo);

            return Result.success(diskInfoDto);
        } catch (DiskAclException ex) {
            return Result.failure(403, ex.getAction());
        }
    }

    /**
     * 复制.
     */
    @Operation(summary = "复制")
    @RequestMapping(value = "copy", method = RequestMethod.POST)
    public Result fileCopy(
            @Parameter(description = "节点code") @RequestParam("code") Long infoCode,
            @Parameter(description = "目标文件夹") @RequestParam("folderCode") Long folderCode,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();

        String userId = currentUserHolder.getUserId();

        try {
            Result<DiskInfo> result = this.diskInfoService.copy(infoCode,
                    userId, folderCode);

            if (result.isFailure()) {
                return result;
            }

            DiskInfo diskInfo = result.getData();

            DiskInfoDTO diskInfoDto = diskInfoConverter.convertOne(diskInfo);

            return Result.success(diskInfoDto);
        } catch (DiskAclException ex) {
            return Result.failure(403, ex.getAction());
        }
    }

    /**
     * 引用.
     */
    @Operation(summary = "引用")
    @RequestMapping(value = "link", method = RequestMethod.POST)
    public Result link(
            @Parameter(description = "节点code") @RequestParam("code") Long infoCode,
            @Parameter(description = "目标文件夹") @RequestParam("folderCode") Long folderCode,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();

        String userId = currentUserHolder.getUserId();

        try {
            Result<DiskInfo> result = this.diskInfoService.link(infoCode,
                    userId, folderCode);

            if (result.isFailure()) {
                return result;
            }

            DiskInfo diskInfo = result.getData();

            DiskInfoDTO diskInfoDto = diskInfoConverter.convertOne(diskInfo);

            return Result.success(diskInfoDto);
        } catch (DiskAclException ex) {
            return Result.failure(403, ex.getAction());
        }
    }

    /**
     * 恢复.
     */
    @Operation(summary = "恢复")
    @RequestMapping(value = "recover", method = RequestMethod.POST)
    public Result folderRecover(
            @Parameter(description = "节点code") @RequestParam("code") Long infoCode)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();

        String userId = currentUserHolder.getUserId();

        try {
            Result<DiskInfo> result = this.diskInfoService.recover(infoCode,
                    userId, null);

            if (result.isFailure()) {
                return result;
            }

            DiskInfo diskInfo = result.getData();
            DiskInfoDTO diskInfoDto = diskInfoConverter.convertOne(diskInfo);

            return Result.success(diskInfoDto);
        } catch (DiskAclException ex) {
            return Result.failure(403, ex.getAction());
        }
    }

    /**
     * 彻底删除.
     */
    @Operation(summary = "彻底删除")
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public Result folderDelete(
            @Parameter(description = "节点code") @RequestParam("code") Long infoCode)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();

        String userId = currentUserHolder.getUserId();

        try {
            Result<DiskInfo> result = this.diskInfoService.delete(infoCode,
                    userId);

            if (result.isFailure()) {
                return result;
            }

            DiskInfo diskInfo = result.getData();
            DiskInfoDTO diskInfoDto = diskInfoConverter.convertOne(diskInfo);

            return Result.success(diskInfoDto);
        } catch (DiskAclException ex) {
            return Result.failure(403, ex.getAction());
        }
    }

    /**
     * 标签列表.
     */
    @Operation(summary = "标签列表")
    @RequestMapping(value = "tag/list", method = RequestMethod.GET)
    public Result viewTags(
            @Parameter(description = "节点code") @RequestParam("code") Long infoCode) {
        try {
            List<Map<String, Object>> list = this.findTags(infoCode);

            return Result.success(Collections.singletonMap("tags", list));
        } catch (DiskAclException ex) {
            return Result.failure(403, ex.getAction());
        }
    }

    public List<Map<String, Object>> findTags(Long infoCode) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        List<DiskTagInfo> diskTagInfos = diskTagInfoManager.find(
                "from DiskTagInfo where diskInfo.id=? order by priority",
                infoCode);

        for (DiskTagInfo diskTagInfo : diskTagInfos) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("id", diskTagInfo.getDiskTag().getId());
            item.put("code", diskTagInfo.getDiskTag().getCode());
            item.put("name", diskTagInfo.getDiskTag().getName());
            list.add(item);
        }

        return list;
    }

    /**
     * 保存标签.
     */
    @Operation(summary = "保存标签")
    @RequestMapping(value = "tag/save", method = RequestMethod.POST)
    public Result saveTags(
            @Parameter(description = "节点code") @RequestParam("code") Long infoCode,
            @Parameter(description = "标签") @RequestParam("tags") String tags) {
        String tenantId = tenantHolder.getTenantId();
        String userId = currentUserHolder.getUserId();

        try {
            this.diskInfoService.saveTags(infoCode, userId, tags);

            List<Map<String, Object>> list = this.findTags(infoCode);

            return Result.success(Collections.singletonMap("tags", list));
        } catch (DiskAclException ex) {
            return Result.failure(403, ex.getAction());
        }
    }

    /**
     * 权限列表.
     */
    @Operation(summary = "权限列表")
    @RequestMapping(value = "perm/list", method = RequestMethod.GET)
    public Result permissions(
            @Parameter(description = "节点code") @RequestParam("code") Long infoCode) {
        String userId = currentUserHolder.getUserId();

        try {
            Result<DiskInfo> result = this.diskInfoService.findById(infoCode,
                    userId);

            if (result.isFailure()) {
                return result;
            }

            DiskInfo diskInfo = result.getData();

            List<DiskAcl> diskAcls = this.diskAclInternalService
                    .findPermissions(infoCode, userId);

            List<Map<String, Object>> list = this.findPerms(infoCode, userId);

            return Result.success(Collections.singletonMap("perms", list));
        } catch (DiskAclException ex) {
            return Result.failure(403, ex.getAction());
        }
    }

    public List<Map<String, Object>> findPerms(Long infoCode, String userId) {
        Result<DiskInfo> result = diskInfoService.findById(infoCode, userId);

        if (result.isFailure()) {
            return Collections.emptyList();
        }

        DiskInfo diskInfo = result.getData();
        List<DiskAcl> diskAcls = this.diskAclInternalService.findPermissions(
                infoCode, userId);

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> ownerItem = new HashMap<String, Object>();
        ownerItem.put("code", "");
        ownerItem.put("ref", diskInfo.getCreator());
        ownerItem.put("catalog", "owner");
        ownerItem.put("permMask", 0);
        ownerItem.put("permName", "owner");
        ownerItem.put("name", userClient.findById(diskInfo.getCreator(), "1")
                .getDisplayName());
        list.add(ownerItem);

        for (DiskAcl diskAcl : diskAcls) {
            DiskSid diskSid = diskAcl.getDiskSid();

            if (diskSid == null) {
                continue;
            }

            if ("owner".equals(diskAcl.getType())) {
                continue;
            }

            if ("role".equals(diskSid.getCatalog())) {
                continue;
            }

            Map<String, Object> item = new HashMap<String, Object>();
            item.put("code", diskAcl.getId());
            item.put("ref", diskSid.getValue());
            item.put("catalog", diskSid.getCatalog());
            item.put("permMask", diskAcl.getMask());
            item.put("permName", (diskAcl.getMask() == 1) ? "read" : "edit");

            if ("user".equals(diskSid.getCatalog())) {
                String targetUserId = diskSid.getValue();
                UserDTO userDto = userClient.findById(targetUserId, "1");
                item.put("name", userDto.getDisplayName());
                item.put("ref", targetUserId);
            }

            list.add(item);
        }

        return list;
    }

    /**
     * 添加权限.
     */
    @Operation(summary = "添加权限")
    @RequestMapping(value = "perm/add", method = RequestMethod.POST)
    public Result addPermission(
            @Parameter(description = "节点code") @RequestParam("code") Long infoCode,
            @Parameter(description = "帐号") @RequestParam("username") List<String> usernames,
            @Parameter(description = "权限") @RequestParam("mask") int mask) {
        String userId = currentUserHolder.getUserId();

        try {
            for (String username : usernames) {
                UserDTO userDto = userClient.findByUsername(username, "1");

                if (userDto == null) {
                    logger.info("cannot find user : {}", username);

                    continue;
                }

                String memberUserId = userDto.getId();

                this.diskInfoService.addPermission(infoCode, userId,
                        memberUserId, mask);
            }

            List<Map<String, Object>> list = this.findPerms(infoCode, userId);

            return Result.success(Collections.singletonMap("perms", list));
        } catch (DiskAclException ex) {
            return Result.failure(403, ex.getAction());
        }
    }

    /**
     * 删除权限.
     */
    @Operation(summary = "删除权限")
    @RequestMapping(value = "perm/remove", method = RequestMethod.POST)
    public Result removePermission(
            @Parameter(description = "节点code") @RequestParam("code") Long infoCode,
            @Parameter(description = "权限code") @RequestParam("aclCode") Long aclCode) {
        String userId = currentUserHolder.getUserId();

        try {
            this.diskInfoService.removePermission(infoCode, userId, aclCode);

            List<Map<String, Object>> list = this.findPerms(infoCode, userId);

            return Result.success(Collections.singletonMap("perms", list));
        } catch (DiskAclException ex) {
            return Result.failure(403, ex.getAction());
        }
    }

    /**
     * 日志.
     */
    @Operation(summary = "日志")
    @RequestMapping(value = "log", method = RequestMethod.GET)
    public Result folderLog(
            @Parameter(description = "节点code") @RequestParam("code") Long infoCode)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();

        String userId = currentUserHolder.getUserId();

        try {
            Page page = new Page();
            page = this.diskLogInternalService.findLogs(page);

            return Result.success(page);
        } catch (DiskAclException ex) {
            return Result.failure(403, ex.getAction());
        }
    }

    /**
     * 修改密级.
     */
    @Operation(summary = "修改密级")
    @RequestMapping(value = "update-security-level", method = RequestMethod.POST)
    public Result updateSecurityLevel(
            @Parameter(description = "节点code") @RequestParam("code") Long infoCode,
            @Parameter(description = "密级") @RequestParam("securityLevel") String securityLevel)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();

        String userId = currentUserHolder.getUserId();

        try {
            Result<DiskInfo> result = this.diskInfoService.updateSecurityLevel(
                    infoCode, userId, securityLevel);

            if (result.isFailure()) {
                return result;
            }

            DiskInfo diskInfo = result.getData();

            DiskInfoDTO diskInfoDto = diskInfoConverter.convertOne(diskInfo);

            return Result.success(diskInfoDto);
        } catch (DiskAclException ex) {
            return Result.failure(403, ex.getAction());
        }
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
    public void setDiskTagInfoManager(DiskTagInfoManager diskTagInfoManager) {
        this.diskTagInfoManager = diskTagInfoManager;
    }

    @Resource
    public void setDiskInfoService(DiskInfoService diskInfoService) {
        this.diskInfoService = diskInfoService;
    }

    @Resource
    public void setDiskLogInternalService(
            DiskLogInternalService diskLogInternalService) {
        this.diskLogInternalService = diskLogInternalService;
    }

    @Resource
    public void setDiskInfoConverter(DiskInfoConverter diskInfoConverter) {
        this.diskInfoConverter = diskInfoConverter;
    }
}
