package com.mossle.disk.web.rs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.validation.Validation;
import javax.validation.Validator;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.api.user.UserDTO;

import com.mossle.client.user.UserClient;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.page.Page;

import com.mossle.disk.component.DiskInfoConverter;
import com.mossle.disk.persistence.domain.DiskInfo;
import com.mossle.disk.persistence.domain.DiskRecent;
import com.mossle.disk.persistence.domain.DiskSpace;
import com.mossle.disk.persistence.manager.DiskInfoManager;
import com.mossle.disk.persistence.manager.DiskLogManager;
import com.mossle.disk.persistence.manager.DiskRecentManager;
import com.mossle.disk.service.DiskFileService;
import com.mossle.disk.service.DiskFolderService;
import com.mossle.disk.service.DiskSpaceService;
import com.mossle.disk.service.internal.DiskLogInternalService;
import com.mossle.disk.service.internal.DiskQueryInternalService;
import com.mossle.disk.support.DiskAclException;
import com.mossle.disk.support.DiskInfoDTO;
import com.mossle.disk.support.DiskSpaceDTO;
import com.mossle.disk.support.Result;
import com.mossle.disk.support.TreeViewNode;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "列表")
@RestController
@RequestMapping("disk/rs/space")
public class DiskSpaceRestController {
    private static Logger logger = LoggerFactory
            .getLogger(DiskSpaceRestController.class);
    private CurrentUserHolder currentUserHolder;
    private UserClient userClient;
    private DiskSpaceService diskSpaceService;
    private DiskFolderService diskFolderService;
    private DiskFileService diskFileService;
    private DiskQueryInternalService diskQueryInternalService;
    private DiskLogInternalService diskLogInternalService;
    private DiskInfoConverter diskInfoConverter;
    private JsonMapper jsonMapper = new JsonMapper();
    private Validator validator = Validation.buildDefaultValidatorFactory()
            .getValidator();
    @Resource
    private DiskInfoManager diskInfoManager;
    @Resource
    private DiskLogManager diskLogManager;
    @Resource
    private DiskRecentManager diskRecentManager;

    /**
     * 我的空间下的子节点.
     */
    @Operation(summary = "我的空间")
    @RequestMapping(value = "user", method = RequestMethod.GET)
    public Result user(Page page) throws Exception {
        logger.info("user");

        String userId = currentUserHolder.getUserId();
        DiskSpace diskSpace = this.diskSpaceService.findUserSpace(userId);
        Long spaceId = diskSpace.getId();
        DiskInfo folder = this.diskQueryInternalService
                .findRootFolderBySpace(spaceId);
        Long folderId = folder.getId();
        int pageNo = page.getPageNo();
        int pageSize = page.getPageSize();
        page = this.diskFolderService.findChildren(folderId, pageNo, pageSize,
                null, null, userId);

        List<DiskInfo> diskInfos = (List<DiskInfo>) page.getResult();
        List<DiskInfoDTO> diskInfoDtos = diskInfoConverter.convertList(
                diskInfos, userId);
        page.setResult(diskInfoDtos);

        return Result.success(page);
    }

    /**
     * 部门空间. TODO:
     */
    @Operation(summary = "部门空间")
    @RequestMapping(value = "dept", method = RequestMethod.GET)
    public Result dept(Page page) throws Exception {
        logger.info("dept");

        String userId = currentUserHolder.getUserId();
        DiskSpace diskSpace = this.diskSpaceService.findUserSpace(userId);
        Long spaceId = diskSpace.getId();
        DiskInfo folder = this.diskQueryInternalService
                .findRootFolderBySpace(spaceId);
        Long folderId = folder.getId();
        int pageNo = page.getPageNo();
        int pageSize = page.getPageSize();
        page = this.diskFolderService.findChildren(folderId, pageNo, pageSize,
                null, null, userId);

        List<DiskInfo> diskInfos = (List<DiskInfo>) page.getResult();
        List<DiskInfoDTO> diskInfoDtos = diskInfoConverter.convertList(
                diskInfos, userId);
        page.setResult(diskInfoDtos);

        return Result.success(page);
    }

