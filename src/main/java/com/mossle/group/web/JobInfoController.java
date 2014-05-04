package com.mossle.group.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.api.scope.ScopeHolder;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;

import com.mossle.ext.export.Exportor;
import com.mossle.ext.export.TableModel;

import com.mossle.group.domain.JobInfo;
import com.mossle.group.domain.JobLevel;
import com.mossle.group.domain.JobTitle;
import com.mossle.group.domain.JobType;
import com.mossle.group.manager.JobInfoManager;
import com.mossle.group.manager.JobLevelManager;
import com.mossle.group.manager.JobTitleManager;
import com.mossle.group.manager.JobTypeManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("group")
public class JobInfoController {
    private JobInfoManager jobInfoManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private JobTypeManager jobTypeManager;
    private JobTitleManager jobTitleManager;
    private JobLevelManager jobLevelManager;

    @RequestMapping("job-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_scopeId", ScopeHolder
                .getScopeId()));
        page = jobInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "group/job-info-list";
    }

    @RequestMapping("job-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            JobInfo jobInfo = jobInfoManager.get(id);
            model.addAttribute("model", jobInfo);
        }

        model.addAttribute("jobTitles", jobTitleManager.getAll());
        model.addAttribute("jobTypes", jobTypeManager.getAll());
        model.addAttribute("jobLevels", jobLevelManager.getAll());

        return "group/job-info-input";
    }

    @RequestMapping("job-info-save")
    public String save(@ModelAttribute JobInfo jobInfo,
            @RequestParam("jobTitleId") long jobTitleId,
            @RequestParam("jobTypeId") long jobTypeId,
            @RequestParam("jobLevelId") long jobLevelId,
            RedirectAttributes redirectAttributes) {
        JobInfo dest = null;
        Long id = jobInfo.getId();

        if (id != null) {
            dest = jobInfoManager.get(id);
            beanMapper.copy(jobInfo, dest);
        } else {
            dest = jobInfo;
        }

        if (id == null) {
            dest.setScopeId(ScopeHolder.getScopeId());
        }

        dest.setJobTitle(jobTitleManager.get(jobTitleId));
        dest.setJobType(jobTypeManager.get(jobTypeId));
        dest.setJobLevel(jobLevelManager.get(jobLevelId));

        jobInfoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/group/job-info-list.do";
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

        return "redirect:/group/job-info-list.do";
    }

    @RequestMapping("job-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletResponse response) throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = jobInfoManager.pagedQuery(page, propertyFilters);

        List<JobInfo> jobInfos = (List<JobInfo>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("org");
        tableModel.addHeaders("id", "name");
        tableModel.setData(jobInfos);
        exportor.export(response, tableModel);
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
}
