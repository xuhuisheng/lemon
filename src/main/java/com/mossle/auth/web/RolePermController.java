package com.mossle.auth.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.api.scope.ScopeHolder;

import com.mossle.auth.component.RoleDefChecker;
import com.mossle.auth.domain.Perm;
import com.mossle.auth.domain.PermType;
import com.mossle.auth.domain.RoleDef;
import com.mossle.auth.manager.PermManager;
import com.mossle.auth.manager.PermTypeManager;
import com.mossle.auth.manager.RoleDefManager;
import com.mossle.auth.support.CheckRoleException;

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
public class RolePermController {
    private static Logger logger = LoggerFactory
            .getLogger(RolePermController.class);
    private PermManager permManager;
    private RoleDefManager roleDefManager;
    private PermTypeManager permTypeManager;
    private MessageHelper messageHelper;
    private RoleDefChecker roleDefChecker;

    @RequestMapping("role-perm-save")
    public String save(
            @RequestParam("id") Long id,
            Model model,
            @RequestParam(value = "selectedItem", required = false) List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        if (selectedItem == null) {
            selectedItem = Collections.emptyList();
        }

        try {
            RoleDef roleDef = roleDefManager.get(id);
            roleDefChecker.check(roleDef);
            roleDef.getPerms().clear();

            for (Long permId : selectedItem) {
                Perm perm = permManager.get(permId);
                roleDef.getPerms().add(perm);
            }

            roleDefManager.save(roleDef);
            messageHelper.addFlashMessage(redirectAttributes,
                    "core.success.save", "保存成功");
        } catch (CheckRoleException ex) {
            logger.warn(ex.getMessage(), ex);
            messageHelper.addFlashMessage(redirectAttributes, ex.getMessage());

            return input(id, model);
        }

        return "redirect:/auth/role-perm-input.do?id=" + id;
    }

    @RequestMapping("role-perm-input")
    public String input(@RequestParam("id") Long id, Model model) {
        RoleDef roleDef = roleDefManager.get(id);
        List<Long> selectedItem = new ArrayList<Long>();

        for (Perm perm : roleDef.getPerms()) {
            selectedItem.add(perm.getId());
        }

        String hql = "from PermType where type=0 and scopeId=?";
        List<PermType> permTypes = permTypeManager.find(hql,
                ScopeHolder.getScopeId());
        model.addAttribute("permTypes", permTypes);
        model.addAttribute("selectedItem", selectedItem);
        model.addAttribute("id", id);

        return "auth/role-perm-input";
    }

    // ~ ======================================================================
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
    public void setPermTypeManager(PermTypeManager permTypeManager) {
        this.permTypeManager = permTypeManager;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }
}
