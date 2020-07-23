package com.mossle.auth.web.sys;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserDTO;

import com.mossle.auth.component.AuthCache;
import com.mossle.auth.component.RoleDefChecker;
import com.mossle.auth.component.UserStatusConverter;
import com.mossle.auth.persistence.domain.Perm;
import com.mossle.auth.persistence.domain.Role;
import com.mossle.auth.persistence.domain.RoleDef;
import com.mossle.auth.persistence.domain.UserStatus;
import com.mossle.auth.persistence.manager.PermManager;
import com.mossle.auth.persistence.manager.RoleDefManager;
import com.mossle.auth.persistence.manager.RoleManager;
import com.mossle.auth.persistence.manager.UserStatusManager;
import com.mossle.auth.service.AuthService;
import com.mossle.auth.support.CheckRoleException;
import com.mossle.auth.support.CheckUserStatusException;
import com.mossle.auth.support.UserStatusDTO;

import com.mossle.client.open.OpenAppDTO;
import com.mossle.client.open.OpenClient;
import com.mossle.client.open.SysDTO;
import com.mossle.client.user.UserClient;

import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;

import com.mossle.spi.auth.ResourcePublisher;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("auth/sys")
public class AuthSysController {
    private static Logger logger = LoggerFactory
            .getLogger(AuthSysController.class);
    private UserStatusManager userStatusManager;
    private RoleManager roleManager;
    private PermManager permManager;
    private RoleDefManager roleDefManager;
    private AuthService authService;
    private ResourcePublisher resourcePublisher;
    private OpenClient openClient;
    private CurrentUserHolder currentUserHolder;
    private UserClient userClient;
    private UserStatusConverter userStatusConverter;
    private TenantHolder tenantHolder;
    private MessageHelper messageHelper;
    private BeanMapper beanMapper = new BeanMapper();
    private RoleDefChecker roleDefChecker;
    private AuthCache authCache;

    @RequestMapping("")
    public String index(Model model) throws Exception {
        String userId = currentUserHolder.getUserId();
        List<OpenAppDTO> openAppDtos = openClient.findUserApps(userId);

        if (openAppDtos.isEmpty()) {
            return "auth/sys/index";
        }

        OpenAppDTO defaultOpenAppDto = openAppDtos.get(0);

        // model.addAttribute("defaultOpenAppDto", defaultOpenAppDto);
        // model.addAttribute("openAppDtos", openAppDtos);
        return "redirect:/auth/sys/" + defaultOpenAppDto.getCode() + "/user.do";
    }

    @RequestMapping("{sysCode}/index")
    public String sysIndex(@PathVariable("sysCode") String sysCode, Model model)
            throws Exception {
        return "redirect:/auth/sys/" + sysCode + "/user.do";
    }

    @RequestMapping("{sysCode}/user")
    public String user(
            Page page,
            @RequestParam(value = "username", required = false) String username,
            @PathVariable("sysCode") String sysCode, Model model)
            throws Exception {
        // open sys
        SysDTO sysDto = openClient.findSys(sysCode);
        model.addAttribute("sysCode", sysCode);
        model.addAttribute("sysDto", sysDto);

        // auth user
        String tenantId = sysCode;

        // 缩小显示范围，把所有用户都显示出来也没什么用途
        if (StringUtils.isBlank(username)) {
            // 如果没有查询条件，就只返回配置了权限的用户
            String hql = "from UserStatus where tenantId=?";
            page = userStatusManager.pagedQuery(hql, page.getPageNo(),
                    page.getPageSize(), tenantId);

            List<UserStatus> userStatuses = (List<UserStatus>) page.getResult();
            List<UserStatusDTO> userStatusDtos = new ArrayList<UserStatusDTO>();

            for (UserStatus userStatus : userStatuses) {
                userStatusDtos.add(userStatusConverter.createUserStatusDto(
                        userStatus, tenantHolder.getUserRepoRef(),
                        tenantHolder.getTenantId()));
            }

            page.setResult(userStatusDtos);
            model.addAttribute("page", page);
        } else {
            logger.debug("username : {}", username);

            // page = userConnector.pagedQuery(tenantId, page, parameterMap);

            // List<UserDTO> userDtos = (List<UserDTO>) page.getResult();
            List<UserDTO> userDtos = this.userClient.search(username);
            logger.debug("userDtos : {}", userDtos);

            List<UserStatusDTO> userStatusDtos = new ArrayList<UserStatusDTO>();

            for (UserDTO userDto : userDtos) {
                String usernameStr = userDto.getUsername();

                // String hql = "from UserStatus where username=? and userRepoRef=?";
                // UserStatus userStatus = userStatusManager.findUnique(hql,
                // usernameStr, tenantHolder.getUserRepoRef());
                String hql = "from UserStatus where username=? and tenantId=?";
                UserStatus userStatus = userStatusManager.findUnique(hql,
                        usernameStr, tenantId);

                if (userStatus == null) {
                    UserStatusDTO userStatusDto = new UserStatusDTO();
                    userStatusDto.setUsername(usernameStr);
                    userStatusDto.setEnabled(true);
                    userStatusDto.setRef(userDto.getId());
                    userStatusDtos.add(userStatusDto);
                } else {
                    String userRepoRef = sysCode;
                    userStatusDtos.add(userStatusConverter.createUserStatusDto(
                            userStatus, userRepoRef, tenantId));
                }
            }

            page.setResult(userStatusDtos);
            model.addAttribute("page", page);
        }

        return "auth/sys/user";
    }

