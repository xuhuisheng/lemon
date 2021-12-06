package com.mossle.disk.web.rs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.api.user.UserDTO;

import com.mossle.client.user.UserClient;

import com.mossle.core.page.Page;

import com.mossle.disk.component.DiskInfoConverter;
import com.mossle.disk.persistence.domain.DiskAcl;
import com.mossle.disk.persistence.domain.DiskInfo;
import com.mossle.disk.persistence.domain.DiskRequest;
import com.mossle.disk.persistence.domain.DiskSid;
import com.mossle.disk.persistence.domain.DiskSpace;
import com.mossle.disk.persistence.manager.DiskInfoManager;
import com.mossle.disk.persistence.manager.DiskLogManager;
import com.mossle.disk.service.DiskFolderService;
import com.mossle.disk.service.DiskInfoService;
import com.mossle.disk.service.DiskShareService;
import com.mossle.disk.service.DiskSpaceService;
import com.mossle.disk.service.internal.DiskAclInternalService;
import com.mossle.disk.service.internal.DiskQueryInternalService;
import com.mossle.disk.support.DiskAclException;
import com.mossle.disk.support.DiskInfoDTO;
import com.mossle.disk.support.DiskRequestDTO;
import com.mossle.disk.support.Result;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "分享")
@RestController
@RequestMapping("disk/rs/share")
public class DiskShareRestController {
    private static Logger logger = LoggerFactory
            .getLogger(DiskShareRestController.class);
    private CurrentUserHolder currentUserHolder;
    private DiskSpaceService diskSpaceService;
    private DiskFolderService diskFolderService;
    private DiskQueryInternalService diskQueryInternalService;
    private DiskAclInternalService diskAclInternalService;
    private DiskInfoService diskInfoService;
    private DiskShareService diskShareService;
    private UserClient userClient;
    private DiskInfoConverter diskInfoConverter;
    @Resource
    private DiskInfoManager diskInfoManager;
    @Resource
    private DiskLogManager diskLogManager;

    /**
     * 我分享的文件.
     */
    @Operation(summary = "我分享的文件")
    @RequestMapping(value = "from-me", method = RequestMethod.GET)
    public Result fromMe(Page page) throws Exception {
        logger.info("from me");

        String userId = currentUserHolder.getUserId();
        page = diskShareService.mySharedList(userId, page.getPageNo(),
                page.getPageSize());

        List<DiskInfo> diskInfos = (List<DiskInfo>) page.getResult();
        List<DiskInfoDTO> diskInfoDtos = this.diskInfoConverter
                .convertList(diskInfos);
        page.setResult(diskInfoDtos);

        return Result.success(page);
    }

    /**
     * 分享给我的文件.
     */
    @Operation(summary = "分享给我的文件")
    @RequestMapping(value = "to-me", method = RequestMethod.GET)
    public Result toMe(Page page) throws Exception {
        logger.info("to me");

        String userId = currentUserHolder.getUserId();
        page = diskShareService.shareToMeList(userId, page.getPageNo(),
                page.getPageSize());

        List<DiskInfo> diskInfos = (List<DiskInfo>) page.getResult();
        List<DiskInfoDTO> diskInfoDtos = this.diskInfoConverter
                .convertList(diskInfos);
        page.setResult(diskInfoDtos);

        return Result.success(page);
    }

    /**
     * 协作者列表.
     */
    @Operation(summary = "协作者列表")
    @RequestMapping(value = "member/list", method = RequestMethod.GET)
    public Result memberList(
            @Parameter(description = "节点code") @RequestParam("code") Long infoCode)
            throws Exception {
        logger.info("member list");

        String userId = currentUserHolder.getUserId();

        try {
            Result<DiskInfo> result = this.diskInfoService.findById(infoCode,
                    userId);

            if (result.isFailure()) {
                return result;
            }

            List<Map<String, Object>> list = this.findPerms(infoCode, userId);

            return Result.success(Collections.singletonMap("list", list));
        } catch (DiskAclException ex) {
            return Result.failure(403, ex.getAction());
        }
    }

