package com.mossle.auth.web;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.tenant.TenantConnector;
import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserConnector;
import com.mossle.api.user.UserDTO;

import com.mossle.auth.component.UserStatusChecker;
import com.mossle.auth.component.UserStatusConverter;
import com.mossle.auth.persistence.domain.Role;
import com.mossle.auth.persistence.domain.UserStatus;
import com.mossle.auth.persistence.manager.RoleManager;
import com.mossle.auth.persistence.manager.UserStatusManager;
import com.mossle.auth.service.AuthService;
import com.mossle.auth.support.CheckUserStatusException;
import com.mossle.auth.support.RoleDTO;

import com.mossle.core.spring.MessageHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("auth")
public class UserConnectorBatchController {
    private static Logger logger = LoggerFactory
            .getLogger(UserConnectorBatchController.class);
    private UserStatusManager userStatusManager;
    private MessageHelper messageHelper;
    private UserStatusConverter userStatusConverter;
    private TenantConnector tenantConnector;
    private UserStatusChecker userStatusChecker;
    private UserConnector userConnector;
    private RoleManager roleManager;
    private AuthService authService;
    private TenantHolder tenantHolder;

    @RequestMapping("user-connector-batch-list")
    public String list() {
        return "auth/user-connector-batch-list";
    }

    @RequestMapping("user-connector-batch-input")
    public String input(
            @RequestParam(value = "userText", required = false) String userText,
            Model model, RedirectAttributes redirectAttributes) {
        if (userText != null) {
            List<UserStatus> userStatuses = new ArrayList<UserStatus>();

            for (String str : userText.split("\n")) {
                str = str.trim();

                if (str.length() == 0) {
                    continue;
                }

                String username = str;
                UserDTO userDto = userConnector.findByUsername(username,
                        tenantHolder.getUserRepoRef());

                if (userDto == null) {
                    messageHelper.addMessage(model, str + " is not exists.");
                    logger.info("{} is not exists", str);
                } else {
                    UserStatus userStatus = authService.createOrGetUserStatus(
                            username, userDto.getId(),
                            tenantHolder.getUserRepoRef(),
                            tenantHolder.getTenantId());

                    try {
                        userStatusChecker.check(userStatus);
                        userStatuses.add(userStatus);
                    } catch (CheckUserStatusException ex) {
                        logger.warn(ex.getMessage(), ex);
                        messageHelper.addMessage(model, ex.getMessage());
                    }
                }
            }

            model.addAttribute("userStatuses", userStatuses);
        }

        List<Role> roles = roleManager.find("from Role where tenantId=?",
                tenantHolder.getTenantId());
        List<RoleDTO> roleDtos = new ArrayList<RoleDTO>();
        roleDtos.addAll(convertRoleDtos(roles, false));
        model.addAttribute("roleDtos", roleDtos);

        // List<TenantInfo> sharedTenantInfos = tenantConnector.findSharedTenants();

        // logger.info("{}", sharedTenantInfos);

        // for (TenantInfo tenantInfo : sharedTenantInfos) {
        // List<Role> sharedRoles = authService.findRoles(tenantInfo.getId());
        // roleDtos.addAll(convertRoleDtos(sharedRoles, true));
        // /}
        return "auth/user-connector-batch-input";
    }

    @RequestMapping("user-connector-batch-save")
    public String save(@RequestParam("userIds") List<Long> userIds,
            @RequestParam("roleIds") List<Long> roleIds) {
        logger.debug("userIds: {}, roleIds: {}", userIds, roleIds);

        for (Long userId : userIds) {
            authService.configUserRole(userId, roleIds,
                    tenantHolder.getUserRepoRef(), tenantHolder.getTenantId(),
                    false);
        }

        return "redirect:/auth/user-connector-list.do";
    }

    public List<RoleDTO> convertRoleDtos(List<Role> roles, boolean useTenant) {
        List<RoleDTO> roleDtos = new ArrayList<RoleDTO>();

        for (Role role : roles) {
            roleDtos.add(convertRoleDto(role, useTenant));
        }

        return roleDtos;
    }

    public RoleDTO convertRoleDto(Role role, boolean useTenant) {
        RoleDTO roleDto = new RoleDTO();
        roleDto.setId(role.getId());

        if (useTenant) {
            roleDto.setName(role.getName() + "("
                    + tenantConnector.findById(role.getTenantId()).getName()
                    + ")");
        } else {
            roleDto.setName(role.getName());
        }

        roleDto.setTenantId(role.getTenantId());

        return roleDto;
    }

    // ~ ======================================================================
    @Resource
    public void setUserStatusManager(UserStatusManager userStatusManager) {
        this.userStatusManager = userStatusManager;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setUserStatusConverter(UserStatusConverter userStatusConverter) {
        this.userStatusConverter = userStatusConverter;
    }

    @Resource
    public void setRoleManager(RoleManager roleManager) {
        this.roleManager = roleManager;
    }

    @Resource
    public void setUserStatusChecker(UserStatusChecker userStatusChecker) {
        this.userStatusChecker = userStatusChecker;
    }

    @Resource
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }

    @Resource
    public void setTenantConnector(TenantConnector tenantConnector) {
        this.tenantConnector = tenantConnector;
    }

    @Resource
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
