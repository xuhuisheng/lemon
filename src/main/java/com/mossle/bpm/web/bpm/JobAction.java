package com.mossle.bpm.web.bpm;

import java.util.List;

import com.mossle.core.struts2.BaseAction;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.runtime.Job;

import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

/**
 * 异步消息管理
 * 
 * @author LuZhao
 * 
 */
@Results({ @Result(name = JobAction.RELOAD, location = "job!list.do?operationMode=RETRIEVE", type = "redirect") })
public class JobAction extends BaseAction {
    public static final String RELOAD = "reload";
    private ProcessEngine processEngine;
    private List<Job> jobs;
    private String id;

    /** 作业列表 */
    public String list() {
        jobs = processEngine.getManagementService().createJobQuery().list();

        return "list";
    }

    /** 执行作业 */
    public String executeJob() {
        processEngine.getManagementService().executeJob(id);

        return RELOAD;
    }

    /** 删除作业 */
    public String removeJob() {
        processEngine.getManagementService().deleteJob(id);

        return RELOAD;
    }

    // ~ ==================================================
    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public void setId(String id) {
        this.id = id;
    }
}
