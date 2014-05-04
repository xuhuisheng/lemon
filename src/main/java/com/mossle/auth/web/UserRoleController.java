package com.mossle.auth.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.api.scope.ScopeConnector;
import com.mossle.api.scope.ScopeHolder;

import com.mossle.auth.component.UserStatusChecker;
import com.mossle.auth.domain.Role;
import com.mossle.auth.manager.RoleManager;
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
public class UserRoleController {
    private static Logger logger = LoggerFactory
            .getLogger(UserRoleController.class);
    private RoleManager roleManager;
    private MessageHelper messageHelper;
    private AuthService authService;

    @RequestMapping("user-role-save")
    public String save(
            @RequestParam("id") Long id,
            @RequestParam(value = "selectedItem", required = false) List<Long> selectedItem,
            Model model, RedirectAttributes redirectAttributes) {
        try {
            authService.configUserRole(id, selectedItem,
                    ScopeHolder.getUserRepoRef(), ScopeHolder.getScopeId(),
                    true);
            messageHelper.addFlashMessage(redirectAttributes,
                    "core.success.save", "保存成功");
        } catch (CheckUserStatusException ex) {
            logger.warn(ex.getMessage(), ex);
            messageHelper.addFlashMessage(redirectAttributes, ex.getMessage());

            return input(id, model);
        }

        return "redirect:/auth/user-role-input.do?id=" + id;
    }

    @RequestMapping("user-role-input")
    public String input(@RequestParam("id") Long id, Model model) {
        // local roles
        List<Role> roles = authService.findRoles(ScopeHolder.getScopeId());
        String hql = "select r.id as id from Role r join r.userStatuses u where u.id=?";
        List<Long> userRoleIds = roleManager.find(hql, id);
        model.addAttribute("id", id);
        model.addAttribute("roles", roles);
        model.addAttribute("userRoleIds", userRoleIds);

        return "auth/user-role-input";
    }

    // ~ ======================================================================
    @Resource
    public void setRoleManager(RoleManager roleManager) {
        this.roleManager = roleManager;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }
}
