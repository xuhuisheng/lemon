package com.mossle.bpm.service;

import javax.annotation.Resource;

import com.mossle.bpm.persistence.domain.BpmConfCountersign;
import com.mossle.bpm.persistence.manager.BpmConfCountersignManager;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.runtime.Execution;

import org.apache.commons.lang3.builder.ToStringBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

/**
 * 发文会签流程Service
 * 
 * @author henryyan
 */
@Service
public class CounterSignService {
    private Logger logger = LoggerFactory.getLogger(CounterSignService.class);
    private RuntimeService runtimeService;
    private TaskService taskService;
    private HistoryService historyService;
    private BpmConfCountersignManager bpmConfCountersignManager;

    public Boolean canComplete(Execution execution, Integer nrOfInstances,
            Integer nrOfActiveInstances, Integer nrOfCompletedInstances,
            Integer loopCounter) {
        return canComplete(execution, 100, nrOfInstances, nrOfActiveInstances,
                nrOfCompletedInstances, loopCounter);
    }

    /**
     * 是否允许结束会签（多实例） 参数的含义请参考用户手册
     */
    public Boolean canComplete(Execution execution, Integer rate,
            Integer nrOfInstances, Integer nrOfActiveInstances,
            Integer nrOfCompletedInstances, Integer loopCounter) {
        String activityId = execution.getActivityId();
        String processDefinitionId = ((ExecutionEntity) execution)
                .getProcessInstance().getProcessDefinitionId();

        BpmConfCountersign bpmConfCountersign = bpmConfCountersignManager
                .findUnique(
                        "from BpmConfCountersign where bpmConfNode.bpmConfBase.processDefinitionId=? and bpmConfNode.code=?",
                        processDefinitionId, activityId);

        if (bpmConfCountersign != null) {
            rate = bpmConfCountersign.getRate();
        }

        String agreeCounterName = "agreeCounter";
        Object agreeCounter = runtimeService.getVariable(execution.getId(),
                agreeCounterName);

        if (agreeCounter == null) {
            // 初始化计数器
            runtimeService.setVariable(execution.getId(), agreeCounterName, 1);
        } else {
            // 计数器累加
            Integer integerCounter = (Integer) runtimeService.getVariable(
                    execution.getId(), agreeCounterName);
            runtimeService.setVariable(execution.getId(), agreeCounterName,
                    ++integerCounter);
        }

        logger.debug("execution: {}",
                ToStringBuilder.reflectionToString(execution));
        logger.debug(
                "rate={}, nrOfInstances={}, nrOfActiveInstances={}, nrOfComptetedInstances={}, loopCounter={}",
                new Object[] { rate, nrOfInstances, nrOfActiveInstances,
                        nrOfCompletedInstances, loopCounter });

        // 计算通过的比例，以此决定是否结束会签
        double completeRate = nrOfCompletedInstances.doubleValue()
                / nrOfInstances;
        boolean canComlete = (completeRate * 100) >= rate;
        logger.debug("rate: {}, completeRate: {}, canComlete={}", new Object[] {
                rate, completeRate, canComlete });

        return canComlete;
    }

    @Resource
    public void setRuntimeService(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    @Resource
    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    @Resource
    public void setHistoryService(HistoryService historyService) {
        this.historyService = historyService;
    }

    @Resource
    public void setBpmConfCountersignManager(
            BpmConfCountersignManager bpmConfCountersignManager) {
        this.bpmConfCountersignManager = bpmConfCountersignManager;
    }
}
