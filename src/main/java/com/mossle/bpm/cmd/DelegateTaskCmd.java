package com.mossle.bpm.cmd;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

import com.mossle.bpm.delegate.DelegateService;
import com.mossle.bpm.graph.ActivitiHistoryGraphBuilder;
import com.mossle.bpm.graph.Edge;
import com.mossle.bpm.graph.Graph;
import com.mossle.bpm.graph.Node;

import com.mossle.core.spring.ApplicationContextHelper;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.HistoricActivityInstanceQueryImpl;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.HistoricActivityInstanceEntity;
import org.activiti.engine.impl.persistence.entity.HistoricTaskInstanceEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 委托任务.
 */
public class DelegateTaskCmd implements Command<Void> {
    private static Logger logger = LoggerFactory
            .getLogger(DelegateTaskCmd.class);
    private String taskId;
    private String attorney;

    public DelegateTaskCmd(String taskId, String attorney) {
        this.taskId = taskId;
        this.attorney = attorney;
    }

    /**
     * 委托任务.
     * 
     */
    public Void execute(CommandContext commandContext) {
        TaskEntity task = Context.getCommandContext().getTaskEntityManager()
                .findTaskById(taskId);

        String assignee = task.getAssignee();

        if (task.getOwner() == null) {
            task.setOwner(assignee);
        }

        task.setAssignee(attorney);
        ApplicationContextHelper.getBean(DelegateService.class).saveRecord(
                assignee, attorney, taskId);

        return null;
    }
}
