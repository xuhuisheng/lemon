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
import com.mossle.group.manager.JobGradeManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("group")
public class JobGradeController {
    private JobGradeManager jobGradeManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();

    @RequestMapping("job-grade-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_scopeId", ScopeHolder
                .getScopeId()));
        page = jobGradeManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "group/job-grade-list";
    }

    @RequestMapping("job-grade-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            JobGrade jobGrade = jobGradeManager.get(id);
            model.addAttribute("model", jobGrade);
        }

        return "group/job-grade-input";
    }

    @RequestMapping("job-grade-save")
    public String save(@ModelAttribute JobGrade jobGrade,
            RedirectAttributes redirectAttributes) {
        JobGrade dest = null;
        Long id = jobGrade.getId();

        if (id != null) {
            dest = jobGradeManager.get(id);
            beanMapper.copy(jobGrade, dest);
        } else {
            dest = jobGrade;
        }

        if (id == null) {
            dest.setScopeId(ScopeHolder.getScopeId());
        }

        jobGradeManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/group/job-grade-list.do";
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

        return "redirect:/group/job-grade-list.do";
    }

    @RequestMapping("job-grade-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletResponse response) throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = jobGradeManager.pagedQuery(page, propertyFilters);

        List<JobGrade> jobGrades = (List<JobGrade>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("org");
        tableModel.addHeaders("id", "name");
        tableModel.setData(jobGrades);
        exportor.export(response, tableModel);
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
}
