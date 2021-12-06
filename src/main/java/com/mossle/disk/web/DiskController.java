package com.mossle.disk.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.api.user.UserDTO;

import com.mossle.client.user.UserClient;

import com.mossle.core.page.Page;

import com.mossle.disk.persistence.domain.DiskInfo;
import com.mossle.disk.persistence.domain.DiskShare;
import com.mossle.disk.persistence.domain.DiskSpace;
import com.mossle.disk.persistence.manager.DiskInfoManager;
import com.mossle.disk.persistence.manager.DiskShareManager;
import com.mossle.disk.persistence.manager.DiskSpaceManager;
import com.mossle.disk.service.DiskInfoService;
import com.mossle.disk.service.DiskService;
import com.mossle.disk.service.DiskSpaceService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("disk")
public class DiskController {
    private static Logger logger = LoggerFactory
            .getLogger(DiskController.class);
    private DiskShareManager diskShareManager;
    private DiskInfoManager diskInfoManager;
    private UserClient userClient;
    private DiskSpaceManager diskSpaceManager;
    private CurrentUserHolder currentUserHolder;
    private DiskService diskService;
    private DiskSpaceService diskSpaceService;
    private DiskInfoService diskInfoService;

    /**
     * 个人文档.
     */
    @RequestMapping("index")
    public String index(
            @RequestParam(value = "path", required = false, defaultValue = "") String path,
            Model model) {
        logger.debug("index");

        // return "redirect:/disk/space/user.do";
        String userId = currentUserHolder.getUserId();
        DiskSpace diskSpace = this.diskSpaceService.findUserSpace(userId);

        List<DiskInfo> diskInfos = diskService.listFiles(diskSpace, path);
        model.addAttribute("diskInfos", diskInfos);
        model.addAttribute("diskSpace", diskSpace);
        model.addAttribute("path", path);

        return "disk/index";
    }

    /**
     * 共享文档.
     */
    @RequestMapping("share")
    public String share(
            @RequestParam(value = "spaceId", required = false) Long spaceId,
            @RequestParam(value = "shareId", required = false) Long shareId,
            @RequestParam(value = "path", required = false, defaultValue = "") String path,
            Model model) {
        // 如果spaceId存在，就显示对应空间下的共享
        // 如果spaceId不存在，就显示可以查看的共享空间
        if (spaceId == null) {
            String userId = currentUserHolder.getUserId();
            List<DiskSpace> diskSpaces = this.diskSpaceService
                    .findShareSpaces(userId);
            model.addAttribute("diskSpaces", diskSpaces);

            return "disk/share-space";
        }

        if ((spaceId != null) && (shareId == null)) {
            String userId = currentUserHolder.getUserId();
            String hql = "select diskShare from DiskShare diskShare join diskShare.diskMembers diskMember where diskMember.userId=?";
            List<DiskShare> diskShares = diskShareManager.find(hql, userId);
            model.addAttribute("diskShares", diskShares);

            return "disk/share-share";
        }

        DiskShare diskShare = diskShareManager.get(shareId);
        DiskSpace diskSpace = diskShare.getDiskInfo().getDiskSpace();

        List<DiskInfo> diskInfos = diskService.listFiles(diskShare, path);
        model.addAttribute("diskInfos", diskInfos);
        model.addAttribute("diskSpace", diskSpace);
        model.addAttribute("path", path);

        return "disk/share";
    }

    /**
     * 群组文档.
     */
    @RequestMapping("group")
    public String group(
            @RequestParam(value = "spaceId", required = false) Long spaceId,
            @RequestParam(value = "path", required = false, defaultValue = "") String path,
            Model model) {
        // 如果spaceId存在，就显示对应空间下的群组
        // 如果spaceId不存在，就显示可以查看的群组空间
        if (spaceId == null) {
            String userId = currentUserHolder.getUserId();
            List<DiskSpace> diskSpaces = this.diskSpaceService
                    .findGroupSpaces(userId);
            model.addAttribute("diskSpaces", diskSpaces);

            return "disk/group-space";
        }

        DiskSpace diskSpace = this.diskSpaceManager.get(spaceId);

        List<DiskInfo> diskInfos = diskService.listFiles(diskSpace, path);
        model.addAttribute("diskInfos", diskInfos);
        model.addAttribute("diskSpace", diskSpace);
        model.addAttribute("path", path);

        return "disk/group";
    }

