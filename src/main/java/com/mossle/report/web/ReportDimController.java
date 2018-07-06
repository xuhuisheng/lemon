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

import com.mossle.report.persistence.domain.ReportDim;
import com.mossle.report.persistence.domain.ReportQuery;
import com.mossle.report.persistence.manager.ReportDimManager;
import com.mossle.report.persistence.manager.ReportQueryManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("report")
public class ReportDimController {
    private ReportDimManager reportDimManager;
    private ReportQueryManager reportQueryManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private CurrentUserHolder currentUserHolder;
    private TenantHolder tenantHolder;

    @RequestMapping("report-dim-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String userId = currentUserHolder.getUserId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = reportDimManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "report/report-dim-list";
    }

    @RequestMapping("report-dim-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            ReportDim reportDim = reportDimManager.get(id);
            model.addAttribute("model", reportDim);
        }

        model.addAttribute("reportQueries", reportQueryManager.getAll());

        return "report/report-dim-input";
    }

    @RequestMapping("report-dim-save")
    public String save(@ModelAttribute ReportDim reportDim,
            @RequestParam("queryId") Long queryId,
            RedirectAttributes redirectAttributes) {
        String userId = currentUserHolder.getUserId();
        String tenantId = tenantHolder.getTenantId();
        Long id = reportDim.getId();
        ReportDim dest = null;

        if (id != null) {
            dest = reportDimManager.get(id);
            beanMapper.copy(reportDim, dest);
        } else {
            dest = reportDim;
        }

        dest.setReportQuery(reportQueryManager.get(queryId));
        reportDimManager.save(dest);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/report/report-dim-list.do";
    }

    @RequestMapping("report-dim-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<ReportDim> reportDims = reportDimManager.findByIds(selectedItem);
        reportDimManager.removeAll(reportDims);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/report/report-dim-list.do";
    }

    @RequestMapping("report-dim-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String userId = currentUserHolder.getUserId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_userId", userId));
        page = reportDimManager.pagedQuery(page, propertyFilters);

        List<ReportDim> reportDims = (List<ReportDim>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("report subject");
        tableModel.addHeaders("id", "name");
        tableModel.setData(reportDims);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setReportDimManager(ReportDimManager reportDimManager) {
        this.reportDimManager = reportDimManager;
    }

    @Resource
    public void setReportQueryManager(ReportQueryManager reportQueryManager) {
        this.reportQueryManager = reportQueryManager;
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