    /**
     * 最近访问.
     */
    @Operation(summary = "最近访问")
    @RequestMapping(value = "recent", method = RequestMethod.GET)
    public Result recent() throws Exception {
        logger.info("recent");

        String userId = currentUserHolder.getUserId();

        String hql = "from DiskRecent where creator=? order by createTime desc";
        int pageNo = 1;
        int pageSize = 100;
        Page page = this.diskRecentManager.pagedQuery(hql, pageNo, pageSize,
                userId);
        List<DiskRecent> diskRecents = (List<DiskRecent>) page.getResult();

        List<Map<String, Object>> groups = new ArrayList<Map<String, Object>>();

        groups.add(buildTodayGroup(userId, diskRecents));
        groups.add(buildSevenGroup(userId, diskRecents));
        groups.add(buildOldGroup(userId, diskRecents));

        return Result.success(Collections.singletonMap("groups", groups));
    }

    public Map<String, Object> buildTodayGroup(String userId,
            List<DiskRecent> diskRecents) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<DiskInfoDTO> list = new ArrayList<DiskInfoDTO>();
        map.put("code", "today");
        map.put("name", "今天");
        map.put("list", list);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long time = calendar.getTime().getTime();

        for (DiskRecent diskRecent : diskRecents) {
            if (diskRecent.getCreateTime().getTime() > time) {
                DiskInfoDTO diskInfoDto = diskInfoConverter.convertOne(
                        diskRecent.getDiskInfo(), userId);
                list.add(diskInfoDto);
            }
        }

