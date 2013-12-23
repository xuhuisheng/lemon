package com.mossle.bpm.listener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import com.mossle.bpm.persistence.domain.*;
import com.mossle.bpm.persistence.manager.*;
import com.mossle.bpm.support.DefaultTaskListener;

import com.mossle.core.mail.MailService;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.el.ExpressionManager;
import org.activiti.engine.impl.persistence.entity.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * <p>
 * 任务到达提醒：xx您好，您有新任务需要处理。
 * </p>
 * <p>
 * 任务超时提醒：xx您好，您的任务还有xx时间即将过期，请尽快处理。
 * </p>
 * <p>
 * 提醒起草人：xx您好，您的流程已经到达xx环节，预计处理需要xx时间。
 * </p>
 * <p>
 * 提醒关键岗位：xx您好，xx任务已经交由xx处理，请知晓。
 * </p>
 * 
 * <p>
 * 超时提醒不是这个Listener里能判断的。
 * </p>
 */
public class NoticeTaskListener extends DefaultTaskListener {
    private static Logger logger = LoggerFactory
            .getLogger(NoticeTaskListener.class);
    public static final int TYPE_ARRIVAL = 0;
    public static final int TYPE_COMPLETE = 1;
    public static final int TYPE_TIMEOUT = 2;
    private MailService mailService;
    private BpmProcessManager bpmProcessManager;
    private BpmTaskDefNoticeManager bpmTaskDefNoticeManager;

    @Override
    public void onCreate(DelegateTask delegateTask) throws Exception {
        String taskDefinitionKey = delegateTask.getTaskDefinitionKey();
        String processDefinitionId = delegateTask.getExecution()
                .getProcessDefinitionId();
        ProcessDefinitionEntity processDefinitionEntity = Context
                .getCommandContext().getProcessDefinitionEntityManager()
                .findProcessDefinitionById(processDefinitionId);
        BpmProcess bpmProcess = bpmProcessManager
                .findUnique(
                        "from BpmProcess where processDefinitionKey=? and processDefinitionVersion=?",
                        processDefinitionEntity.getKey(),
                        processDefinitionEntity.getVersion());
        List<BpmTaskDefNotice> bpmTaskDefNotices = bpmTaskDefNoticeManager
                .find("from BpmTaskDefNotice where taskDefinitionKey=? and bpmProcess=?",
                        taskDefinitionKey, bpmProcess);

        for (BpmTaskDefNotice bpmTaskDefNotice : bpmTaskDefNotices) {
            if (TYPE_ARRIVAL == bpmTaskDefNotice.getType()) {
                processArrival(delegateTask, bpmTaskDefNotice);
            } else if (TYPE_COMPLETE == bpmTaskDefNotice.getType()) {
                // do nothing
            } else if (TYPE_TIMEOUT == bpmTaskDefNotice.getType()) {
                processTimeout(delegateTask, bpmTaskDefNotice);
            } else {
                logger.warn("unknow type : {}, bpmTaskDefNotice.id : {}",
                        bpmTaskDefNotice.getType(), bpmTaskDefNotice.getId());
            }
        }
    }

    @Override
    public void onComplete(DelegateTask delegateTask) throws Exception {
        String taskDefinitionKey = delegateTask.getTaskDefinitionKey();
        String processDefinitionId = delegateTask.getExecution()
                .getProcessDefinitionId();
        ProcessDefinitionEntity processDefinitionEntity = Context
                .getCommandContext().getProcessDefinitionEntityManager()
                .findProcessDefinitionById(processDefinitionId);
        BpmProcess bpmProcess = bpmProcessManager
                .findUnique(
                        "from BpmProcess where processDefinitionKey=? and processDefinitionVersion=?",
                        processDefinitionEntity.getKey(),
                        processDefinitionEntity.getVersion());
        List<BpmTaskDefNotice> bpmTaskDefNotices = bpmTaskDefNoticeManager
                .find("from BpmTaskDefNotice where taskDefinitionKey=? and bpmProcess=?",
                        taskDefinitionKey, bpmProcess);

        for (BpmTaskDefNotice bpmTaskDefNotice : bpmTaskDefNotices) {
            if (TYPE_ARRIVAL == bpmTaskDefNotice.getType()) {
                // do nothing
            } else if (TYPE_COMPLETE == bpmTaskDefNotice.getType()) {
                processComplete(delegateTask, bpmTaskDefNotice);
            } else if (TYPE_TIMEOUT == bpmTaskDefNotice.getType()) {
                // do nothing
            } else {
                logger.warn("unknow type : {}, bpmTaskDefNotice.id : {}",
                        bpmTaskDefNotice.getType(), bpmTaskDefNotice.getId());
            }
        }
    }

    public void processArrival(DelegateTask delegateTask,
            BpmTaskDefNotice bpmTaskDefNotice) {
        String receiver = bpmTaskDefNotice.getReceiver();
        String template = bpmTaskDefNotice.getTemplate();
        ExpressionManager expressionManager = Context
                .getProcessEngineConfiguration().getExpressionManager();

        if ("任务接收人".equals(receiver)) {
            String email = delegateTask.getAssignee() + "@gmail.com";
            String subject = "您有新任务需要处理";

            // String content = "您有新任务需要处理" + delegateTask.getName();
            String content = expressionManager.createExpression(template)
                    .getValue(delegateTask).toString();
            mailService.send(email, subject, content);
        } else if ("流程发起人".equals(receiver)) {
            String email = delegateTask.getAssignee() + "@gmail.com";
            String subject = "您的流程已经到达" + delegateTask.getName() + "环节";

            // String content = "您有新任务需要处理" + delegateTask.getName();
            String content = expressionManager.createExpression(template)
                    .getValue(delegateTask).toString();
            mailService.send(email, subject, content);
        } else {
            HistoricProcessInstanceEntity historicProcessInstanceEntity = Context
                    .getCommandContext()
                    .getHistoricProcessInstanceEntityManager()
                    .findHistoricProcessInstance(
                            delegateTask.getProcessInstanceId());
            String email = historicProcessInstanceEntity.getStartUserId()
                    + "@gmail.com";
            String subject = "有新任务已经到达" + delegateTask.getName() + "环节";

            // String content = "您有新任务需要处理" + delegateTask.getName();
            String content = expressionManager.createExpression(template)
                    .getValue(delegateTask).toString();
            mailService.send(email, subject, content);
        }
    }

    public void processComplete(DelegateTask delegateTask,
            BpmTaskDefNotice bpmTaskDefNotice) {
        String receiver = bpmTaskDefNotice.getReceiver();
        String template = bpmTaskDefNotice.getTemplate();
        ExpressionManager expressionManager = Context
                .getProcessEngineConfiguration().getExpressionManager();

        String email = receiver + "@gmail.com";
        String subject = "任务已完成";

        String content = expressionManager.createExpression(template)
                .getValue(delegateTask).toString();
        mailService.send(email, subject, content);
    }

    public void processTimeout(DelegateTask delegateTask,
            BpmTaskDefNotice bpmTaskDefNotice) {
    }

    @Resource
    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    @Resource
    public void setBpmProcessManager(BpmProcessManager bpmProcessManager) {
        this.bpmProcessManager = bpmProcessManager;
    }

    @Resource
    public void setBpmTaskDefNoticeManager(
            BpmTaskDefNoticeManager bpmTaskDefNoticeManager) {
        this.bpmTaskDefNoticeManager = bpmTaskDefNoticeManager;
    }
}
