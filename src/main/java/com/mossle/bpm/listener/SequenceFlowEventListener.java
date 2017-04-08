package com.mossle.bpm.listener;

import java.text.SimpleDateFormat;

import java.util.Date;

import javax.annotation.Resource;

import com.mossle.api.user.UserConnector;

import com.mossle.bpm.persistence.domain.BpmSequence;
import com.mossle.bpm.persistence.manager.BpmSequenceManager;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.ActivitiSequenceFlowTakenEvent;
import org.activiti.engine.delegate.event.BaseEntityEventListener;
import org.activiti.engine.delegate.event.impl.ActivitiEntityEventImpl;
import org.activiti.engine.impl.cmd.GetDeploymentProcessDefinitionCmd;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.HistoricProcessInstanceEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;

public class SequenceFlowEventListener implements ActivitiEventListener {
    private BpmSequenceManager bpmSequenceManager;

    public void onEvent(ActivitiEvent event) {
        if (!(event instanceof ActivitiSequenceFlowTakenEvent)) {
            return;
        }

        ActivitiSequenceFlowTakenEvent activitiSequenceFlowTakenEvent = (ActivitiSequenceFlowTakenEvent) event;
        String code = activitiSequenceFlowTakenEvent.getId();
        String sourceCode = activitiSequenceFlowTakenEvent
                .getSourceActivityId();
        String targetCode = activitiSequenceFlowTakenEvent
                .getTargetActivityId();
        String processInstanceId = activitiSequenceFlowTakenEvent
                .getProcessInstanceId();
        BpmSequence bpmSequence = new BpmSequence();
        bpmSequence.setCode(code);
        bpmSequence.setSourceCode(sourceCode);
        bpmSequence.setTargetCode(targetCode);
        bpmSequence.setProcessInstanceId(processInstanceId);
        bpmSequence.setCreateTime(new Date());
        bpmSequenceManager.save(bpmSequence);
    }

    public boolean isFailOnException() {
        return false;
    }

    @Resource
    public void setBpmSequenceManager(BpmSequenceManager bpmSequenceManager) {
        this.bpmSequenceManager = bpmSequenceManager;
    }
}
