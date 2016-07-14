package com.mossle.auth.web;

import javax.annotation.Resource;

import com.mossle.auth.service.AuthService;

import com.mossle.spi.auth.ResourcePublisher;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
