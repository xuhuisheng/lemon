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

import com.mossle.internal.mail.domain.MailHistory;
import com.mossle.internal.mail.domain.MailQueue;
import com.mossle.internal.mail.manager.MailHistoryManager;
import com.mossle.internal.mail.manager.MailQueueManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/mail")
public class MailHistoryController {
    private MailHistoryManager mailHistoryManager;
    private MailQueueManager mailQueueManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();

    @RequestMapping("mail-history-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        page.setDefaultOrder("createTime", Page.DESC);

        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = mailHistoryManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "mail/mail-history-list";
    }

    @RequestMapping("mail-history-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            MailHistory mailHistory = mailHistoryManager.get(id);
            model.addAttribute("model", mailHistory);
        }

        return "mail/mail-history-input";
    }

    @RequestMapping("mail-history-save")
    public String save(@ModelAttribute MailHistory mailHistory,
            RedirectAttributes redirectAttributes) {
        Long id = mailHistory.getId();
        MailHistory dest = null;

        if (id != null) {
            dest = mailHistoryManager.get(id);
            beanMapper.copy(mailHistory, dest);
        } else {
            dest = mailHistory;
        }

        mailHistoryManager.save(dest);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/mail/mail-history-list.do";
    }

    @RequestMapping("mail-history-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<MailHistory> mailHistorys = mailHistoryManager
                .findByIds(selectedItem);
        mailHistoryManager.removeAll(mailHistorys);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/mail/mail-history-list.do";
    }

    @RequestMapping("mail-history-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletResponse response) throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = mailHistoryManager.pagedQuery(page, propertyFilters);

        List<MailHistory> mailHistorys = (List<MailHistory>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("mail config");
        tableModel.addHeaders("id", "name");
        tableModel.setData(mailHistorys);
        exportor.export(response, tableModel);
    }

    @RequestMapping("mail-history-view")
    public String view(@RequestParam("id") Long id, Model model) {
        model.addAttribute("mailHistory", mailHistoryManager.get(id));

        return "mail/mail-history-view";
    }

    @RequestMapping("mail-history-send")
    public String send(@RequestParam("id") Long id) {
        MailHistory mailHistory = mailHistoryManager.get(id);
        MailQueue mailQueue = new MailQueue();
        beanMapper.copy(mailHistory, mailQueue);
        mailQueue.setId(null);
        mailQueueManager.save(mailQueue);

        return "redirect:/mail/mail-queue-list.do";
    }

    // ~ ======================================================================
    @Resource
    public void setMailHistoryManager(MailHistoryManager mailHistoryManager) {
        this.mailHistoryManager = mailHistoryManager;
    }

    @Resource
    public void setMailQueueManager(MailQueueManager mailQueueManager) {
        this.mailQueueManager = mailQueueManager;
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