    public List<Map<String, Object>> findPerms(long infoCode, String userId) {
        Result<DiskInfo> result = this.diskInfoService.findById(infoCode,
                userId);

        if (result.isFailure()) {
            return Collections.emptyList();
        }

        DiskInfo diskInfo = result.getData();
        List<DiskAcl> diskAcls = this.diskAclInternalService.findPermissions(
                infoCode, userId);

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> ownerItem = new HashMap<String, Object>();
        ownerItem.put("memberCode", userId);
        ownerItem.put("memberName",
                userClient.findById(diskInfo.getCreator(), "1")
                        .getDisplayName());
        ownerItem.put("memberAvatar", "");
        ownerItem.put("memberType", "owner");
        ownerItem.put("mask", "owner");
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
            item.put("memberCode", diskSid.getValue());
            item.put("memberName", diskSid.getName());
            item.put("memberType", diskSid.getCatalog());
            item.put("memberAvatar", "");
            item.put("mask", (diskAcl.getMask() == 1) ? "read" : "edit");

            if ("user".equals(diskSid.getCatalog())) {
                String targetUserId = diskSid.getValue();
                UserDTO userDto = userClient.findById(targetUserId, "1");
                item.put("memberName", userDto.getDisplayName());
            }

            list.add(item);
        }

