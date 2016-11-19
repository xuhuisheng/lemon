package com.mossle.auth.web;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.auth.persistence.domain.Access;
import com.mossle.auth.persistence.manager.AccessManager;
import com.mossle.auth.service.AuthService;

import com.mossle.core.spring.MessageHelper;

import com.mossle.spi.auth.ResourcePublisher;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("auth")
public class AccessBatchController {
    private AccessManager accessManager;
    private AuthService authService;
    private MessageHelper messageHelper;
    private ResourcePublisher resourcePublisher;
    private TenantHolder tenantHolder;

    // ~ ======================================================================
    @RequestMapping("access-batch-list")
    public String list() {
        return "auth/access-batch-list";
    }

    @RequestMapping("access-batch-input")
    public String input(@RequestParam("type") String type, Model model) {
        String hql = "from Access where type=? and tenantId=? order by priority";
        List<Access> accesses = accessManager.find(hql, type,
                tenantHolder.getTenantId());
        StringBuilder buff = new StringBuilder();

        for (Access access : accesses) {
            String value = access.getValue();
            String permStr = "";

            if (access.getPerm() != null) {
                permStr = access.getPerm().getCode();
            }

            buff.append(value).append(",").append(permStr).append("\n");
        }

        String text = buff.toString();
        model.addAttribute("text", text);
        model.addAttribute("type", type);

        return "auth/access-batch-input";
    }

    @RequestMapping("access-batch-save")
    public String save(@RequestParam("text") String text,
            @RequestParam("type") String type,
            RedirectAttributes redirectAttributes) {
        authService.batchSaveAccess(text, type, tenantHolder.getTenantId());
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        resourcePublisher.publish();

        return "redirect:/auth/access-list.do";
    }

    // ~ ======================================================================
    @Resource
    public void setAccessManager(AccessManager accessManager) {
        this.accessManager = accessManager;
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
    public void setResourcePublisher(ResourcePublisher resourcePublisher) {
        this.resourcePublisher = resourcePublisher;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