    /**
     * 文档库.
     */
    @RequestMapping("repo")
    public String repo(
            @RequestParam(value = "spaceId", required = false) Long spaceId,
            @RequestParam(value = "path", required = false, defaultValue = "") String path,
            Model model) {
        // 如果spaceId存在，就显示对应空间下的文档库
        // 如果spaceId不存在，就显示可以查看的文档库
        if (spaceId == null) {
            String userId = currentUserHolder.getUserId();
            List<DiskSpace> diskSpaces = this.diskSpaceService
                    .findRepoSpaces(userId);
            model.addAttribute("diskSpaces", diskSpaces);

            return "disk/repo-space";
        }

        DiskSpace diskSpace = this.diskSpaceManager.get(spaceId);

        List<DiskInfo> diskInfos = diskService.listFiles(diskSpace, path);
        model.addAttribute("diskInfos", diskInfos);
        model.addAttribute("diskSpace", diskSpace);
        model.addAttribute("path", path);

        return "disk/repo";
    }

    /**
     * 回收站.
     */
    @RequestMapping("trash")
    public String trash(Page page, Model model) {
        String userId = currentUserHolder.getUserId();

        // DiskSpace diskSpace = this.diskSpaceService.findUserSpace(userId);

        // List<DiskInfo> diskInfos = diskInfoManager
        // .find("from DiskInfo where diskSpace=? and status='trash'",
        // diskSpace);
        // model.addAttribute("diskInfos", diskInfos);
        // model.addAttribute("diskSpace", diskSpace);
        String hql = "from DiskInfo where status='trash' and deleteStatus='root' and lastModifier=? order by lastModifiedTime desc";
        page = diskInfoManager.pagedQuery(hql, page.getPageNo(),
                page.getPageSize(), userId);
        model.addAttribute("page", page);

        return "disk/trash";
    }

    /**
     * 设置共享.
     */
    @RequestMapping("create-share")
    public String createShare(@RequestParam("infoId") Long infoId,
            @RequestParam("username") String username,
            @RequestParam("mask") int mask) {
        String currentUserId = currentUserHolder.getUserId();

        // 删除 新建 修改 复制 下载 预览 显示
        // 1 1 1 1 1 1 1
        // 默认 二进制 1111 = 十进制 15
        UserDTO userDto = userClient.findByUsername(username, "1");
        this.diskService.findShare(infoId, currentUserId, userDto.getId());

        return "redirect:/disk/index.do?path=";
    }

    /**
     * 创建群组文档.
     */
    @RequestMapping("create-group")
    public String createGroup(@RequestParam("name") String name) {
        String userId = currentUserHolder.getUserId();
        this.diskSpaceService.createGroupSpace(userId, name);

        return "redirect:/disk/group.do";
    }

    /**
     * 创建文档库.
     */
    @RequestMapping("create-repo")
    public String createRepo(@RequestParam("name") String name) {
        String userId = currentUserHolder.getUserId();
        this.diskSpaceService.createRepoSpace(userId, name);

        return "redirect:/disk/repo.do";
    }

    /**
     * 共享，内部共享.
     */
    @RequestMapping("s/internal")
    public String shareInternal(Model model) {
        String userId = currentUserHolder.getUserId();
        String hql = "from DiskShare where catalog='internal' and creator=?";
        List<DiskShare> diskShares = diskShareManager.find(hql, userId);

        model.addAttribute("diskShares", diskShares);

        return "disk/s/internal";
    }

    /**
     * 共享，外链共享.
     */
    @RequestMapping("s/external")
    public String shareExternal(Model model) {
        String userId = currentUserHolder.getUserId();
        String hql = "from DiskShare where catalog='external' and creator=?";
        List<DiskShare> diskShares = diskShareManager.find(hql, userId);

        model.addAttribute("diskShares", diskShares);

        return "disk/s/external";
    }

    /**
     * 共享，公开共享.
     */
    @RequestMapping("s/public")
    public String sharePublic(Model model) {
        String userId = currentUserHolder.getUserId();
        String hql = "from DiskShare where catalog='public' and creator=?";
        List<DiskShare> diskShares = diskShareManager.find(hql, userId);

        model.addAttribute("diskShares", diskShares);

        return "disk/s/public";
    }

    /**
     * 共享，屏蔽共享.
     */
    @RequestMapping("s/shield")
    public String shareShield(Model model) {
        String userId = currentUserHolder.getUserId();
        String hql = "select diskShare from DiskShare diskShare join diskShare.diskMembers diskMember"
                + " where diskMember.status='shield' and diskMember.userId=?";
        List<DiskShare> diskShares = diskShareManager.find(hql, userId);

        model.addAttribute("diskShares", diskShares);

        return "disk/s/public";
    }