    @RequestMapping("{sysCode}/user-role-config")
    public String userRoleConfig(@RequestParam("ref") String ref,
            @PathVariable("sysCode") String sysCode) {
        // open sys
        // SysDTO sysDto = openClient.findSys(sysCode);
        // model.addAttribute("sysCode", sysCode);
        // model.addAttribute("sysDto", sysDto);
        String tenantId = sysCode;

        logger.debug("ref : {}", ref);

        UserDTO userDto = userClient.findById(ref, "1");
        Long id = null;

        if (userDto != null) {
            String username = userDto.getUsername();

            UserStatus userStatus = authService.createOrGetUserStatus(username,
                    userDto.getId(), tenantHolder.getUserRepoRef(), tenantId);

            id = userStatus.getId();
        }

        return "redirect:/auth/sys/" + sysCode + "/user-role-input.do?id=" + id;
    }

    @RequestMapping("{sysCode}/user-role-input")
    public String userRoleInput(@RequestParam("id") Long id,
            @PathVariable("sysCode") String sysCode, Model model) {
        // open sys
        SysDTO sysDto = openClient.findSys(sysCode);
        model.addAttribute("sysCode", sysCode);
        model.addAttribute("sysDto", sysDto);

        String tenantId = sysCode;

        // local roles
        List<Role> roles = authService.findRoles(tenantId);
        String hql = "select r.id as id from Role r join r.userStatuses u where u.id=?";
        List<Long> userRoleIds = roleManager.find(hql, id);
        model.addAttribute("id", id);
        model.addAttribute("roles", roles);
        model.addAttribute("userRoleIds", userRoleIds);

        return "auth/sys/user-role-input";
    }

    @RequestMapping("{sysCode}/user-role-save")
    public String userRoleSave(
            @RequestParam("id") Long id,
            @RequestParam(value = "selectedItem", required = false) List<Long> selectedItem,
            @PathVariable("sysCode") String sysCode, Model model,
            RedirectAttributes redirectAttributes) {
        // open sys
        SysDTO sysDto = openClient.findSys(sysCode);
        model.addAttribute("sysCode", sysCode);
        model.addAttribute("sysDto", sysDto);

        String tenantId = sysCode;

        try {
            authService.configUserRole(id, selectedItem,
                    tenantHolder.getUserRepoRef(), tenantId, true);
            messageHelper.addFlashMessage(redirectAttributes,
                    "core.success.save", "保存成功");
        } catch (CheckUserStatusException ex) {
            logger.warn(ex.getMessage(), ex);
            messageHelper.addFlashMessage(redirectAttributes, ex.getMessage());

            return userRoleInput(id, sysCode, model);
        }

        return "redirect:/auth/sys/" + sysCode + "/user-role-input.do?id=" + id;
    }

