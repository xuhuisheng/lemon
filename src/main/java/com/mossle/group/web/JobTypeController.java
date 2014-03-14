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

import com.mossle.group.domain.JobType;
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
public class JobTypeController {
    private JobTypeManager jobTypeManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();

    @RequestMapping("job-type-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_scopeId", ScopeHolder
                .getScopeId()));
        page = jobTypeManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "group/job-type-list";
    }

    @RequestMapping("job-type-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            JobType jobType = jobTypeManager.get(id);
            model.addAttribute("model", jobType);
        }

        model.addAttribute("jobTypes", jobTypeManager.getAll());

        return "group/job-type-input";
    }

    @RequestMapping("job-type-save")
    public String save(
            @ModelAttribute JobType jobType,
            @RequestParam(value = "jobTypeId", required = false) Long jobTypeId,
            RedirectAttributes redirectAttributes) {
        JobType dest = null;
        Long id = jobType.getId();

        if (id != null) {
            dest = jobTypeManager.get(id);
            beanMapper.copy(jobType, dest);
        } else {
            dest = jobType;
        }

        if (jobTypeId != null) {
            dest.setJobType(jobTypeManager.get(jobTypeId));
        } else {
            dest.setJobType(null);
        }

        if (id == null) {
            dest.setScopeId(ScopeHolder.getScopeId());
        }

        jobTypeManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/group/job-type-list.do";
    }

    @RequestMapping("job-type-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<JobType> jobTypes = jobTypeManager.findByIds(selectedItem);

        for (JobType jobType : jobTypes) {
            jobTypeManager.remove(jobType);
        }

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/group/job-type-list.do";
    }

    @RequestMapping("job-type-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletResponse response) throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = jobTypeManager.pagedQuery(page, propertyFilters);

        List<JobType> jobTypes = (List<JobType>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("org");
        tableModel.addHeaders("id", "name");
        tableModel.setData(jobTypes);
        exportor.export(response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setJobTypeManager(JobTypeManager jobTypeManager) {
        this.jobTypeManager = jobTypeManager;
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
