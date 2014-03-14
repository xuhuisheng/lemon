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

import com.mossle.group.domain.JobTitle;
import com.mossle.group.manager.JobTitleManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("group")
public class JobTitleController {
    private JobTitleManager jobTitleManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();

    @RequestMapping("job-title-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_scopeId", ScopeHolder
                .getScopeId()));
        page = jobTitleManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "group/job-title-list";
    }

    @RequestMapping("job-title-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            JobTitle jobTitle = jobTitleManager.get(id);
            model.addAttribute("model", jobTitle);
        }

        return "group/job-title-input";
    }

    @RequestMapping("job-title-save")
    public String save(@ModelAttribute JobTitle jobTitle,
            RedirectAttributes redirectAttributes) {
        JobTitle dest = null;
        Long id = jobTitle.getId();

        if (id != null) {
            dest = jobTitleManager.get(id);
            beanMapper.copy(jobTitle, dest);
        } else {
            dest = jobTitle;
        }

        if (id == null) {
            dest.setScopeId(ScopeHolder.getScopeId());
        }

        jobTitleManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/group/job-title-list.do";
    }

    @RequestMapping("job-title-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<JobTitle> jobTitles = jobTitleManager.findByIds(selectedItem);

        for (JobTitle jobTitle : jobTitles) {
            jobTitleManager.remove(jobTitle);
        }

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/group/job-title-list.do";
    }

    @RequestMapping("job-title-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletResponse response) throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = jobTitleManager.pagedQuery(page, propertyFilters);

        List<JobTitle> jobTitles = (List<JobTitle>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("org");
        tableModel.addHeaders("id", "name");
        tableModel.setData(jobTitles);
        exportor.export(response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setJobTitleManager(JobTitleManager jobTitleManager) {
        this.jobTitleManager = jobTitleManager;
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
