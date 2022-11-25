package com.mossle.bpm.listener;

import java.util.Date;

import javax.annotation.Resource;

import com.mossle.bpm.persistence.domain.BpmSequence;
import com.mossle.bpm.persistence.manager.BpmSequenceManager;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.ActivitiSequenceFlowTakenEvent;

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
