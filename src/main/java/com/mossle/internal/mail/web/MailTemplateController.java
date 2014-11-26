package com.mossle.internal.mail.web;

import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;

import com.mossle.ext.export.Exportor;
import com.mossle.ext.export.TableModel;
import com.mossle.ext.mail.MailDTO;
import com.mossle.ext.mail.MailHelper;
import com.mossle.ext.mail.MailServerInfo;
import com.mossle.ext.store.StoreConnector;
import com.mossle.ext.store.StoreDTO;

import com.mossle.internal.mail.domain.MailAttachment;
import com.mossle.internal.mail.domain.MailConfig;
import com.mossle.internal.mail.domain.MailTemplate;
import com.mossle.internal.mail.manager.MailAttachmentManager;
import com.mossle.internal.mail.manager.MailConfigManager;
import com.mossle.internal.mail.manager.MailTemplateManager;

import org.springframework.core.io.FileSystemResource;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/mail")
public class MailTemplateController {
    private MailTemplateManager mailTemplateManager;
    private MailConfigManager mailConfigManager;
    private MailAttachmentManager mailAttachmentManager;
    private StoreConnector storeConnector;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();

    @RequestMapping("mail-template-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = mailTemplateManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "mail/mail-template-list";
    }

    @RequestMapping("mail-template-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            MailTemplate mailTemplate = mailTemplateManager.get(id);
            model.addAttribute("model", mailTemplate);
        }

        return "mail/mail-template-input";
    }

    @RequestMapping("mail-template-save")
    public String save(
            @ModelAttribute MailTemplate mailTemplate,
            @RequestParam(value = "attachmentIds", required = false) List<Long> attachmentIds,
            RedirectAttributes redirectAttributes) {
        Long id = mailTemplate.getId();
        MailTemplate dest = null;

        if (id != null) {
            dest = mailTemplateManager.get(id);
            beanMapper.copy(mailTemplate, dest);
        } else {
            dest = mailTemplate;
        }

        mailTemplateManager.save(dest);

        if (attachmentIds != null) {
            for (Long attachmentId : attachmentIds) {
                MailAttachment mailAttachment = mailAttachmentManager
                        .get(attachmentId);
                mailAttachment.setMailTemplate(dest);
                mailAttachmentManager.save(mailAttachment);
            }
        }

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/mail/mail-template-list.do";
    }

    @RequestMapping("mail-template-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<MailTemplate> mailTemplates = mailTemplateManager
                .findByIds(selectedItem);
        mailTemplateManager.removeAll(mailTemplates);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/mail/mail-template-list.do";
    }

    @RequestMapping("mail-template-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletResponse response) throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = mailTemplateManager.pagedQuery(page, propertyFilters);

        List<MailTemplate> mailTemplates = (List<MailTemplate>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("mail config");
        tableModel.addHeaders("id", "name");
        tableModel.setData(mailTemplates);
        exportor.export(response, tableModel);
    }

    @RequestMapping("mail-template-test")
    public String test(@RequestParam("id") Long id, Model model) {
        model.addAttribute("mailConfigs", mailConfigManager.getAll());
        model.addAttribute("mailTemplate", mailTemplateManager.get(id));

        return "mail/mail-template-test";
    }

    @RequestMapping("mail-template-send")
    public String test(@RequestParam("id") Long id, Long mailConfigId,
            Model model) throws Exception {
        MailTemplate mailTemplate = mailTemplateManager.get(id);
        MailConfig mailConfig = mailConfigManager.get(mailConfigId);

        MailServerInfo mailServerInfo = new MailServerInfo();
        mailServerInfo.setHost(mailConfig.getHost());
        mailServerInfo.setSmtpAuth(mailConfig.getSmtpAuth() == 1);
        mailServerInfo.setSmtpStarttls(mailConfig.getSmtpStarttls() == 1);
        mailServerInfo.setUsername(mailConfig.getUsername());
        mailServerInfo.setPassword(mailConfig.getPassword());
        mailServerInfo.setDefaultFrom(mailConfig.getDefaultFrom());

        MailDTO mailDto = new MailDTO();
        mailDto.setFrom(mailTemplate.getSender());
        mailDto.setTo(mailTemplate.getReceiver());
        mailDto.setCc(mailTemplate.getCc());
        mailDto.setBcc(mailTemplate.getBcc());
        mailDto.setSubject(mailTemplate.getSubject());
        mailDto.setContent(mailTemplate.getContent());

        for (MailAttachment mailAttachment : mailTemplate.getMailAttachments()) {
            mailDto.addAttachment(
                    mailAttachment.getName(),
                    storeConnector.get("mailattachment",
                            mailAttachment.getPath()).getResource());
        }

        MailDTO resultMailDto = new MailHelper().send(mailDto, mailServerInfo);
        model.addAttribute("mailDto", mailDto);

        if (!resultMailDto.isSuccess()) {
            StringWriter writer = new StringWriter();
            resultMailDto.getException().printStackTrace(
                    new PrintWriter(writer));
            model.addAttribute("exception", writer.toString());
        }

        return "mail/mail-template-send";
    }

    // ~ ======================================================================
    @Resource
    public void setMailTemplateManager(MailTemplateManager mailTemplateManager) {
        this.mailTemplateManager = mailTemplateManager;
    }

    @Resource
    public void setMailConfigManager(MailConfigManager mailConfigManager) {
        this.mailConfigManager = mailConfigManager;
    }

    @Resource
    public void setMailAttachmentManager(
            MailAttachmentManager mailAttachmentManager) {
        this.mailAttachmentManager = mailAttachmentManager;
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
}
