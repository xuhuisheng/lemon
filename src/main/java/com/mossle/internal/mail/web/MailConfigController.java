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

import com.mossle.internal.mail.domain.MailConfig;
import com.mossle.internal.mail.manager.MailConfigManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/mail")
public class MailConfigController {
    private MailConfigManager mailConfigManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();

    @RequestMapping("mail-config-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = mailConfigManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "mail/mail-config-list";
    }

    @RequestMapping("mail-config-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            MailConfig mailConfig = mailConfigManager.get(id);
            model.addAttribute("model", mailConfig);
        }

        return "mail/mail-config-input";
    }

    @RequestMapping("mail-config-save")
    public String save(@ModelAttribute MailConfig mailConfig,
            RedirectAttributes redirectAttributes) {
        Long id = mailConfig.getId();
        MailConfig dest = null;

        if (id != null) {
            dest = mailConfigManager.get(id);
            beanMapper.copy(mailConfig, dest);
        } else {
            dest = mailConfig;
        }

        mailConfigManager.save(dest);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/mail/mail-config-list.do";
    }

    @RequestMapping("mail-config-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<MailConfig> mailConfigs = mailConfigManager
                .findByIds(selectedItem);
        mailConfigManager.removeAll(mailConfigs);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/mail/mail-config-list.do";
    }

    @RequestMapping("mail-config-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletResponse response) throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = mailConfigManager.pagedQuery(page, propertyFilters);

        List<MailConfig> mailConfigs = (List<MailConfig>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("mail config");
        tableModel.addHeaders("id", "name");
        tableModel.setData(mailConfigs);
        exportor.export(response, tableModel);
    }

    @RequestMapping("mail-config-test")
    public String test(@RequestParam("id") Long id, Model model) {
        MailConfig mailConfig = mailConfigManager.get(id);
        MailServerInfo mailServerInfo = new MailServerInfo();
        mailServerInfo.setHost(mailConfig.getHost());
        mailServerInfo.setSmtpAuth(mailConfig.getSmtpAuth() == 1);
        mailServerInfo.setSmtpStarttls(mailConfig.getSmtpStarttls() == 1);
        mailServerInfo.setUsername(mailConfig.getUsername());
        mailServerInfo.setPassword(mailConfig.getPassword());
        mailServerInfo.setDefaultFrom(mailConfig.getDefaultFrom());

        MailDTO mailDto = new MailDTO();
        mailDto.setTo("demo.mossle@gmail.com");
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

        return "mail/mail-config-test";
    }

    // ~ ======================================================================
    @Resource
    public void setMailConfigManager(MailConfigManager mailConfigManager) {
        this.mailConfigManager = mailConfigManager;
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
