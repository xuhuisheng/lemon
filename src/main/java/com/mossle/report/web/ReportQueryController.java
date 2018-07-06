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

import com.mossle.report.persistence.domain.ReportQuery;
import com.mossle.report.persistence.domain.ReportSubject;
import com.mossle.report.persistence.manager.ReportQueryManager;
import com.mossle.report.persistence.manager.ReportSubjectManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("report")
public class ReportQueryController {
    private ReportQueryManager reportQueryManager;
    private ReportSubjectManager reportSubjectManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private CurrentUserHolder currentUserHolder;
    private TenantHolder tenantHolder;

    @RequestMapping("report-query-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String userId = currentUserHolder.getUserId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = reportQueryManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "report/report-query-list";
    }

    @RequestMapping("report-query-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            ReportQuery reportQuery = reportQueryManager.get(id);
            model.addAttribute("model", reportQuery);
        }

        model.addAttribute("reportSubjects", reportSubjectManager.getAll());

        return "report/report-query-input";
    }

    @RequestMapping("report-query-save")
    public String save(@ModelAttribute ReportQuery reportQuery,
            @RequestParam("subjectId") Long subjectId,
            RedirectAttributes redirectAttributes) {
        String userId = currentUserHolder.getUserId();
        String tenantId = tenantHolder.getTenantId();
        Long id = reportQuery.getId();
        ReportQuery dest = null;

        if (id != null) {
            dest = reportQueryManager.get(id);
            beanMapper.copy(reportQuery, dest);
        } else {
            dest = reportQuery;
        }

        dest.setReportSubject(reportSubjectManager.get(subjectId));
        reportQueryManager.save(dest);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/report/report-query-list.do";
    }

    @RequestMapping("report-query-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<ReportQuery> reportQuerys = reportQueryManager
                .findByIds(selectedItem);
        reportQueryManager.removeAll(reportQuerys);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/report/report-query-list.do";
    }

    @RequestMapping("report-query-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String userId = currentUserHolder.getUserId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_userId", userId));
        page = reportQueryManager.pagedQuery(page, propertyFilters);

        List<ReportQuery> reportQuerys = (List<ReportQuery>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("report subject");
        tableModel.addHeaders("id", "name");
        tableModel.setData(reportQuerys);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setReportQueryManager(ReportQueryManager reportQueryManager) {
        this.reportQueryManager = reportQueryManager;
    }

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
