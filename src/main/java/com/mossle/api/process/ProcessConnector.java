package com.mossle.api.process;

import java.util.Map;

import com.mossle.api.form.FormDTO;

import com.mossle.core.page.Page;

public interface ProcessConnector {
    /**
     * 获得启动表单.
     */
    FormDTO findStartForm(String processDefinitionId);

    /**
     * 获得流程配置.
     */
    ProcessDTO findProcess(String processId);

    /**
     * 发起流程.
     */
    String startProcess(String userId, String businessKey,
            String processDefinitionId, Map<String, Object> processParemeters);

    /**
     * 未结流程.
     */
    Page findRunningProcessInstances(String userId, String tenantId, Page page);

    /**
     * 已结流程.
     */
    Page findCompletedProcessInstances(String userId, String tenantId, Page page);

    /**
     * 参与流程.
     */
    Page findInvolvedProcessInstances(String userId, String tenantId, Page page);

    /**
     * 待办任务（个人任务）.
     */
    Page findPersonalTasks(String userId, String tenantId, Page page);

    /**
     * 代领任务（组任务）.
     */
    Page findGroupTasks(String userId, String tenantId, Page page);

    /**
     * 已办任务（历史任务）.
     */
    Page findHistoryTasks(String userId, String tenantId, Page page);

    /**
     * 代理中的任务（代理人还未完成该任务）.
     */
    Page findDelegatedTasks(String userId, String tenantId, Page page);

    /**
     * 同时返回已领取和未领取的任务.
     */
    Page findCandidateOrAssignedTasks(String userId, String tenantId, Page page);

    /**
     * 流程定义.
     */
    Page findProcessDefinitions(String tenantId, Page page);

    /**
     * 流程实例.
     */
    Page findProcessInstances(String tenantId, Page page);

    /**
     * 任务.
     */
    Page findTasks(String tenantId, Page page);

    /**
     * 部署.
     */
    Page findDeployments(String tenantId, Page page);

    /**
     * 历史流程实例.
     */
    Page findHistoricProcessInstances(String tenantId, Page page);

    /**
     * 历史节点.
     */
    Page findHistoricActivityInstances(String tenantId, Page page);

    /**
     * 历史任务.
     */
    Page findHistoricTaskInstances(String tenantId, Page page);

    /**
     * 作业.
     */
    Page findJobs(String tenantId, Page page);
}