    @RequestMapping("{sysCode}/role")
    public String role(Page page, @PathVariable("sysCode") String sysCode,
            Model model) throws Exception {
        // open sys
        SysDTO sysDto = openClient.findSys(sysCode);
        model.addAttribute("sysCode", sysCode);
        model.addAttribute("sysDto", sysDto);

        String tenantId = sysCode;

        // auth role
        String hql = "from Role where tenantId=?";
        page = roleManager.pagedQuery(hql, page.getPageNo(),
                page.getPageSize(), tenantId);
        model.addAttribute("page", page);

        return "auth/sys/role";
    }

    @RequestMapping("{sysCode}/role-input")
    public String roleInput(@PathVariable("sysCode") String sysCode, Model model) {
        // open sys
        SysDTO sysDto = openClient.findSys(sysCode);
        model.addAttribute("sysCode", sysCode);
        model.addAttribute("sysDto", sysDto);

        String tenantId = sysCode;

        // auth resc
        return "auth/sys/role-input";
    }

    @RequestMapping("{sysCode}/role-save")
    public String roleSave(@PathVariable("sysCode") String sysCode, Role role,
            RedirectAttributes redirectAttributes) {
        // open sys
        // SysDTO sysDto = openClient.findSys(sysCode);
        // model.addAttribute("sysCode", sysCode);
        // model.addAttribute("sysDto", sysDto);

        String tenantId = sysCode;

        // auth role
        Role dest = null;
        Long id = role.getId();

        if (id != null) {
            dest = roleManager.get(id);
            beanMapper.copy(role, dest);
        } else {
            dest = role;
        }

        if (id == null) {
            dest.setTenantId(tenantId);
        }

        roleManager.save(dest);

        String hql = "from RoleDef where name=? and tenantId=?";
        RoleDef roleDef = roleDefManager.findUnique(hql, dest.getName(),
                sysCode);

        if (roleDef == null) {
            roleDef = new RoleDef();
            roleDef.setName(dest.getName());
            roleDef.setTenantId(sysCode);
            roleDefManager.save(roleDef);
            role.setRoleDef(roleDef);
            roleManager.save(role);
        }

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/auth/sys/" + sysCode + "/role.do";
    }

    @RequestMapping("{sysCode}/role-perm-config")
    public String rolePermConfig(@PathVariable("sysCode") String sysCode,
            @RequestParam("id") Long id, Model model) {
        // open sys
        SysDTO sysDto = openClient.findSys(sysCode);
        model.addAttribute("sysCode", sysCode);
        model.addAttribute("sysDto", sysDto);

        String tenantId = sysCode;

        // auth role
        Role role = roleManager.get(id);
        RoleDef roleDef = role.getRoleDef();
        List<Long> selectedItem = new ArrayList<Long>();

        for (Perm perm : roleDef.getPerms()) {
            selectedItem.add(perm.getId());
        }

        // auth resc
        String hql = "from Perm where perm is null and tenantId=? order by priority";
        List<Perm> perms = permManager.find(hql, tenantId);
        model.addAttribute("perms", perms);

        model.addAttribute("selectedItem", selectedItem);
        model.addAttribute("id", id);

        return "auth/sys/role-perm-config";
    }

    @RequestMapping("{sysCode}/role-perm-save")
    public String rolePermSave(
            @PathVariable("sysCode") String sysCode,
            @RequestParam("id") Long id,
            Model model,
            @RequestParam(value = "selectedItem", required = false) List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        // open sys
        SysDTO sysDto = openClient.findSys(sysCode);
        model.addAttribute("sysCode", sysCode);
        model.addAttribute("sysDto", sysDto);

        String tenantId = sysCode;

        if (selectedItem == null) {
            selectedItem = Collections.emptyList();
        }

        try {
            Role role = roleManager.get(id);
            RoleDef roleDef = role.getRoleDef();
            roleDefChecker.check(roleDef);
            roleDef.getPerms().clear();

            for (Long permId : selectedItem) {
                Perm perm = permManager.get(permId);
                roleDef.getPerms().add(perm);
            }

            roleDefManager.save(roleDef);
            messageHelper.addFlashMessage(redirectAttributes,
                    "core.success.save", "保存成功");

            for (Role roleInstance : roleDef.getRoles()) {
                for (UserStatus userStatus : roleInstance.getUserStatuses()) {
                    authCache.evictUserStatus(userStatus);
                }
            }
        } catch (CheckRoleException ex) {
            logger.warn(ex.getMessage(), ex);
            messageHelper.addFlashMessage(redirectAttributes, ex.getMessage());

            return rolePermConfig(sysCode, id, model);
        }

        return "redirect:/auth/sys/" + sysCode + "/role-perm-config.do?id="
                + id;
    }

