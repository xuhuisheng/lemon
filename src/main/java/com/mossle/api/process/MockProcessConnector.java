package com.mossle.api.process;

import java.util.Map;

import com.mossle.api.form.FormDTO;

import com.mossle.core.page.Page;

public class MockProcessConnector implements ProcessConnector {
    /**
     * 获得启动表单.
     */
    public FormDTO findStartForm(String processDefinitionId) {
        return null;
    }

    /**
     * 获得流程配置.
     */
    public ProcessDTO findProcess(String processId) {
        return null;
    }

    /**
     * 发起流程.
     */
    public String startProcess(String userId, String businessKey,
            String processDefinitionId, Map<String, Object> processParemeters) {
        return null;
    }

    /**
     * 未结流程.
     */
    public Page findRunningProcessInstances(String userId, String tenantId,
            Page page) {
        return null;
    }

    /**
     * 已结流程.
     */
    public Page findCompletedProcessInstances(String userId, String tenantId,
            Page page) {
        return null;
    }

    /**
     * 参与流程.
     */
    public Page findInvolvedProcessInstances(String userId, String tenantId,
            Page page) {
        return null;
    }

    /**
     * 待办任务（个人任务）.
     */
    public Page findPersonalTasks(String userId, String tenantId, Page page) {
        return null;
    }

    /**
     * 代领任务（组任务）.
     */
    public Page findGroupTasks(String userId, String tenantId, Page page) {
        return null;
    }

    /**
     * 已办任务（历史任务）.
     */
    public Page findHistoryTasks(String userId, String tenantId, Page page) {
        return null;
    }

    /**
     * 代理中的任务（代理人还未完成该任务）.
     */
    public Page findDelegatedTasks(String userId, String tenantId, Page page) {
        return null;
    }

    /**
     * 同时返回已领取和未领取的任务.
     */
    public Page findCandidateOrAssignedTasks(String userId, String tenantId,
            Page page) {
        return null;
    }

    /**
     * 流程定义.
     */
    public Page findProcessDefinitions(String tenantId, Page page) {
        return null;
    }

    /**
     * 流程实例.
     */
    public Page findProcessInstances(String tenantId, Page page) {
        return null;
    }

    /**
     * 任务.
     */
    public Page findTasks(String tenantId, Page page) {
        return null;
    }

    /**
     * 部署.
     */
    public Page findDeployments(String tenantId, Page page) {
        return null;
    }

    /**
     * 历史流程实例.
     */
    public Page findHistoricProcessInstances(String tenantId, Page page) {
        return null;
    }

    /**
     * 历史节点.
     */
    public Page findHistoricActivityInstances(String tenantId, Page page) {
        return null;
    }

    /**
     * 历史任务.
     */
    public Page findHistoricTaskInstances(String tenantId, Page page) {
        return null;
    }

    /**
     * 作业.
     */
    public Page findJobs(String tenantId, Page page) {
        return null;
    }
}
