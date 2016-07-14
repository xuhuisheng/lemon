package com.mossle.org.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserConnector;

import com.mossle.core.export.Exportor;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.spring.MessageHelper;

import com.mossle.org.persistence.domain.JobGrade;
import com.mossle.org.persistence.domain.JobInfo;
import com.mossle.org.persistence.domain.JobLevel;
import com.mossle.org.persistence.domain.JobType;
import com.mossle.org.persistence.domain.JobUser;
import com.mossle.org.persistence.manager.JobGradeManager;
import com.mossle.org.persistence.manager.JobInfoManager;
import com.mossle.org.persistence.manager.JobLevelManager;
import com.mossle.org.persistence.manager.JobTitleManager;
import com.mossle.org.persistence.manager.JobTypeManager;
import com.mossle.org.persistence.manager.JobUserManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("com.mossle.org.web.JobController")
@RequestMapping("org")
public class JobController {
    private JobGradeManager jobGradeManager;
    private JobLevelManager jobLevelManager;
    private JobTitleManager jobTitleManager;
    private JobTypeManager jobTypeManager;
    private JobInfoManager jobInfoManager;
    private JobUserManager jobUserManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private TenantHolder tenantHolder;

    @RequestMapping("job-list")
    public String list(Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<JobType> jobTypes = jobTypeManager.findBy("tenantId", tenantId);
        model.addAttribute("jobTypes", jobTypes);

        List<JobGrade> jobGrades = jobGradeManager.find(
                "from JobGrade where tenantId=? order by priority", tenantId);
        model.addAttribute("jobGrades", jobGrades);

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        model.addAttribute("list", list);

        Map<String, Object> previousMap = null;

        for (JobGrade jobGrade : jobGrades) {
            boolean printJobGrade = true;
            List<JobLevel> jobLevels = jobLevelManager.find(
                    "from JobLevel where jobGrade=? order by priority",
                    jobGrade);

            for (JobLevel jobLevel : jobLevels) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("printJobGrade", printJobGrade);
                map.put("jobGrade", jobGrade);
                map.put("jobGradeSize", jobGrade.getJobLevels().size());
                map.put("jobLevel", jobLevel);

                List<JobInfo> jobInfos = new ArrayList<JobInfo>();
                map.put("jobInfos", jobInfos);

                for (JobType jobType : jobTypes) {
                    String hql = "from JobInfo where jobType=? and jobLevel=?";
                    JobInfo jobInfo = jobInfoManager.findUnique(hql, jobType,
                            jobLevel);
                    jobInfos.add(jobInfo);
                }

                list.add(map);

                // jobGrade rowspan
                if (printJobGrade) {
                    printJobGrade = false;
                }

                // jobTitle rowspan
                if (previousMap == null) {
                    previousMap = map;
                } else {
                }
            }
        }

        return "org/job-list";
    }

    @RequestMapping("job-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        String tenantId = tenantHolder.getTenantId();

        if (id != null) {
            JobUser jobUser = jobUserManager.get(id);
            model.addAttribute("model", jobUser);
        }

        model.addAttribute("jobInfos",
                jobInfoManager.findBy("tenantId", tenantId));

        return "org/job-input";
    }

    @RequestMapping("job-save")
    public String save(@ModelAttribute JobUser jobUser,
            @RequestParam("jobId") long jobId,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        JobUser dest = null;
        Long id = jobUser.getId();

        if (id != null) {
            dest = jobUserManager.get(id);
            beanMapper.copy(jobUser, dest);
        } else {
            dest = jobUser;
            dest.setTenantId(tenantId);
        }

        dest.setJobInfo(jobInfoManager.get(jobId));

        jobUserManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/org/job-list.do";
    }

    @RequestMapping("job-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<JobUser> jobUsers = jobUserManager.findByIds(selectedItem);

        for (JobUser jobUser : jobUsers) {
            jobUserManager.remove(jobUser);
        }

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/org/job-list.do";
    }

    // ~ ======================================================================
    @Resource
    public void setJobGradeManager(JobGradeManager jobGradeManager) {
        this.jobGradeManager = jobGradeManager;
    }

    @Resource
    public void setJobLevelManager(JobLevelManager jobLevelManager) {
        this.jobLevelManager = jobLevelManager;
    }

    @Resource
    public void setJobTitleManager(JobTitleManager jobTitleManager) {
        this.jobTitleManager = jobTitleManager;
    }

    @Resource
    public void setJobTypeManager(JobTypeManager jobTypeManager) {
        this.jobTypeManager = jobTypeManager;
    }

    @Resource
    public void setJobInfoManager(JobInfoManager jobInfoManager) {
        this.jobInfoManager = jobInfoManager;
    }

    @Resource
    public void setJobUserManager(JobUserManager jobUserManager) {
        this.jobUserManager = jobUserManager;
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
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
