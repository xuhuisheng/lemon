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

import com.mossle.org.persistence.domain.JobInfo;
import com.mossle.org.persistence.manager.JobInfoManager;
import com.mossle.org.persistence.manager.JobLevelManager;
import com.mossle.org.persistence.manager.JobTitleManager;
import com.mossle.org.persistence.manager.JobTypeManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("org")
public class JobInfoController {
    private JobInfoManager jobInfoManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private JobTypeManager jobTypeManager;
    private JobTitleManager jobTitleManager;
    private JobLevelManager jobLevelManager;
    private TenantHolder tenantHolder;

    @RequestMapping("job-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = jobInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "org/job-info-list";
    }

    @RequestMapping("job-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        String tenantId = tenantHolder.getTenantId();

        if (id != null) {
            JobInfo jobInfo = jobInfoManager.get(id);
            model.addAttribute("model", jobInfo);
        }

        model.addAttribute("jobTitles",
                jobTitleManager.findBy("tenantId", tenantId));
        model.addAttribute("jobTypes",
                jobTypeManager.findBy("tenantId", tenantId));
        model.addAttribute("jobLevels",
                jobLevelManager.findBy("tenantId", tenantId));

        return "org/job-info-input";
    }

    @RequestMapping("job-info-save")
    public String save(@ModelAttribute JobInfo jobInfo,
            @RequestParam("jobTitleId") long jobTitleId,
            @RequestParam("jobTypeId") long jobTypeId,
            @RequestParam("jobLevelId") long jobLevelId,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        JobInfo dest = null;
        Long id = jobInfo.getId();

        if (id != null) {
            dest = jobInfoManager.get(id);
            beanMapper.copy(jobInfo, dest);
        } else {
            dest = jobInfo;
            dest.setTenantId(tenantId);
        }

        dest.setJobTitle(jobTitleManager.get(jobTitleId));
        dest.setJobType(jobTypeManager.get(jobTypeId));
        dest.setJobLevel(jobLevelManager.get(jobLevelId));

        jobInfoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/org/job-info-list.do";
    }

    @RequestMapping("job-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<JobInfo> jobInfos = jobInfoManager.findByIds(selectedItem);

        for (JobInfo jobInfo : jobInfos) {
            jobInfoManager.remove(jobInfo);
        }

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/org/job-info-list.do";
    }

    @RequestMapping("job-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = jobInfoManager.pagedQuery(page, propertyFilters);

        List<JobInfo> jobInfos = (List<JobInfo>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("org");
        tableModel.addHeaders("id", "name");
        tableModel.setData(jobInfos);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setJobInfoManager(JobInfoManager jobInfoManager) {
        this.jobInfoManager = jobInfoManager;
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
    public void setJobTypeManager(JobTypeManager jobTypeManager) {
        this.jobTypeManager = jobTypeManager;
    }

    @Resource
    public void setJobTitleManager(JobTitleManager jobTitleManager) {
        this.jobTitleManager = jobTitleManager;
    }

    @Resource
    public void setJobLevelManager(JobLevelManager jobLevelManager) {
        this.jobLevelManager = jobLevelManager;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
