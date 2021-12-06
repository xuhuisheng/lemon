package com.mossle.disk.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.api.user.UserDTO;

import com.mossle.client.user.UserClient;

import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.disk.persistence.domain.DiskInfo;
import com.mossle.disk.persistence.domain.DiskRecent;
import com.mossle.disk.persistence.domain.DiskSpace;
import com.mossle.disk.persistence.manager.DiskInfoManager;
import com.mossle.disk.persistence.manager.DiskMemberManager;
import com.mossle.disk.persistence.manager.DiskRecentManager;
import com.mossle.disk.persistence.manager.DiskSpaceManager;
import com.mossle.disk.service.DiskFolderService;
import com.mossle.disk.service.DiskService;
import com.mossle.disk.service.DiskSpaceService;
import com.mossle.disk.service.internal.DiskAclInternalService;
import com.mossle.disk.service.internal.DiskQueryInternalService;
import com.mossle.disk.support.TreeViewNode;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("disk")
public class DiskSpaceController {
    private static Logger logger = LoggerFactory
            .getLogger(DiskSpaceController.class);
    private DiskSpaceManager diskSpaceManager;
    private DiskMemberManager diskMemberManager;
    private DiskInfoManager diskInfoManager;
    private DiskService diskService;
    private BeanMapper beanMapper = new BeanMapper();
    private MessageHelper messageHelper;
    private CurrentUserHolder currentUserHolder;
    private UserClient userClient;
    private DiskFolderService diskFolderService;
    private DiskSpaceService diskSpaceService;
    private DiskAclInternalService diskAclInternalService;
    private DiskQueryInternalService diskQueryInternalService;
    private JsonMapper jsonMapper = new JsonMapper();
    @Resource
    private DiskRecentManager diskRecentManager;

    @RequestMapping("disk-space-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        // String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        // propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = diskSpaceManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "disk/disk-space-list";
    }

    @RequestMapping("disk-space-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            DiskSpace diskSpace = this.diskSpaceManager.get(id);
            model.addAttribute("model", diskSpace);
        }

