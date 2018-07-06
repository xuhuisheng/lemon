package com.mossle.report.web;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.report.persistence.domain.ReportSubject;
import com.mossle.report.persistence.manager.ReportSubjectManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("report")
public class ReportSubjectController {
    private ReportSubjectManager reportSubjectManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private CurrentUserHolder currentUserHolder;
    private TenantHolder tenantHolder;

    @RequestMapping("report-subject-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String userId = currentUserHolder.getUserId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = reportSubjectManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "report/report-subject-list";
    }

    @RequestMapping("report-subject-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            ReportSubject reportSubject = reportSubjectManager.get(id);
            model.addAttribute("model", reportSubject);
        }

        return "report/report-subject-input";
    }

    @RequestMapping("report-subject-save")
    public String save(@ModelAttribute ReportSubject reportSubject,
            RedirectAttributes redirectAttributes) {
        String userId = currentUserHolder.getUserId();
        String tenantId = tenantHolder.getTenantId();
        Long id = reportSubject.getId();
        ReportSubject dest = null;

        if (id != null) {
            dest = reportSubjectManager.get(id);
            beanMapper.copy(reportSubject, dest);
        } else {
            dest = reportSubject;
        }

        reportSubjectManager.save(dest);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/report/report-subject-list.do";
    }

    @RequestMapping("report-subject-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<ReportSubject> reportSubjects = reportSubjectManager
                .findByIds(selectedItem);
        reportSubjectManager.removeAll(reportSubjects);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/report/report-subject-list.do";
    }

    @RequestMapping("report-subject-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String userId = currentUserHolder.getUserId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_userId", userId));
        page = reportSubjectManager.pagedQuery(page, propertyFilters);

        List<ReportSubject> reportSubjects = (List<ReportSubject>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("report subject");
        tableModel.addHeaders("id", "name");
        tableModel.setData(reportSubjects);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setReportSubjectManager(
            ReportSubjectManager reportSubjectManager) {
        this.reportSubjectManager = reportSubjectManager;
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
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
