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

import com.mossle.group.domain.JobGrade;
import com.mossle.group.domain.JobLevel;
import com.mossle.group.manager.JobGradeManager;
import com.mossle.group.manager.JobLevelManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("group")
public class JobLevelController {
    private JobLevelManager jobLevelManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private JobGradeManager jobGradeManager;

    @RequestMapping("job-level-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_scopeId", ScopeHolder
                .getScopeId()));
        page = jobLevelManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "group/job-level-list";
    }

    @RequestMapping("job-level-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            JobLevel jobLevel = jobLevelManager.get(id);
            model.addAttribute("model", jobLevel);
        }

        model.addAttribute("jobGrades", jobGradeManager.getAll());

        return "group/job-level-input";
    }

    @RequestMapping("job-level-save")
    public String save(@ModelAttribute JobLevel jobLevel,
            @RequestParam("jobGradeId") Long jobGradeId,
            RedirectAttributes redirectAttributes) {
        JobLevel dest = null;
        Long id = jobLevel.getId();

        if (id != null) {
            dest = jobLevelManager.get(id);
            beanMapper.copy(jobLevel, dest);
        } else {
            dest = jobLevel;
        }

        dest.setJobGrade(jobGradeManager.get(jobGradeId));

        if (id == null) {
            dest.setScopeId(ScopeHolder.getScopeId());
        }

        jobLevelManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/group/job-level-list.do";
    }

    @RequestMapping("job-level-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<JobLevel> jobLevels = jobLevelManager.findByIds(selectedItem);

        for (JobLevel jobLevel : jobLevels) {
            jobLevelManager.remove(jobLevel);
        }

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/group/job-level-list.do";
    }

    @RequestMapping("job-level-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletResponse response) throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = jobLevelManager.pagedQuery(page, propertyFilters);

        List<JobLevel> jobLevels = (List<JobLevel>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("org");
        tableModel.addHeaders("id", "name");
        tableModel.setData(jobLevels);
        exportor.export(response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setJobLevelManager(JobLevelManager jobLevelManager) {
        this.jobLevelManager = jobLevelManager;
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
    public void setJobGradeManager(JobGradeManager jobGradeManager) {
        this.jobGradeManager = jobGradeManager;
    }
}
