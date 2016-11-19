package com.mossle.bpm.listener;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.humantask.HumanTaskConnector;
import com.mossle.api.humantask.HumanTaskConstants;
import com.mossle.api.humantask.HumanTaskDTO;
import com.mossle.api.humantask.ParticipantDTO;

import com.mossle.bpm.persistence.domain.BpmConfUser;
import com.mossle.bpm.persistence.manager.BpmConfUserManager;
import com.mossle.bpm.support.DefaultTaskListener;
import com.mossle.bpm.support.DelegateTaskHolder;
import com.mossle.bpm.support.HumanTaskBuilder;

import com.mossle.core.mapper.BeanMapper;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.el.ExpressionManager;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.task.IdentityLink;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HumanTaskTaskListener extends DefaultTaskListener {
    public static final int TYPE_COPY = 3;
    private static Logger logger = LoggerFactory
            .getLogger(HumanTaskTaskListener.class);
    private HumanTaskConnector humanTaskConnector;
    private BpmConfUserManager bpmConfUserManager;
    private BeanMapper beanMapper = new BeanMapper();

    @Override
    public void onCreate(DelegateTask delegateTask) throws Exception {
        HumanTaskDTO humanTaskDto = null;

        // 根据delegateTask创建HumanTaskDTO
        try {
            DelegateTaskHolder.setDelegateTask(delegateTask);

            humanTaskDto = this.createHumanTask(delegateTask);

            // 任务抄送
            this.checkCopyHumanTask(delegateTask, humanTaskDto);
        } finally {
            DelegateTaskHolder.clear();
        }

        if (humanTaskDto != null) {
            delegateTask.setAssignee(humanTaskDto.getAssignee());
            delegateTask.setOwner(humanTaskDto.getOwner());
        }
    }

    /**
     * 如果直接完成了activiti的task，要同步完成HumanTask.
     */
    @Override
    public void onComplete(DelegateTask delegateTask) throws Exception {
        HumanTaskDTO humanTaskDto = humanTaskConnector
                .findHumanTaskByTaskId(delegateTask.getId());

        if ("complete".equals(humanTaskDto.getStatus())) {
            return;
        }

        humanTaskDto.setStatus("complete");
        humanTaskDto.setCompleteTime(new Date());
        humanTaskDto.setAction("提交");

        humanTaskConnector.saveHumanTask(humanTaskDto, false);
    }

    @Override
    public void onDelete(DelegateTask delegateTask) throws Exception {
        HumanTaskDTO humanTaskDto = humanTaskConnector
                .findHumanTaskByTaskId(delegateTask.getId());

        if (humanTaskDto == null) {
            return;
        }

        if (!"complete".equals(humanTaskDto.getStatus())) {
            humanTaskDto.setStatus("delete");
            humanTaskDto.setCompleteTime(new Date());
            humanTaskDto.setAction("人工终止");
            humanTaskDto.setOwner(humanTaskDto.getAssignee());
            humanTaskDto.setAssignee(Authentication.getAuthenticatedUserId());
            humanTaskConnector.saveHumanTask(humanTaskDto, false);
        }
    }

    /**
     * 是否会签任务.
     */
    public boolean isVote(DelegateTask delegateTask) {
        ExecutionEntity executionEntity = (ExecutionEntity) delegateTask
                .getExecution();
        ActivityImpl activityImpl = executionEntity.getActivity();

        return activityImpl.getProperty("multiInstance") != null;
    }

    public HumanTaskDTO createHumanTask(DelegateTask delegateTask)
            throws Exception {
        HumanTaskDTO humanTaskDto = new HumanTaskBuilder()
                .setDelegateTask(delegateTask)
                .setVote(this.isVote(delegateTask)).build();

        humanTaskDto = humanTaskConnector.saveHumanTask(humanTaskDto);
        logger.debug("candidates : {}", delegateTask.getCandidates());

        for (IdentityLink identityLink : delegateTask.getCandidates()) {
            String type = identityLink.getType();
            ParticipantDTO participantDto = new ParticipantDTO();
            participantDto.setType(type);
            participantDto.setHumanTaskId(humanTaskDto.getId());

            if ("user".equals(type)) {
                participantDto.setCode(identityLink.getUserId());
            } else {
                participantDto.setCode(identityLink.getGroupId());
            }

            humanTaskConnector.saveParticipant(participantDto);
        }

        return humanTaskDto;
    }

    public void checkCopyHumanTask(DelegateTask delegateTask,
            HumanTaskDTO humanTaskDto) throws Exception {
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
                    if (bpmConfUser.getType() == TYPE_COPY) {
                        logger.info("copy humantask : {}, {}",
                                humanTaskDto.getId(), value);
                        this.copyHumanTask(humanTaskDto, value);
                    }
                }
            }
        } catch (Exception ex) {
            logger.debug(ex.getMessage(), ex);
        }
    }

    public void copyHumanTask(HumanTaskDTO humanTaskDto, String userId) {
        // 创建新任务
        HumanTaskDTO target = new HumanTaskDTO();
        beanMapper.copy(humanTaskDto, target);
        target.setId(null);
        target.setCategory("copy");
        target.setAssignee(userId);
        target.setCatalog(HumanTaskConstants.CATALOG_COPY);

        humanTaskConnector.saveHumanTask(target);
    }

    @Resource
    public void setHumanTaskConnector(HumanTaskConnector humanTaskConnector) {
        this.humanTaskConnector = humanTaskConnector;
    }

    @Resource
    public void setBpmConfUserManager(BpmConfUserManager bpmConfUserManager) {
        this.bpmConfUserManager = bpmConfUserManager;
    }
}