        return "disk/disk-space-input";
    }

    @RequestMapping("disk-space-save")
    public String save(@ModelAttribute DiskSpace diskSpace,
            RedirectAttributes redirectAttributes) {
        // String tenantId = tenantHolder.getTenantId();
        Long id = diskSpace.getId();
        DiskSpace dest = null;

        Date now = new Date();
        String userId = currentUserHolder.getUserId();

        if (id != null) {
            dest = diskSpaceManager.get(id);
            beanMapper.copy(diskSpace, dest);
        } else {
            dest = diskSpace;
            dest.setCreateTime(now);
            dest.setCreator(userId);

            // dest.setTenantId(tenantId);
        }

        diskSpaceManager.save(dest);

        if (id == null) {
            DiskInfo diskInfo = new DiskInfo();
            diskInfo.setName(dest.getName());
            diskInfo.setCreateTime(now);
            diskInfo.setCreator(userId);
            diskInfo.setDiskSpace(diskSpace);
            diskInfoManager.save(diskInfo);
        }

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/disk/disk-space-list.do";
    }

    @RequestMapping("disk-space-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<DiskSpace> diskSpaces = diskSpaceManager.findByIds(selectedItem);
        diskSpaceManager.removeAll(diskSpaces);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/disk/disk-space-list.do";
    }

    @RequestMapping("disk-space-member-list")
    public String memberList(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        // String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        // propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = diskMemberManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "disk/disk-space-member-list";
    }

    @RequestMapping("disk-space-member-save")
    public String memberSave(@RequestParam("spaceId") Long spaceId,
            @RequestParam("username") String username) {
        DiskSpace diskSpace = this.diskSpaceManager.get(spaceId);
        UserDTO userDto = userClient.findByUsername(username, "1");
        String userId = userDto.getId();
        this.diskService.addMember(diskSpace, userId);

        return "redirect:/disk/disk-space-member-list.do?filter_EQL_diskSpace.id="
                + spaceId;
    }

    @RequestMapping("space/folder")
    public String spaceFolder(@RequestParam("id") Long id, Model model) {
        logger.info("space folder");

        // String tenantId = tenantHolder.getTenantId();
        String userId = currentUserHolder.getUserId();
        DiskSpace diskSpace = this.diskSpaceManager.get(id);
        DiskInfo folder = this.diskQueryInternalService
                .findRootFolderBySpace(diskSpace.getId());

        return "redirect:/disk/folder/" + folder.getId();
    }

    @RequestMapping("space/share-input")
    public String shareInput(
            @RequestParam(value = "id", required = false) Long id, Model model) {
        if (id != null) {
            DiskSpace diskSpace = this.diskSpaceManager.get(id);
            model.addAttribute("model", diskSpace);
        }

        return "disk/space/share-input";
    }

    @RequestMapping("space/share-save")
    public String spaceSave(@ModelAttribute DiskSpace diskSpace,
            RedirectAttributes redirectAttributes) {
        // String tenantId = tenantHolder.getTenantId();
        Long id = diskSpace.getId();
        DiskSpace dest = null;

        Date now = new Date();
        String userId = currentUserHolder.getUserId();

        if (id != null) {
            dest = diskSpaceManager.get(id);
            beanMapper.copy(diskSpace, dest);
        } else {
            dest = diskSpace;
            dest.setCreateTime(now);
            dest.setCreator(userId);

            // dest.setTenantId(tenantId);
        }

        // diskSpaceManager.save(dest);
        if (id == null) {
            this.diskSpaceService.createRepoSpace(userId, diskSpace.getName());
        } else {
            diskSpaceManager.save(dest);
        }

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/disk/space/share.do";
    }

    @RequestMapping("space/share-remove")
    public String spaceShareRemove(
            @RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<DiskSpace> diskSpaces = diskSpaceManager.findByIds(selectedItem);

        // diskSpaceManager.removeAll(diskSpaces);
        for (DiskSpace diskSpace : diskSpaces) {
            diskSpace.setStatus("deleted");
            diskSpaceManager.save(diskSpace);
        }

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/disk/space/share.do";
    }

    @RequestMapping(value = "space/tree", produces = "application/json")
    @ResponseBody
    public String spaceTree(
            @RequestParam(value = "id", required = false) Long parentId,
            @RequestParam(value = "type", required = false) String parentType)
            throws Exception {
        String userId = currentUserHolder.getUserId();
        List<TreeViewNode> list = diskSpaceService.findTreeView(parentId,
                parentType, userId);
        String json = jsonMapper.toJson(list);

        // logger.info(json);
        return json;
    }

    @RequestMapping("space/space-save")
    public String spaceSave(@RequestParam("name") String name) {
        String userId = currentUserHolder.getUserId();
        diskSpaceService.createRepoSpace(userId, name);

        return "redirect:/disk/space/group.do";
    }

    @RequestMapping("space/space-remove")
    public String spaceRemove(
            @RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<DiskSpace> diskSpaces = diskSpaceManager.findByIds(selectedItem);

        // diskSpaceManager.removeAll(diskSpaces);
        for (DiskSpace diskSpace : diskSpaces) {
            diskSpace.setStatus("trash");
            diskSpaceManager.save(diskSpace);
        }

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/disk/space/group.do";
    }

    // ~
    @RequestMapping("space/home")
    public String spaceHome(Model model) {
        // TODO:
        logger.info("space home");

        String userId = currentUserHolder.getUserId();

        String hql = "from DiskRecent where creator=? order by createTime desc";
        int pageNo = 1;
        int pageSize = 100;
        Page page = this.diskRecentManager.pagedQuery(hql, pageNo, pageSize,
                userId);
        List<DiskRecent> diskRecents = (List<DiskRecent>) page.getResult();
        List<DiskInfo> diskInfos = new ArrayList<DiskInfo>();
        Set<Long> ids = new HashSet<Long>();

        for (DiskRecent diskRecent : diskRecents) {
            if (ids.contains(diskRecent.getDiskInfo().getId())) {
                continue;
            }

            ids.add(diskRecent.getDiskInfo().getId());
            diskInfos.add(diskRecent.getDiskInfo());
        }

        page.setResult(diskInfos);
        page.setTotalCount(diskInfos.size());
        model.addAttribute("page", page);

        return "disk/space/home";
    }

    @RequestMapping("space/user")
    public String spaceUser(Page page, Model model) {
        logger.info("space user");

        // String tenantId = tenantHolder.getTenantId();
        String userId = currentUserHolder.getUserId();
        DiskSpace diskSpace = this.diskSpaceService.findUserSpace(userId);
        DiskInfo folder = this.diskQueryInternalService
                .findRootFolderBySpace(diskSpace.getId());

        Long folderId = folder.getId();
        int pageNo = page.getPageNo();
        int pageSize = page.getPageSize();
        page = this.diskFolderService.findChildren(folderId, pageNo, pageSize,
                null, null, userId);

        List<DiskInfo> folders = this.diskFolderService.findFolderPath(
                folderId, userId);

        model.addAttribute("page", page);
        model.addAttribute("folderId", folderId);
        model.addAttribute("folder", folder);
        model.addAttribute("folders", folders);
        model.addAttribute("diskAcls",
                diskAclInternalService.findPermissions(folderId, userId));
        // model.addAttribute("diskTags", diskTagManager.getAll());
        model.addAttribute("space", diskSpace);
        model.addAttribute("folder", folder);

        return "disk/space/index";
    }

    @RequestMapping("space/group")
    public String spaceGroup(
            @RequestParam(value = "filter_EQS_name", required = false) String spaceName,
            Page page, Model model) {
        logger.info("space group");

        // String tenantId = tenantHolder.getTenantId();
        String userId = currentUserHolder.getUserId();

        page = diskQueryInternalService
                .findGroupSpaces(spaceName, userId, page);

        model.addAttribute("page", page);

        return "disk/space/group";
    }

    @RequestMapping("space/share")
    public String spaceShare(
            @RequestParam(value = "filter_EQS_name", required = false) String name,
            Page page, Model model) {
        logger.info("space share");

        // String tenantId = tenantHolder.getTenantId();
        String userId = currentUserHolder.getUserId();

        if (StringUtils.isBlank(name)) {
            String hql = "select space from DiskSpace space join space.diskMembers m "
                    + " where m.userId=? and space.status='active' order by id";
            page = diskSpaceManager.pagedQuery(hql, page.getPageNo(),
                    page.getPageSize(), userId);
        } else {
            String hql = "select space from DiskSpace space join space.diskMembers m "
                    + " where m.userId=? and space.status='active' and space.name like ? order by id";
            page = diskSpaceManager.pagedQuery(hql, page.getPageNo(),
                    page.getPageSize(), userId, "%" + name + "%");
        }

        model.addAttribute("page", page);

        return "disk/space/share";
    }

    @RequestMapping("space/favorite")
    public String spaceFavorite(Model model) {
        // TODO:
        logger.info("space favorite");

        return "disk/space/favorite";
    }

    @RequestMapping("space/trash")
    public String spaceTrash(Model model) {
        // TODO:
        logger.info("space trash");

        return "disk/space/trash";
    }

    // ~ ======================================================================
    @Resource
    public void setDiskSpaceManager(DiskSpaceManager diskSpaceManager) {
        this.diskSpaceManager = diskSpaceManager;
    }

    @Resource
    public void setDiskMemberManager(DiskMemberManager diskMemberManager) {
        this.diskMemberManager = diskMemberManager;
    }

    @Resource
    public void setDiskInfoManager(DiskInfoManager diskInfoManager) {
        this.diskInfoManager = diskInfoManager;
    }

    @Resource
    public void setDiskService(DiskService diskService) {
        this.diskService = diskService;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

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
    public void setDiskAclInternalService(
            DiskAclInternalService diskAclInternalService) {
        this.diskAclInternalService = diskAclInternalService;
    }

    @Resource
    public void setDiskQueryInternalService(
            DiskQueryInternalService diskQueryInternalService) {
        this.diskQueryInternalService = diskQueryInternalService;
    }
}
