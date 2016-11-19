package com.mossle.bpm.web;

import javax.annotation.Resource;

import com.mossle.api.process.ProcessConnector;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.page.Page;

import org.activiti.engine.ProcessEngine;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 异步消息管理
 * 
 * @author LuZhao
 * 
 */
@Controller
@RequestMapping("bpm")
public class JobController {
    private ProcessEngine processEngine;
    private ProcessConnector processConnector;
    private TenantHolder tenantHolder;

    /** 作业列表 */
    @RequestMapping("job-list")
    public String list(@ModelAttribute Page page, Model model) {
        String tenantId = tenantHolder.getTenantId();
        page = processConnector.findJobs(tenantId, page);
        model.addAttribute("page", page);

        return "bpm/job-list";
    }

    /** 执行作业 */
    @RequestMapping("job-executeJob")
    public String executeJob(@RequestParam("id") String id) {
        processEngine.getManagementService().executeJob(id);

        return "redirect:/bpm/job-list.do";
    }

    /** 删除作业 */
    @RequestMapping("job-removeJob")
    public String removeJob(@RequestParam("id") String id) {
        processEngine.getManagementService().deleteJob(id);

        return "redirect:/bpm/job-list.do";
    }

    // ~ ==================================================
    @Resource
    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    @Resource
    public void setProcessConnector(ProcessConnector processConnector) {
        this.processConnector = processConnector;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
