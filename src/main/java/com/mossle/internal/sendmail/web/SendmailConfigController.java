package com.mossle.internal.sendmail.web;

import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mail.MailDTO;
import com.mossle.core.mail.MailHelper;
import com.mossle.core.mail.MailServerInfo;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.internal.sendmail.persistence.domain.SendmailConfig;
import com.mossle.internal.sendmail.persistence.manager.SendmailConfigManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/sendmail")
public class SendmailConfigController {
    private SendmailConfigManager sendmailConfigManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private TenantHolder tenantHolder;

    @RequestMapping("sendmail-config-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = sendmailConfigManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "sendmail/sendmail-config-list";
    }

    @RequestMapping("sendmail-config-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            SendmailConfig sendmailConfig = sendmailConfigManager.get(id);
            model.addAttribute("model", sendmailConfig);
        }

        return "sendmail/sendmail-config-input";
    }

    @RequestMapping("sendmail-config-save")
    public String save(@ModelAttribute SendmailConfig sendmailConfig,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        Long id = sendmailConfig.getId();
        SendmailConfig dest = null;

        if (id != null) {
            dest = sendmailConfigManager.get(id);
            beanMapper.copy(sendmailConfig, dest);
        } else {
            dest = sendmailConfig;
            dest.setTenantId(tenantId);
        }

        sendmailConfigManager.save(dest);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/sendmail/sendmail-config-list.do";
    }

    @RequestMapping("sendmail-config-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<SendmailConfig> sendmailConfigs = sendmailConfigManager
                .findByIds(selectedItem);
        sendmailConfigManager.removeAll(sendmailConfigs);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/sendmail/sendmail-config-list.do";
    }

    @RequestMapping("sendmail-config-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = sendmailConfigManager.pagedQuery(page, propertyFilters);

        List<SendmailConfig> sendmailConfigs = (List<SendmailConfig>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("sendmail config");
        tableModel.addHeaders("id", "name");
        tableModel.setData(sendmailConfigs);
        exportor.export(request, response, tableModel);
    }

    @RequestMapping("sendmail-config-test")
    public String test(@RequestParam("id") Long id, Model model) {
        SendmailConfig sendmailConfig = sendmailConfigManager.get(id);
        MailServerInfo mailServerInfo = new MailServerInfo();
        mailServerInfo.setHost(sendmailConfig.getHost());
        mailServerInfo.setPort(sendmailConfig.getPort());
        mailServerInfo.setSmtpAuth(sendmailConfig.getSmtpAuth() == 1);
        mailServerInfo.setSmtpStarttls(sendmailConfig.getSmtpStarttls() == 1);
        mailServerInfo.setSmtpSsl(sendmailConfig.getSmtpSsl() == 1);
        mailServerInfo.setUsername(sendmailConfig.getUsername());
        mailServerInfo.setPassword(sendmailConfig.getPassword());
        mailServerInfo.setDefaultFrom(sendmailConfig.getDefaultFrom());

        MailDTO mailDto = new MailDTO();
        mailDto.setTo(sendmailConfig.getUsername());
        mailDto.setSubject("test");
        mailDto.setContent("test");

        MailDTO resultMailDto = new MailHelper().send(mailDto, mailServerInfo);
        model.addAttribute("mailDto", mailDto);

        if (!resultMailDto.isSuccess()) {
            StringWriter writer = new StringWriter();
            resultMailDto.getException().printStackTrace(
                    new PrintWriter(writer));
            model.addAttribute("exception", writer.toString());
        }

        return "sendmail/sendmail-config-test";
    }

    // ~ ======================================================================
    @Resource
    public void setSendmailConfigManager(
            SendmailConfigManager sendmailConfigManager) {
        this.sendmailConfigManager = sendmailConfigManager;
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
}
