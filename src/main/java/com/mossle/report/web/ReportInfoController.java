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

import com.mossle.report.persistence.domain.ReportInfo;
import com.mossle.report.persistence.domain.ReportQuery;
import com.mossle.report.persistence.manager.ReportInfoManager;
import com.mossle.report.persistence.manager.ReportQueryManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("report")
public class ReportInfoController {
    private ReportInfoManager reportInfoManager;
    private ReportQueryManager reportQueryManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private CurrentUserHolder currentUserHolder;
    private TenantHolder tenantHolder;

    @RequestMapping("report-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String userId = currentUserHolder.getUserId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = reportInfoManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "report/report-info-list";
    }

    @RequestMapping("report-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            ReportInfo reportInfo = reportInfoManager.get(id);
            model.addAttribute("model", reportInfo);
        }

        model.addAttribute("reportQueries", reportQueryManager.getAll());

        return "report/report-info-input";
    }

    @RequestMapping("report-info-save")
    public String save(@ModelAttribute ReportInfo reportInfo,
            @RequestParam("queryId") Long queryId,
            RedirectAttributes redirectAttributes) {
        String userId = currentUserHolder.getUserId();
        String tenantId = tenantHolder.getTenantId();
        Long id = reportInfo.getId();
        ReportInfo dest = null;

        if (id != null) {
            dest = reportInfoManager.get(id);
            beanMapper.copy(reportInfo, dest);
        } else {
            dest = reportInfo;
        }

        dest.setReportQuery(reportQueryManager.get(queryId));
        reportInfoManager.save(dest);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/report/report-info-list.do";
    }

    @RequestMapping("report-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<ReportInfo> reportInfos = reportInfoManager
                .findByIds(selectedItem);
        reportInfoManager.removeAll(reportInfos);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/report/report-info-list.do";
    }

    @RequestMapping("report-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String userId = currentUserHolder.getUserId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_userId", userId));
        page = reportInfoManager.pagedQuery(page, propertyFilters);

        List<ReportInfo> reportInfos = (List<ReportInfo>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("report subject");
        tableModel.addHeaders("id", "name");
        tableModel.setData(reportInfos);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setReportInfoManager(ReportInfoManager reportInfoManager) {
        this.reportInfoManager = reportInfoManager;
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
