package com.mossle.feedback.web;

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

import com.mossle.feedback.persistence.domain.FeedbackInfo;
import com.mossle.feedback.persistence.manager.FeedbackInfoManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("feedback")
public class FeedbackInfoController {
    private FeedbackInfoManager feedbackInfoManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private MessageHelper messageHelper;
    private TenantHolder tenantHolder;

    @RequestMapping("feedback-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = feedbackInfoManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "feedback/feedback-info-list";
    }

    @RequestMapping("feedback-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            FeedbackInfo feedbackInfo = feedbackInfoManager.get(id);
            model.addAttribute("model", feedbackInfo);
        }

        return "feedback/feedback-info-input";
    }

    @RequestMapping("feedback-info-save")
    public String save(@ModelAttribute FeedbackInfo feedbackInfo,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        Long id = feedbackInfo.getId();
        FeedbackInfo dest = null;

        if (id != null) {
            dest = feedbackInfoManager.get(id);
            beanMapper.copy(feedbackInfo, dest);
        } else {
            dest = feedbackInfo;
        }

        feedbackInfoManager.save(dest);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/feedback/feedback-info-list.do";
    }

    @RequestMapping("feedback-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<FeedbackInfo> feedbackInfos = feedbackInfoManager
                .findByIds(selectedItem);
        feedbackInfoManager.removeAll(feedbackInfos);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/feedback/feedback-info-list.do";
    }

    @RequestMapping("feedback-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = feedbackInfoManager.pagedQuery(page, propertyFilters);

        List<FeedbackInfo> feedbackInfos = (List<FeedbackInfo>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("feedbackInfo");
        tableModel.addHeaders("id", "name");
        tableModel.setData(feedbackInfos);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setFeedbackInfoManager(FeedbackInfoManager feedbackInfoManager) {
        this.feedbackInfoManager = feedbackInfoManager;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
