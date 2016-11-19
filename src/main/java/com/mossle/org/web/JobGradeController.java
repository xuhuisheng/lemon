package com.mossle.org.web;

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

import com.mossle.org.persistence.domain.JobGrade;
import com.mossle.org.persistence.manager.JobGradeManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("org")
public class JobGradeController {
    private JobGradeManager jobGradeManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private TenantHolder tenantHolder;

    @RequestMapping("job-grade-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = jobGradeManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "org/job-grade-list";
    }

    @RequestMapping("job-grade-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            JobGrade jobGrade = jobGradeManager.get(id);
            model.addAttribute("model", jobGrade);
        }

        return "org/job-grade-input";
    }

    @RequestMapping("job-grade-save")
    public String save(@ModelAttribute JobGrade jobGrade,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        JobGrade dest = null;
        Long id = jobGrade.getId();

        if (id != null) {
            dest = jobGradeManager.get(id);
            beanMapper.copy(jobGrade, dest);
        } else {
            dest = jobGrade;
            dest.setTenantId(tenantId);
        }

        jobGradeManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/org/job-grade-list.do";
    }

    @RequestMapping("job-grade-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<JobGrade> jobGrades = jobGradeManager.findByIds(selectedItem);

        for (JobGrade jobGrade : jobGrades) {
            jobGradeManager.remove(jobGrade);
        }

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/org/job-grade-list.do";
    }

    @RequestMapping("job-grade-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = jobGradeManager.pagedQuery(page, propertyFilters);

        List<JobGrade> jobGrades = (List<JobGrade>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("org");
        tableModel.addHeaders("id", "name");
        tableModel.setData(jobGrades);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setJobGradeManager(JobGradeManager jobGradeManager) {
        this.jobGradeManager = jobGradeManager;
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
