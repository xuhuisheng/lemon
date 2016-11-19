package com.mossle.auth.web;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.tenant.TenantConnector;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.auth.component.UserStatusChecker;
import com.mossle.auth.persistence.domain.Role;
import com.mossle.auth.persistence.domain.UserStatus;
import com.mossle.auth.persistence.manager.RoleManager;
import com.mossle.auth.persistence.manager.UserStatusManager;
import com.mossle.auth.service.AuthService;
import com.mossle.auth.support.CheckUserStatusException;

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
public class UserStatusBatchController {
    private static Logger logger = LoggerFactory
            .getLogger(UserStatusBatchController.class);
    private UserStatusManager userStatusManager;
    private RoleManager roleManager;
    private TenantConnector tenantConnector;
    private UserStatusChecker userStatusChecker;
    private AuthService authService;
    private MessageHelper messageHelper;
    private TenantHolder tenantHolder;

    @RequestMapping("user-status-batch-input")
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

                UserStatus userStatus = userStatusManager.findUnique(
                        "from UserStatus where username=? and userRepoRef=?",
                        str, tenantHolder.getUserRepoRef());

                if (userStatus == null) {
                    messageHelper.addFlashMessage(redirectAttributes, str
                            + " is not exists.");
                } else {
                    try {
                        userStatusChecker.check(userStatus);
                        userStatuses.add(userStatus);
                    } catch (CheckUserStatusException ex) {
                        logger.warn(ex.getMessage(), ex);
                        messageHelper.addFlashMessage(redirectAttributes,
                                ex.getMessage());
                    }
                }
            }

            model.addAttribute("userStatuses", userStatuses);
        }

        List<Role> roles = roleManager.find("from Role where tenantId=?",
                tenantHolder.getTenantId());

        model.addAttribute("roles", roles);

        return "auth/user-status-batch-input";
    }

    @RequestMapping("user-statuc-batch-save")
    public String save(@RequestParam("userIds") List<Long> userIds,
            @RequestParam("roleIds") List<Long> roleIds) {
        logger.debug("userIds: {}, roleIds: {}", userIds, roleIds);

        for (Long userId : userIds) {
            authService.configUserRole(userId, roleIds,
                    tenantHolder.getUserRepoRef(), tenantHolder.getTenantId(),
                    false);
        }

        return "redirect:/auth/user-status-batch-input.do";
    }

    // ~ ======================================================================
    @Resource
    public void setUserStatusManager(UserStatusManager userStatusManager) {
        this.userStatusManager = userStatusManager;
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
    public void setTenantConnector(TenantConnector tenantConnector) {
        this.tenantConnector = tenantConnector;
    }

    @Resource
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
