package com.mossle.bpm.service;

import javax.annotation.Resource;

import com.mossle.spi.humantask.CounterSignDTO;
import com.mossle.spi.humantask.TaskDefinitionConnector;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.runtime.Execution;

import org.apache.commons.lang3.builder.ToStringBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

/**
 * 发文会签流程Service
 * 
 * @author henryyan
 */
@Service
public class CounterSignService {
    private Logger logger = LoggerFactory.getLogger(CounterSignService.class);
    private ProcessEngine processEngine;
    private TaskDefinitionConnector taskDefinitionConnector;

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

        RuntimeService runtimeService = processEngine.getRuntimeService();

        CounterSignDTO counterSign = taskDefinitionConnector.findCounterSign(
                activityId, processDefinitionId);

        if (counterSign != null) {
            rate = counterSign.getRate();
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
    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    @Resource
    public void setTaskDefinitionConnector(
            TaskDefinitionConnector taskDefinitionConnector) {
        this.taskDefinitionConnector = taskDefinitionConnector;
    }
}