        return list;
    }

    /**
     * 添加权限.
     */
    @Operation(summary = "添加权限")
    @RequestMapping(value = "member/add", method = RequestMethod.POST)
    public Result updatePermission(
            @Parameter(description = "节点code") @RequestParam("code") Long infoCode,
            @Parameter(description = "成员code") @RequestParam("memberCode") String memberCode,
            @Parameter(description = "成员类型") @RequestParam("memberType") String memberType,
            @Parameter(description = "成员名称") @RequestParam("memberName") String memberName,
            @Parameter(description = "权限") @RequestParam("mask") String mask) {
        String userId = currentUserHolder.getUserId();

        try {
            this.diskAclInternalService.addPermissionByMemberCode(infoCode,
                    userId, memberCode, memberType, memberName, mask);

            List<Map<String, Object>> list = this.findPerms(infoCode, userId);

            return Result.success(Collections.singletonMap("list", list));
        } catch (DiskAclException ex) {
            return Result.failure(403, ex.getAction());
        }
    }

    /**
     * 修改权限.
     */
    @Operation(summary = "修改权限")
    @RequestMapping(value = "member/update", method = RequestMethod.POST)
    public Result updatePermission(
            @Parameter(description = "节点code") @RequestParam("code") Long infoCode,
            @Parameter(description = "成员code") @RequestParam("memberCode") String memberCode,
            @Parameter(description = "成员类型") @RequestParam("memberType") String memberType,
            @Parameter(description = "权限") @RequestParam("mask") String mask) {
        String userId = currentUserHolder.getUserId();

        try {
            this.diskAclInternalService.updatePermissionByMemberCode(infoCode,
                    userId, memberCode, memberType, mask);

            List<Map<String, Object>> list = this.findPerms(infoCode, userId);

            return Result.success(Collections.singletonMap("list", list));
        } catch (DiskAclException ex) {
            return Result.failure(403, ex.getAction());
        }
    }

    /**
     * 删除权限.
     */
    @Operation(summary = "删除权限")
    @RequestMapping(value = "member/remove", method = RequestMethod.POST)
    public Result removePermission(
            @Parameter(description = "节点code") @RequestParam("code") Long infoCode,
            @Parameter(description = "成员code") @RequestParam("memberCode") String memberCode) {
        String userId = currentUserHolder.getUserId();

        try {
            // this.diskAclService
            // .removePermission(entityCatalog, entityRef, folderId);
            this.diskAclInternalService.removePermissionByMemberCode(infoCode,
                    userId, memberCode);

            List<Map<String, Object>> list = this.findPerms(infoCode, userId);

            return Result.success(Collections.singletonMap("list", list));
        } catch (DiskAclException ex) {
            return Result.failure(403, ex.getAction());
        }
    }

    /**
     * 批量添加协作者.
     */
    @Operation(summary = "批量添加协作者")
    @RequestMapping(value = "member/batch/save", method = RequestMethod.POST)
    public Result updatePermission(@RequestBody String text) throws Exception {
        String userId = currentUserHolder.getUserId();

        try {
            Result<DiskInfo> result = this.diskAclInternalService
                    .batchSave(text);

            if (result.isFailure()) {
                return result;
            }

            DiskInfo diskInfo = result.getData();
            long infoCode = diskInfo.getId();

            List<Map<String, Object>> list = this.findPerms(infoCode, userId);

            return Result.success(Collections.singletonMap("list", list));
        } catch (DiskAclException ex) {
            return Result.failure(403, ex.getAction());
        }
    }

    /**
     * 分享设置详情.
     */
    @Operation(summary = "分享设置详情")
    @RequestMapping(value = "setting/detail", method = RequestMethod.GET)
    public Result removePermission(
            @Parameter(description = "节点code") @RequestParam("code") Long infoCode) {
        String userId = currentUserHolder.getUserId();

        try {
            Result<DiskInfo> result = diskInfoService
                    .findById(infoCode, userId);

            if (result.isFailure()) {
                return result;
            }

            DiskInfo diskInfo = result.getData();
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("public", "true".equals(diskInfo.getPublicType()));
            data.put("editable", "true".equals(diskInfo.getPublicEdit()));

            return Result.success(data);
        } catch (DiskAclException ex) {
            return Result.failure(403, ex.getAction());
        }
    }

    /**
     * 保存设置详情.
     */
    @Operation(summary = "保存设置详情")
    @RequestMapping(value = "setting/save", method = RequestMethod.POST)
    public Result removePermission(
            @Parameter(description = "节点code") @RequestParam("code") Long infoCode,
            @Parameter(description = "是否公开") @RequestParam("public") boolean publicString,
            @Parameter(description = "公开是否可编辑") @RequestParam("editable") boolean editable) {
        String userId = currentUserHolder.getUserId();

        try {
            Result<DiskInfo> result = diskInfoService
                    .findById(infoCode, userId);

            if (result.isFailure()) {
                return result;
            }

            DiskInfo diskInfo = result.getData();
            diskInfo.setPublicType(Boolean.toString(publicString));
            diskInfo.setPublicEdit(Boolean.toString(editable));
            diskInfoService.save(diskInfo);

            Map<String, Object> data = new HashMap<String, Object>();
            data.put("public", "true".equals(diskInfo.getPublicType()));
            data.put("editable", "true".equals(diskInfo.getPublicEdit()));

            return Result.success(data);
        } catch (DiskAclException ex) {
            return Result.failure(403, ex.getAction());
        }
    }

    /**
     * 申请权限.
     */
    @Operation(summary = "申请权限")
    @RequestMapping(value = "request/create", method = RequestMethod.POST)
    public Result requestCreate(
            @Parameter(description = "节点code") @RequestParam("code") Long infoCode,
            @Parameter(description = "权限") @RequestParam("mask") String permission,
            @Parameter(description = "备注") @RequestParam(value = "description", required = false) String description) {
        String userId = currentUserHolder.getUserId();

        try {
            int mask = "read".equals(permission) ? 1 : 127;
            DiskRequest diskRequest = diskShareService.create(infoCode, userId,
                    mask, description);
            DiskRequestDTO diskRequestDto = convertDiskRequest(diskRequest);

            return Result.success(diskRequestDto);
        } catch (DiskAclException ex) {
            return Result.failure(403, ex.getAction());
        }
    }

    public DiskRequestDTO convertDiskRequest(DiskRequest diskRequest) {
        UserDTO member = userClient.findById(diskRequest.getCreator(), "1");
        UserDTO owner = userClient.findById(diskRequest.getDiskInfo()
                .getCreator(), "1");
        DiskRequestDTO diskRequestDto = new DiskRequestDTO();
        diskRequestDto.setCode(Long.toString(diskRequest.getId()));
        diskRequestDto.setName(diskRequest.getDiskInfo().getName());
        diskRequestDto.setMemberCode(member.getId());
        diskRequestDto.setMemberName(member.getDisplayName());
        diskRequestDto.setDescription(diskRequest.getDescription());
        diskRequestDto.setStatus(diskRequest.getStatus());
        diskRequestDto.setMask((diskRequest.getMask() == 1) ? "read" : "edit");
        diskRequestDto.setResult(diskRequest.getResult());
        diskRequestDto.setOwnerId(owner.getId());
        diskRequestDto.setOwnerName(owner.getDisplayName());

        return diskRequestDto;
    }

    /**
     * 通过申请.
     */
    @Operation(summary = "通过申请")
    @RequestMapping(value = "request/approve", method = RequestMethod.POST)
    public Result requestApprove(
            @Parameter(description = "申请code") @RequestParam("code") Long requestCode,
            @Parameter(description = "备注") @RequestParam(value = "description", required = false) String description) {
        String userId = currentUserHolder.getUserId();

        try {
            DiskRequest diskRequest = diskShareService.approve(requestCode,
                    userId, description);

            DiskRequestDTO diskRequestDto = convertDiskRequest(diskRequest);

            return Result.success(diskRequestDto);
        } catch (DiskAclException ex) {
            return Result.failure(403, ex.getAction());
        }
    }

    /**
     * 拒绝申请.
     */
    @Operation(summary = "拒绝申请")
    @RequestMapping(value = "request/reject", method = RequestMethod.POST)
    public Result requestReject(
            @Parameter(description = "申请code") @RequestParam("code") Long requestCode,
            @Parameter(description = "备注") @RequestParam(value = "description", required = false) String description) {
        String userId = currentUserHolder.getUserId();

        try {
            DiskRequest diskRequest = diskShareService.reject(requestCode,
                    userId, description);

            DiskRequestDTO diskRequestDto = convertDiskRequest(diskRequest);

            return Result.success(diskRequestDto);
        } catch (DiskAclException ex) {
            return Result.failure(403, ex.getAction());
        }
    }

    /**
     * 申请详情.
     */
    @Operation(summary = "申请详情")
    @RequestMapping(value = "request/detail", method = RequestMethod.GET)
    public Result requestDetail(
            @Parameter(description = "申请code") @RequestParam("code") Long requestCode) {
        String userId = currentUserHolder.getUserId();

        try {
            DiskRequest diskRequest = diskShareService.findById(requestCode,
                    userId);

            if (diskRequest == null) {
                return Result.failure(404, "no request : " + requestCode);
            }

            DiskRequestDTO diskRequestDto = convertDiskRequest(diskRequest);

            return Result.success(diskRequestDto);
        } catch (DiskAclException ex) {
            return Result.failure(403, ex.getAction());
        }
    }

    /**
     * 检查是否拥有当前节点权限.
     */
    @Operation(summary = "检查是否拥有当前节点权限")
    @RequestMapping(value = "check", method = RequestMethod.GET)
    public Result check(
            @Parameter(description = "申请code") @RequestParam("code") Long infoCode,
            @Parameter(description = "权限") @RequestParam("mask") String maskText) {
        String userId = currentUserHolder.getUserId();

        try {
            int mask = -1;

            if ("read".equals(maskText)) {
                mask = 1;
            } else if ("edit".equals(maskText)) {
                mask = 127;
            }

            boolean result = diskShareService.check(infoCode, userId, mask);

            return Result.success(Collections.singletonMap("result", result));
        } catch (DiskAclException ex) {
            return Result.failure(403, ex.getAction());
        }
    }

    // ~ ======================================================================
    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setUserClient(UserClient userClient) {
        this.userClient = userClient;
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
    public void setDiskQueryInternalService(
            DiskQueryInternalService diskQueryInternalService) {
        this.diskQueryInternalService = diskQueryInternalService;
    }

    @Resource
    public void setDiskAclInternalService(
            DiskAclInternalService diskAclInternalService) {
        this.diskAclInternalService = diskAclInternalService;
    }

    @Resource
    public void setDiskInfoService(DiskInfoService diskInfoService) {
        this.diskInfoService = diskInfoService;
    }

    @Resource
    public void setDiskShareService(DiskShareService diskShareService) {
        this.diskShareService = diskShareService;
    }

    @Resource
    public void setDiskInfoConverter(DiskInfoConverter diskInfoConverter) {
        this.diskInfoConverter = diskInfoConverter;
    }
}
