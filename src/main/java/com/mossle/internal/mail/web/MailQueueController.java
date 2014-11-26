package com.mossle.internal.mail.web;

import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;
import com.mossle.core.util.StringUtils;

import com.mossle.ext.export.Exportor;
import com.mossle.ext.export.TableModel;
import com.mossle.ext.mail.MailDTO;
import com.mossle.ext.mail.MailHelper;
import com.mossle.ext.mail.MailServerInfo;

import com.mossle.internal.mail.domain.MailConfig;
import com.mossle.internal.mail.domain.MailQueue;
import com.mossle.internal.mail.domain.MailTemplate;
import com.mossle.internal.mail.manager.MailConfigManager;
import com.mossle.internal.mail.manager.MailQueueManager;
import com.mossle.internal.mail.manager.MailTemplateManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/mail")
public class MailQueueController {
    private MailQueueManager mailQueueManager;
    private MailConfigManager mailConfigManager;
    private MailTemplateManager mailTemplateManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();

    @RequestMapping("mail-queue-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        page.setDefaultOrder("createTime", Page.DESC);

        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = mailQueueManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "mail/mail-queue-list";
    }

    @RequestMapping("mail-queue-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            MailQueue mailQueue = mailQueueManager.get(id);
            model.addAttribute("model", mailQueue);
        }

        model.addAttribute("mailConfigs", mailConfigManager.getAll());
        model.addAttribute("mailTemplates", mailTemplateManager.getAll());

        return "mail/mail-queue-input";
    }

    @RequestMapping("mail-queue-save")
    public String save(@ModelAttribute MailQueue mailQueue,
            @RequestParam("mailConfigId") Long mailConfigId,
            @RequestParam("mailTemplateId") Long mailTemplateId,
            RedirectAttributes redirectAttributes) {
        Long id = mailQueue.getId();
        MailQueue dest = null;

        if (id != null) {
            dest = mailQueueManager.get(id);
            beanMapper.copy(mailQueue, dest);
        } else {
            dest = mailQueue;
            dest.setCreateTime(new Date());
        }

        MailTemplate mailTemplate = mailTemplateManager.get(mailTemplateId);
        dest.setMailConfig(mailConfigManager.get(mailConfigId));
        dest.setMailTemplate(mailTemplate);

        mailQueueManager.save(dest);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/mail/mail-queue-list.do";
    }

    @RequestMapping("mail-queue-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<MailQueue> mailQueues = mailQueueManager.findByIds(selectedItem);
        mailQueueManager.removeAll(mailQueues);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/mail/mail-queue-list.do";
    }

    @RequestMapping("mail-queue-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletResponse response) throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = mailQueueManager.pagedQuery(page, propertyFilters);

        List<MailQueue> mailQueues = (List<MailQueue>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("mail config");
        tableModel.addHeaders("id", "name");
        tableModel.setData(mailQueues);
        exportor.export(response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setMailQueueManager(MailQueueManager mailQueueManager) {
        this.mailQueueManager = mailQueueManager;
    }

    @Resource
    public void setMailConfigManager(MailConfigManager mailConfigManager) {
        this.mailConfigManager = mailConfigManager;
    }

    @Resource
    public void setMailTemplateManager(MailTemplateManager mailTemplateManager) {
        this.mailTemplateManager = mailTemplateManager;
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
