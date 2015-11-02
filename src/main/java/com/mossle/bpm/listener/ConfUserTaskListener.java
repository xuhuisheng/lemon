package com.mossle.bpm.listener;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.bpm.persistence.domain.BpmConfUser;
import com.mossle.bpm.persistence.manager.BpmConfUserManager;
import com.mossle.bpm.support.DefaultTaskListener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.el.ExpressionManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfUserTaskListener extends DefaultTaskListener {
    private static Logger logger = LoggerFactory
            .getLogger(ConfUserTaskListener.class);
    private BpmConfUserManager bpmConfUserManager;

    @Override
    public void onCreate(DelegateTask delegateTask) throws Exception {
        List<BpmConfUser> bpmConfUsers = bpmConfUserManager
                .find("from BpmConfUser where bpmConfNode.bpmConfBase.processDefinitionId=? and bpmConfNode.code=?",
                        delegateTask.getProcessDefinitionId(), delegateTask
                                .getExecution().getCurrentActivityId());
        logger.debug("{}", bpmConfUsers);

        ExpressionManager expressionManager = Context
                .getProcessEngineConfiguration().getExpressionManager();

        try {
            for (BpmConfUser bpmConfUser : bpmConfUsers) {
                logger.debug("status : {}, type: {}", bpmConfUser.getStatus(),
                        bpmConfUser.getType());
                logger.debug("value : {}", bpmConfUser.getValue());

                String value = expressionManager
                        .createExpression(bpmConfUser.getValue())
                        .getValue(delegateTask).toString();

                if (bpmConfUser.getStatus() == 1) {
                    if (bpmConfUser.getType() == 0) {
                        delegateTask.setAssignee(value);
                    } else if (bpmConfUser.getType() == 1) {
                        delegateTask.addCandidateUser(value);
                    } else if (bpmConfUser.getType() == 2) {
                        delegateTask.addCandidateGroup(value);
                    }
                } else if (bpmConfUser.getStatus() == 2) {
                    if (bpmConfUser.getType() == 0) {
                        if (delegateTask.getAssignee().equals(value)) {
                            delegateTask.setAssignee(null);
                        }
                    } else if (bpmConfUser.getType() == 1) {
                        delegateTask.deleteCandidateUser(value);
                    } else if (bpmConfUser.getType() == 2) {
                        delegateTask.deleteCandidateGroup(value);
                    }
                }
            }
        } catch (Exception ex) {
            logger.debug(ex.getMessage(), ex);
        }
    }

    @Resource
    public void setBpmConfUserManager(BpmConfUserManager bpmConfUserManager) {
        this.bpmConfUserManager = bpmConfUserManager;
    }
}
