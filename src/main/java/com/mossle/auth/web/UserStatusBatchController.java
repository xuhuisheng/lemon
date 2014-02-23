package com.mossle.auth.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.api.scope.ScopeConnector;
import com.mossle.api.scope.ScopeHolder;

import com.mossle.auth.component.UserStatusChecker;
import com.mossle.auth.domain.Role;
import com.mossle.auth.domain.UserStatus;
import com.mossle.auth.manager.RoleManager;
import com.mossle.auth.manager.UserStatusManager;
import com.mossle.auth.service.AuthService;
import com.mossle.auth.support.CheckUserStatusException;

import com.mossle.core.spring.MessageHelper;

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
@RequestMapping("auth")
public class UserStatusBatchController {
    private static Logger logger = LoggerFactory
            .getLogger(UserStatusBatchController.class);
    private UserStatusManager userStatusManager;
    private RoleManager roleManager;
    private ScopeConnector scopeConnector;
    private UserStatusChecker userStatusChecker;
    private AuthService authService;
    private MessageHelper messageHelper;

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
                        str, ScopeHolder.getUserRepoRef());

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

        List<Role> roles = roleManager.find("from Role where scopeId=?",
                ScopeHolder.getScopeId());

        model.addAttribute("roles", roles);

        return "auth/user-status-batch-input";
    }

    @RequestMapping("user-statuc-batch-save")
    public String save(@RequestParam("userIds") List<Long> userIds,
            @RequestParam("roleIds") List<Long> roleIds) {
        logger.debug("userIds: {}, roleIds: {}", userIds, roleIds);

        for (Long userId : userIds) {
            authService.configUserRole(userId, roleIds,
                    ScopeHolder.getUserRepoRef(), ScopeHolder.getScopeId(),
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
    public void setScopeConnector(ScopeConnector scopeConnector) {
        this.scopeConnector = scopeConnector;
    }

    @Resource
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }
}