    // ~

    /**
     * 首页.
     */

    /*
     * @RequestMapping("disk-home") public String home(
     * 
     * @RequestParam(value = "username", required = false) String username, Model model) { if (username == null) { Page
     * page = diskInfoManager.pagedQuery("from DiskInfo", 1, 10); List<DiskInfo> diskInfos = (List<DiskInfo>)
     * page.getResult(); List<String> userIds = new ArrayList<String>(); List<UserDTO> userDtos = new
     * ArrayList<UserDTO>();
     * 
     * for (DiskInfo diskInfo : diskInfos) { String userId = diskInfo.getCreator();
     * 
     * if (userIds.contains(userId)) { continue; }
     * 
     * UserDTO userDto = userClient.findById(userId, "1"); userDtos.add(userDto); }
     * 
     * model.addAttribute("userDtos", userDtos); } else { Map<String, Object> parameters = new HashMap<String,
     * Object>(); parameters.put("filter_LIKES_username", username);
     * 
     * Page page = userConnector.pagedQuery(tenantHolder.getTenantId(), new Page(), parameters);
     * 
     * if (page != null) { model.addAttribute("userDtos", page.getResult()); } }
     * 
     * return "disk/disk-home"; }
     */

    /**
     * 列表.
     */
    @RequestMapping("disk-list")
    public String list(@RequestParam("u") String u,
            @RequestParam(value = "path", required = false) String path,
            Model model) {
        if (path == null) {
            path = "";
        }

        String userId = u;

        List<DiskShare> diskShares = diskShareManager.findBy("creator", userId);
        model.addAttribute("diskShares", diskShares);
        model.addAttribute("path", path);

        return "disk/disk-list";
    }

    /**
     * 详情.
     */
    @RequestMapping("disk-view")
    public String view(
            @RequestParam("id") Long id,
            @CookieValue(value = "share", required = false) String sharePassword,
            Model model) {
        DiskShare diskShare = diskShareManager.get(id);

        if ("private".equals(diskShare.getShareType())) {
            if (!diskShare.getSharePassword().equals(sharePassword)) {
                return "disk/disk-code";
            }
        }

        model.addAttribute("diskShare", diskShare);

        return "disk/disk-view";
    }

    /**
     * 私密分享输入验证码.
     */
    @RequestMapping("disk-code")
    public String diskCode(@RequestParam("id") Long id,
            @RequestParam("code") String code, HttpServletResponse response) {
        response.addCookie(new Cookie("share", code));

        return "redirect:/disk/disk-view.do?id=" + id;
    }

    /**
     * 权限列表.
     */
    @RequestMapping("acl-list")
    public String aclList(@RequestParam("id") Long id, Model model) {
        DiskInfo diskInfo = diskInfoManager.get(id);
        model.addAttribute("diskInfo", diskInfo);

        return "disk/acl-list";
    }

    // ~

    /**
     * 以空间为入口，打开folder.
     */
    @RequestMapping("space/open")
    public String spaceOpen(@RequestParam("spaceId") Long spaceId) {
        DiskInfo diskInfo = diskInfoManager
                .findUnique(
                        "from DiskInfo where diskSpace.id=? and diskInfo=null",
                        spaceId);

        return "redirect:/disk/folder/" + diskInfo.getId();
    }

    /**
     * 从回收站恢复.
     */
    @RequestMapping("recover")
    public String recover(@RequestParam("id") Long id,
            @RequestParam(value = "parentId", required = false) Long parentId) {
        String userId = currentUserHolder.getUserId();
        this.diskInfoService.recover(id, userId, parentId);

        return "redirect:/disk/trash.do";
    }

    /**
     * 确认删除.
     */
    @RequestMapping("remove-confirm")
    public String removeConfirm(@RequestParam("id") Long id) {
        String userId = currentUserHolder.getUserId();
        this.diskInfoService.delete(id, userId);

        return "redirect:/disk/trash.do";
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
    public void setUserClient(UserClient userClient) {
        this.userClient = userClient;
    }

    @Resource
    public void setDiskSpaceManager(DiskSpaceManager diskSpaceManager) {
        this.diskSpaceManager = diskSpaceManager;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setDiskService(DiskService diskService) {
        this.diskService = diskService;
    }

    @Resource
    public void setDiskSpaceService(DiskSpaceService diskSpaceService) {
        this.diskSpaceService = diskSpaceService;
    }

    @Resource
    public void setDiskInfoService(DiskInfoService diskInfoService) {
        this.diskInfoService = diskInfoService;
    }
}
