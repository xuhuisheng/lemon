package com.mossle.internal.sendmail.web;

import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.store.StoreConnector;
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
import com.mossle.core.store.DataSourceInputStreamSource;

import com.mossle.internal.sendmail.persistence.domain.SendmailAttachment;
import com.mossle.internal.sendmail.persistence.domain.SendmailConfig;
import com.mossle.internal.sendmail.persistence.domain.SendmailTemplate;
import com.mossle.internal.sendmail.persistence.manager.SendmailAttachmentManager;
import com.mossle.internal.sendmail.persistence.manager.SendmailConfigManager;
import com.mossle.internal.sendmail.persistence.manager.SendmailTemplateManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("sendmail")
public class SendmailTemplateController {
    private SendmailTemplateManager sendmailTemplateManager;
    private SendmailConfigManager sendmailConfigManager;
    private SendmailAttachmentManager sendmailAttachmentManager;
    private StoreConnector storeConnector;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private TenantHolder tenantHolder;

    @RequestMapping("sendmail-template-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = sendmailTemplateManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "sendmail/sendmail-template-list";
    }

    @RequestMapping("sendmail-template-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            SendmailTemplate sendmailTemplate = sendmailTemplateManager.get(id);
            model.addAttribute("model", sendmailTemplate);
        }

        return "sendmail/sendmail-template-input";
    }

    @RequestMapping("sendmail-template-save")
    public String save(
            @ModelAttribute SendmailTemplate sendmailTemplate,
            @RequestParam(value = "attachmentIds", required = false) List<Long> attachmentIds,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        Long id = sendmailTemplate.getId();
        SendmailTemplate dest = null;

        if (id != null) {
            dest = sendmailTemplateManager.get(id);
            beanMapper.copy(sendmailTemplate, dest);
        } else {
            dest = sendmailTemplate;
            dest.setTenantId(tenantId);
        }

        sendmailTemplateManager.save(dest);

        if (attachmentIds != null) {
            for (Long attachmentId : attachmentIds) {
                SendmailAttachment sendmailAttachment = sendmailAttachmentManager
                        .get(attachmentId);
                sendmailAttachment.setSendmailTemplate(dest);
                sendmailAttachmentManager.save(sendmailAttachment);
            }
        }

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/sendmail/sendmail-template-list.do";
    }

    @RequestMapping("sendmail-template-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<SendmailTemplate> sendmailTemplates = sendmailTemplateManager
                .findByIds(selectedItem);
        sendmailTemplateManager.removeAll(sendmailTemplates);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/sendmail/sendmail-template-list.do";
    }

    @RequestMapping("sendmail-template-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = sendmailTemplateManager.pagedQuery(page, propertyFilters);

        List<SendmailTemplate> sendmailTemplates = (List<SendmailTemplate>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("sendmail config");
        tableModel.addHeaders("id", "name");
        tableModel.setData(sendmailTemplates);
        exportor.export(request, response, tableModel);
    }

    @RequestMapping("sendmail-template-test")
    public String test(@RequestParam("id") Long id, Model model) {
        String tenantId = tenantHolder.getTenantId();
        model.addAttribute("sendmailConfigs",
                sendmailConfigManager.findBy("tenantId", tenantId));
        model.addAttribute("sendmailTemplate", sendmailTemplateManager.get(id));

        return "sendmail/sendmail-template-test";
    }

    @RequestMapping("sendmail-template-send")
    public String test(@RequestParam("id") Long id, Long sendmailConfigId,
            Model model) throws Exception {
        String tenantId = tenantHolder.getTenantId();
        SendmailTemplate sendmailTemplate = sendmailTemplateManager.get(id);
        SendmailConfig sendmailConfig = sendmailConfigManager
                .get(sendmailConfigId);

        MailServerInfo mailServerInfo = new MailServerInfo();
        mailServerInfo.setHost(sendmailConfig.getHost());
        mailServerInfo.setSmtpAuth(sendmailConfig.getSmtpAuth() == 1);
        mailServerInfo.setSmtpStarttls(sendmailConfig.getSmtpStarttls() == 1);
        mailServerInfo.setUsername(sendmailConfig.getUsername());
        mailServerInfo.setPassword(sendmailConfig.getPassword());
        mailServerInfo.setDefaultFrom(sendmailConfig.getDefaultFrom());

        MailDTO mailDto = new MailDTO();
        mailDto.setFrom(sendmailTemplate.getSender());
        mailDto.setTo(sendmailTemplate.getReceiver());
        mailDto.setCc(sendmailTemplate.getCc());
        mailDto.setBcc(sendmailTemplate.getBcc());
        mailDto.setSubject(sendmailTemplate.getSubject());
        mailDto.setContent(sendmailTemplate.getContent());

        for (SendmailAttachment sendmailAttachment : sendmailTemplate
                .getSendmailAttachments()) {
            DataSourceInputStreamSource resource = new DataSourceInputStreamSource(
                    storeConnector.getStore("sendmailattachment",
                            sendmailAttachment.getPath(), tenantId)
                            .getDataSource());
            mailDto.addAttachment(sendmailAttachment.getName(), resource);
        }

        MailDTO resultMailDto = new MailHelper().send(mailDto, mailServerInfo);
        model.addAttribute("mailDto", mailDto);

        if (!resultMailDto.isSuccess()) {
            StringWriter writer = new StringWriter();
            resultMailDto.getException().printStackTrace(
                    new PrintWriter(writer));
            model.addAttribute("exception", writer.toString());
        }

        return "sendmail/sendmail-template-send";
    }

    // ~ ======================================================================
    @Resource
    public void setSendmailTemplateManager(
            SendmailTemplateManager sendmailTemplateManager) {
        this.sendmailTemplateManager = sendmailTemplateManager;
    }

    @Resource
    public void setSendmailConfigManager(
            SendmailConfigManager sendmailConfigManager) {
        this.sendmailConfigManager = sendmailConfigManager;
    }

    @Resource
    public void setSendmailAttachmentManager(
            SendmailAttachmentManager sendmailAttachmentManager) {
        this.sendmailAttachmentManager = sendmailAttachmentManager;
    }

    @Resource
    public void setStoreConnector(StoreConnector storeConnector) {
        this.storeConnector = storeConnector;
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
