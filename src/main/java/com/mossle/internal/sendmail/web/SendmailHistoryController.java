package com.mossle.internal.sendmail.web;

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

import com.mossle.internal.sendmail.persistence.domain.SendmailHistory;
import com.mossle.internal.sendmail.persistence.domain.SendmailQueue;
import com.mossle.internal.sendmail.persistence.manager.SendmailHistoryManager;
import com.mossle.internal.sendmail.persistence.manager.SendmailQueueManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("sendmail")
public class SendmailHistoryController {
    private SendmailHistoryManager sendmailHistoryManager;
    private SendmailQueueManager sendmailQueueManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private TenantHolder tenantHolder;

    @RequestMapping("sendmail-history-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        page.setDefaultOrder("createTime", Page.DESC);

        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = sendmailHistoryManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "sendmail/sendmail-history-list";
    }

    @RequestMapping("sendmail-history-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            SendmailHistory sendmailHistory = sendmailHistoryManager.get(id);
            model.addAttribute("model", sendmailHistory);
        }

        return "sendmail/sendmail-history-input";
    }

    @RequestMapping("sendmail-history-save")
    public String save(@ModelAttribute SendmailHistory sendmailHistory,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        Long id = sendmailHistory.getId();
        SendmailHistory dest = null;

        if (id != null) {
            dest = sendmailHistoryManager.get(id);
            beanMapper.copy(sendmailHistory, dest);
        } else {
            dest = sendmailHistory;
            dest.setTenantId(tenantId);
        }

        sendmailHistoryManager.save(dest);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/sendmail/sendmail-history-list.do";
    }

    @RequestMapping("sendmail-history-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<SendmailHistory> sendmailHistorys = sendmailHistoryManager
                .findByIds(selectedItem);
        sendmailHistoryManager.removeAll(sendmailHistorys);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/sendmail/sendmail-history-list.do";
    }

    @RequestMapping("sendmail-history-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = sendmailHistoryManager.pagedQuery(page, propertyFilters);

        List<SendmailHistory> sendmailHistorys = (List<SendmailHistory>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("mail config");
        tableModel.addHeaders("id", "name");
        tableModel.setData(sendmailHistorys);
        exportor.export(request, response, tableModel);
    }

    @RequestMapping("sendmail-history-view")
    public String view(@RequestParam("id") Long id, Model model) {
        model.addAttribute("mailHistory", sendmailHistoryManager.get(id));

        return "sendmail/sendmail-history-view";
    }

    @RequestMapping("sendmail-history-send")
    public String send(@RequestParam("id") Long id) {
        SendmailHistory sendmailHistory = sendmailHistoryManager.get(id);
        SendmailQueue sendmailQueue = new SendmailQueue();
        beanMapper.copy(sendmailHistory, sendmailQueue);
        sendmailQueue.setId(null);
        sendmailQueueManager.save(sendmailQueue);

        return "redirect:/sendmail/sendmail-queue-list.do";
    }

    // ~ ======================================================================
    @Resource
    public void setSendmailHistoryManager(
            SendmailHistoryManager sendmailHistoryManager) {
        this.sendmailHistoryManager = sendmailHistoryManager;
    }

    @Resource
    public void setSendmailQueueManager(
            SendmailQueueManager sendmailQueueManager) {
        this.sendmailQueueManager = sendmailQueueManager;
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