    @RequestMapping("{sysCode}/resc")
    public String resc(@PathVariable("sysCode") String sysCode, Model model) {
        // open sys
        SysDTO sysDto = openClient.findSys(sysCode);
        model.addAttribute("sysCode", sysCode);
        model.addAttribute("sysDto", sysDto);

        String tenantId = sysCode;

        // auth resc
        String hql = "from Perm where perm is null and tenantId=? order by priority";
        List<Perm> perms = permManager.find(hql, sysCode);
        model.addAttribute("perms", perms);

        return "auth/sys/resc";
    }

    @RequestMapping("{sysCode}/resc-input")
    public String rescInput(@PathVariable("sysCode") String sysCode,@RequestParam(value="id",required=false) Long id, Model model) {
        // open sys
        SysDTO sysDto = openClient.findSys(sysCode);
        model.addAttribute("sysCode", sysCode);
        model.addAttribute("sysDto", sysDto);

        String tenantId = sysCode;
        if (id != null) {
            model.addAttribute("model", permManager.get(id));
        }

        // auth resc
        return "auth/sys/resc-input";
    }

    @RequestMapping("{sysCode}/resc-save")
    public String rescSave(@PathVariable("sysCode") String sysCode, Perm perm,
            @RequestParam(value = "parentId", required = false) Long parentId,
            RedirectAttributes redirectAttributes) {
        // open sys
        // SysDTO sysDto = openClient.findSys(sysCode);
        // model.addAttribute("sysCode", sysCode);
        // model.addAttribute("sysDto", sysDto);

        String tenantId = sysCode;

        // auth resc
        Perm dest = null;
        Long id = perm.getId();

        if (id != null) {
            dest = permManager.get(id);
            beanMapper.copy(perm, dest);
        } else {
            dest = perm;
        }

        if (id == null) {
            dest.setTenantId(tenantId);
        }

        if (parentId != null) {
            dest.setPerm(this.permManager.get(parentId));
        }

        // dest.setPermType(permTypeManager.get(permTypeId));
        permManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/auth/sys/" + sysCode + "/resc.do";
    }

    @RequestMapping("{sysCode}/resc-remove")
    public String rescRemove(@PathVariable("sysCode") String sysCode,
            @RequestParam("id") Long id,
            RedirectAttributes redirectAttributes) {
        // open sys
        // SysDTO sysDto = openClient.findSys(sysCode);
        // model.addAttribute("sysCode", sysCode);
        // model.addAttribute("sysDto", sysDto);

        String tenantId = sysCode;

        // auth resc
        permManager.removeById(id);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/auth/sys/" + sysCode + "/resc.do";
    }

    // ~ ======================================================================
    @Resource
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    @Resource
    public void setResourcePublisher(ResourcePublisher resourcePublisher) {
        this.resourcePublisher = resourcePublisher;
    }

    @Resource
    public void setOpenClient(OpenClient openClient) {
        this.openClient = openClient;
    }

    @Resource
    public void setUserStatusManager(UserStatusManager userStatusManager) {
        this.userStatusManager = userStatusManager;
    }

    @Resource
    public void setRoleManager(RoleManager roleManager) {
        this.roleManager = roleManager;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setUserStatusConverter(UserStatusConverter userStatusConverter) {
        this.userStatusConverter = userStatusConverter;
    }

    @Resource
    public void setUserClient(UserClient userClient) {
        this.userClient = userClient;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setPermManager(PermManager permManager) {
        this.permManager = permManager;
    }

    @Resource
    public void setRoleDefManager(RoleDefManager roleDefManager) {
        this.roleDefManager = roleDefManager;
    }

    @Resource
    public void setRoleDefChecker(RoleDefChecker roleDefChecker) {
        this.roleDefChecker = roleDefChecker;
    }

    @Resource
    public void setAuthCache(AuthCache authCache) {
        this.authCache = authCache;
    }
}
