package com.mossle.internal.sendmail.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.internal.sendmail.persistence.domain.SendmailApp;
import com.mossle.internal.sendmail.persistence.manager.SendmailAppManager;
import com.mossle.internal.sendmail.service.SendmailService;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/sendmail")
public class SendmailAppController {
    private SendmailAppManager sendmailAppManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private TenantHolder tenantHolder;
    private SendmailService sendmailService;

    @RequestMapping("sendmail-app-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        // propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = sendmailAppManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "sendmail/sendmail-app-list";
    }

    @RequestMapping("sendmail-app-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            SendmailApp sendmailApp = sendmailAppManager.get(id);
            model.addAttribute("model", sendmailApp);
        }

        return "sendmail/sendmail-app-input";
    }

    @RequestMapping("sendmail-app-save")
    public String save(@ModelAttribute SendmailApp sendmailApp,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        Long id = sendmailApp.getId();
        SendmailApp dest = null;

        if (id != null) {
            dest = sendmailAppManager.get(id);
            beanMapper.copy(sendmailApp, dest);
        } else {
            dest = sendmailApp;

            // dest.setTenantId(tenantId);
        }

        sendmailAppManager.save(dest);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/sendmail/sendmail-app-list.do";
    }

    @RequestMapping("sendmail-app-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<SendmailApp> sendmailApps = sendmailAppManager
                .findByIds(selectedItem);
        sendmailAppManager.removeAll(sendmailApps);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/sendmail/sendmail-app-list.do";
    }

    @RequestMapping("sendmail-app-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = sendmailAppManager.pagedQuery(page, propertyFilters);

        List<SendmailApp> sendmailApps = (List<SendmailApp>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("sendmail config");
        tableModel.addHeaders("id", "name");
        tableModel.setData(sendmailApps);
        exportor.export(request, response, tableModel);
    }

    @RequestMapping("sendmail-app-test")
    public String test(@RequestParam("id") Long id) {
        String to = "bot@mossle.com";
        String templateCode = "test";
        Map<String, Object> parameter = new HashMap<String, Object>();
        String appId = "lemon";
        sendmailService.send(to, templateCode, parameter, appId);

        return "sendmail/sendmail-app-list";
    }

    // ~ ======================================================================
    @Resource
    public void setSendmailAppManager(SendmailAppManager sendmailAppManager) {
        this.sendmailAppManager = sendmailAppManager;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }

    @Resource
    public void setSendmailService(SendmailService sendmailService) {
        this.sendmailService = sendmailService;
    }
}