        return map;
    }

    public Map<String, Object> buildSevenGroup(String userId,
            List<DiskRecent> diskRecents) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<DiskInfoDTO> list = new ArrayList<DiskInfoDTO>();
        map.put("code", "seven");
        map.put("name", "最近七天");
        map.put("list", list);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long endTime = calendar.getTime().getTime();
        calendar.add(Calendar.DAY_OF_YEAR, -7);

        long startTime = calendar.getTime().getTime();

        for (DiskRecent diskRecent : diskRecents) {
            if ((diskRecent.getCreateTime().getTime() > startTime)
                    && (diskRecent.getCreateTime().getTime() < endTime)) {
                DiskInfoDTO diskInfoDto = diskInfoConverter.convertOne(
                        diskRecent.getDiskInfo(), userId);
                list.add(diskInfoDto);
            }
        }

        return map;
    }

    public Map<String, Object> buildOldGroup(String userId,
            List<DiskRecent> diskRecents) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<DiskInfoDTO> list = new ArrayList<DiskInfoDTO>();
        map.put("code", "old");
        map.put("name", "七天之前");
        map.put("list", list);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DAY_OF_YEAR, -7);

        long time = calendar.getTime().getTime();

        for (DiskRecent diskRecent : diskRecents) {
            if (diskRecent.getCreateTime().getTime() < time) {
                DiskInfoDTO diskInfoDto = diskInfoConverter.convertOne(
                        diskRecent.getDiskInfo(), userId);
                list.add(diskInfoDto);
            }
        }

        return map;
    }

    /**
     * 回收站.
     */
    @Operation(summary = "回收站")
    @RequestMapping(value = "trash", method = RequestMethod.GET)
    public Result trash(Page page) {
        logger.info("trash");

        String userId = currentUserHolder.getUserId();

        try {
            page = this.diskFileService.findTrash(userId, page.getPageNo(),
                    page.getPageSize());

            List<DiskInfo> diskInfos = (List<DiskInfo>) page.getResult();
            List<DiskInfoDTO> list = this.diskInfoConverter.convertList(
                    diskInfos, userId);
            page.setResult(list);

            return Result.success(page);
        } catch (DiskAclException ex) {
            return Result.failure(403, ex.getAction());
        }
    }

    /**
     * 搜索.
     */
    @Operation(summary = "搜索")
    @RequestMapping(value = "search", method = RequestMethod.GET)
    public Result search(
            @Parameter(description = "搜索内容") @RequestParam("search") String query,
            @Parameter(description = "排序方式") @RequestParam(value = "sort", required = false) String sort,
            @Parameter(description = "筛选类型") @RequestParam(value = "filterValue", required = false) String filterValue,
            @Parameter(description = "搜索类型") @RequestParam(value = "searchType", required = false) String searchType,
            Page page) {
        String userId = currentUserHolder.getUserId();

        try {
            page = this.diskFileService.searchAdvanced(query, sort,
                    filterValue, searchType, userId, page);

            List<DiskInfo> diskInfos = (List<DiskInfo>) page.getResult();
            List<DiskInfoDTO> list = this.diskInfoConverter.convertList(
                    diskInfos, userId);
            page.setResult(list);

            return Result.success(page);
        } catch (DiskAclException ex) {
            return Result.failure(403, ex.getAction());
        }
    }

    /**
     * 我的空间的详情.
     */
    @Operation(summary = "我的空间的详情")
    @RequestMapping(value = "profile", method = RequestMethod.GET)
    public Result profile() throws Exception {
        String userId = currentUserHolder.getUserId();
        UserDTO userDto = userClient.findById(userId, "1");
        DiskSpace diskSpace = this.diskSpaceService.findUserSpace(userId);
        DiskInfo rootFolder = this.diskQueryInternalService
                .findRootFolderBySpace(diskSpace.getId());

        DiskSpaceDTO diskSpaceDto = new DiskSpaceDTO();
        diskSpaceDto.setCode(Long.toString(diskSpace.getId()));
        diskSpaceDto.setName(diskSpace.getName());
        diskSpaceDto.setType(diskSpace.getCatalog());
        diskSpaceDto.setOwnerId(userId);
        diskSpaceDto.setOwnerName(userDto.getDisplayName());
        diskSpaceDto.setOwnerAvatar("");
        diskSpaceDto.setFolderCode(Long.toString(rootFolder.getId()));

        // diskSpaceDto.setUserCount(this.diskLogInternalService
        // .findUserCountByUserId(userId));
        // diskSpaceDto.setClickCount(this.diskLogInternalService
        // .findClickCountByUserId(userId));
        // diskSpaceDto.setDownloadCount(this.diskLogInternalService
        // .findDownloadCountByUserId(userId));
        return Result.success(diskSpaceDto);
    }

    /**
     * 空间列表.
     */
    @Operation(summary = "空间列表")
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public Result list() throws Exception {
        String userId = currentUserHolder.getUserId();
        Map<String, Object> data = new HashMap<String, Object>();
        List<Object> spaces = new ArrayList<Object>();
        data.put("result", spaces);

        // TODO: record recent folder
        // List<Object> folders = new ArrayList<Object>();
        String hql = " select distinct info from DiskInfo info, DiskLog log "
                + " where info.id=log.parentId and log.creator=? "
                + " order by log.id desc";
        Page page = diskInfoManager.pagedQuery(hql, 1, 5, userId);
        List<DiskInfo> folders = (List<DiskInfo>) page.getResult();

        data.put("folders", diskInfoConverter.convertList(folders));

        DiskSpace userSpace = this.diskSpaceService.findUserSpace(userId);
        DiskInfo userSpaceRootFolder = this.diskQueryInternalService
                .findRootFolderBySpace(userSpace.getId());
        spaces.add(diskInfoConverter.convertOne(userSpaceRootFolder));
        spaces.add(this.buildSpace(0L, "部门空间", "dept", 0L));

        return Result.success(data);
    }

    public Map<String, Object> buildSpace(long code, String name,
            String spaceType, long spaceCode) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", Long.toString(code));
        map.put("name", name);
        map.put("spaceCode", Long.toString(spaceCode));
        map.put("spaceType", spaceType);

        return map;
    }

    /**
     * 全部文件.
     */
    @Operation(summary = "全部文件")
    @RequestMapping(value = "all", method = RequestMethod.GET)
    public Result all(Page page) throws Exception {
        logger.info("all");

        String userId = currentUserHolder.getUserId();

        String hql = "from DiskInfo where dirType=1 and status='active' and creator=? order by createTime desc";
        int pageNo = page.getPageNo();
        int pageSize = page.getPageSize();
        page = this.diskInfoManager.pagedQuery(hql, pageNo, pageSize, userId);

        List<DiskInfo> diskInfos = (List<DiskInfo>) page.getResult();
        List<DiskInfoDTO> diskInfoDtos = diskInfoConverter.convertList(
                diskInfos, userId);
        page.setResult(diskInfoDtos);

        return Result.success(page);
    }

    /**
     * 部门空间下的子节点.
     */
    @Operation(summary = "部门空间")
    @RequestMapping(value = "group", method = RequestMethod.GET)
    public Result group(
            @Parameter(description = "部门code") @RequestParam("code") Long spaceCode)
            throws Exception {
        logger.info("group");

        String userId = currentUserHolder.getUserId();
        DiskSpace diskSpace = this.diskSpaceService.findById(spaceCode);
        Long spaceId = diskSpace.getId();
        DiskInfo folder = this.diskQueryInternalService
                .findRootFolderBySpace(spaceId);
        Long folderId = folder.getId();
        int pageNo = 1;
        int pageSize = 100;
        Page page = this.diskFolderService.findChildren(folderId, pageNo,
                pageSize, userId, null, null);
        List<DiskInfo> diskInfos = (List<DiskInfo>) page.getResult();

        List<DiskInfoDTO> diskInfoDtos = diskInfoConverter.convertList(
                diskInfos, userId);

        return Result.success(Collections.singletonMap("result", diskInfoDtos));
    }

    /**
     * 左侧菜单的顶级节点.
     */
    @Operation(summary = "左侧菜单的顶级节点")
    @RequestMapping(value = "treeRoot", method = RequestMethod.GET, produces = "application/json")
    public String treeRoot(
            @Parameter(description = "隐藏回收站") @RequestParam(value = "hideTrash", required = false) Boolean hideTrash)
            throws Exception {
        logger.debug("tree root");

        String userId = currentUserHolder.getUserId();
        List<TreeViewNode> list = diskSpaceService.findTreeRoot(userId,
                hideTrash == Boolean.TRUE);
        String json = jsonMapper.toJson(list);

        // logger.info(json);
        return json;
    }

    /**
     * 共享空间子节点.
     */
    @Operation(summary = "共享空间子节点")
    @RequestMapping(value = "treeSpaces", method = RequestMethod.GET, produces = "application/json")
    public String treeSpaces() throws Exception {
        String userId = currentUserHolder.getUserId();
        List<TreeViewNode> list = diskSpaceService.findTreeSpaces(userId);
        String json = jsonMapper.toJson(list);

        // logger.info(json);
        return json;
    }

    /**
     * TODO: 可能不需要空间下顶级文件夹的接口，因为每个空间都对应一个根文件夹，可以使用根文件夹查询子节点.
     */
    @RequestMapping(value = "treeSpaceFolders", produces = "application/json")
    public String treeSpaceFolders() throws Exception {
        String userId = currentUserHolder.getUserId();
        List<TreeViewNode> list = diskSpaceService.findTreeSpaceFolders(userId);
        String json = jsonMapper.toJson(list);

        // logger.info(json);
        return json;
    }

    /**
     * 文件夹的子节点.
     */
    @Operation(summary = "文件夹的子节点")
    @RequestMapping(value = "treeFolders", method = RequestMethod.GET, produces = "application/json")
    public String treeFolders(
            @Parameter(description = "文件夹code") @RequestParam("code") Long folderCode)
            throws Exception {
        String userId = currentUserHolder.getUserId();
        List<TreeViewNode> list = diskSpaceService.findTreeFolders(folderCode,
                userId);
        String json = jsonMapper.toJson(list);

        // logger.info(json);
        return json;
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
    public void setDiskFileService(DiskFileService diskFileService) {
        this.diskFileService = diskFileService;
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

    @Resource
    public void setDiskInfoConverter(DiskInfoConverter diskInfoConverter) {
        this.diskInfoConverter = diskInfoConverter;
    }
}
