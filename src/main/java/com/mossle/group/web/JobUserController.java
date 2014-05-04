package com.mossle.group.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.api.scope.ScopeHolder;
import com.mossle.api.user.UserConnector;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;

import com.mossle.ext.export.Exportor;
import com.mossle.ext.export.TableModel;

import com.mossle.group.domain.JobInfo;
import com.mossle.group.domain.JobUser;
import com.mossle.group.manager.JobInfoManager;
import com.mossle.group.manager.JobUserManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("group")
public class JobUserController {
    private JobInfoManager jobInfoManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private JobUserManager jobUserManager;
    private UserConnector userConnector;

    @RequestMapping("job-user-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_scopeId", ScopeHolder
                .getScopeId()));
        page = jobUserManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "group/job-user-list";
    }

    @RequestMapping("job-user-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            JobUser jobUser = jobUserManager.get(id);
            model.addAttribute("model", jobUser);
        }

        model.addAttribute("jobInfos", jobInfoManager.getAll());

        return "group/job-user-input";
    }

    @RequestMapping("job-user-save")
    public String save(@ModelAttribute JobUser jobUser,
            @RequestParam("jobId") long jobId,
            RedirectAttributes redirectAttributes) {
        JobUser dest = null;
        Long id = jobUser.getId();

        if (id != null) {
            dest = jobUserManager.get(id);
            beanMapper.copy(jobUser, dest);
        } else {
            dest = jobUser;
        }

        if (id == null) {
            dest.setScopeId(ScopeHolder.getScopeId());
        }

        dest.setJobInfo(jobInfoManager.get(jobId));

        jobUserManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/group/job-user-list.do";
    }

    @RequestMapping("job-user-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<JobUser> jobUsers = jobUserManager.findByIds(selectedItem);

        for (JobUser jobUser : jobUsers) {
            jobUserManager.remove(jobUser);
        }

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/group/job-user-list.do";
    }

    @RequestMapping("job-user-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletResponse response) throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = jobUserManager.pagedQuery(page, propertyFilters);

        List<JobUser> jobUsers = (List<JobUser>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("org");
        tableModel.addHeaders("id", "name");
        tableModel.setData(jobUsers);
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
    public void setJobUserManager(JobUserManager jobUserManager) {
        this.jobUserManager = jobUserManager;
    }

    @Resource
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }
}
