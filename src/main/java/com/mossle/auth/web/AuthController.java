package com.mossle.auth.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.api.tenant.TenantConnector;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.auth.component.UserStatusChecker;
import com.mossle.auth.persistence.domain.Role;
import com.mossle.auth.persistence.manager.RoleManager;
import com.mossle.auth.service.AuthService;
import com.mossle.auth.support.CheckUserStatusException;

import com.mossle.spi.auth.ResourcePublisher;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("auth")
public class AuthController {
    private AuthService authService;
    private ResourcePublisher resourcePublisher;

    @RequestMapping("auth-list")
    public String list(Model model) throws Exception {
        String text = authService.doExport();
        model.addAttribute("text", text);

        return "auth/auth-list";
    }

    @RequestMapping("auth-save")
    public String save(@RequestParam("text") String text) {
        authService.doImport(text);
        resourcePublisher.publish();

        return "redirect:/auth/auth-list.do";
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
}
