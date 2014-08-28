package com.mossle.api.process;

import com.mossle.core.page.Page;

public interface ProcessConnector {
    /**
     * 未结流程.
     */
    Page findRunningProcessInstances(String userId, Page page);

    /**
     * 已结流程.
     */
    Page findCompletedProcessInstances(String userId, Page page);

    /**
     * 参与流程.
     */
    Page findInvolvedProcessInstances(String userId, Page page);

    /**
     * 待办任务（个人任务）.
     */
    Page findPersonalTasks(String userId, Page page);

    /**
     * 代领任务（组任务）.
     */
    Page findGroupTasks(String userId, Page page);

    /**
     * 已办任务（历史任务）.
     */
    Page findHistoryTasks(String userId, Page page);

    /**
     * 代理中的任务（代理人还未完成该任务）.
     */
    Page findDelegatedTasks(String userId, Page page);

    /**
     * 流程定义.
     */
    Page findProcessDefinitions(Page page);

    /**
     * 流程实例.
     */
    Page findProcessInstances(Page page);

    /**
     * 任务.
     */
    Page findTasks(Page page);

    /**
     * 部署.
     */
    Page findDeployments(Page page);

    /**
     * 历史流程实例.
     */
    Page findHistoricProcessInstances(Page page);

    /**
     * 历史节点.
     */
    Page findHistoricActivityInstances(Page page);

    /**
     * 历史任务.
     */
    Page findHistoricTaskInstances(Page page);

    /**
     * 作业.
     */
    Page findJobs(Page page);
}
