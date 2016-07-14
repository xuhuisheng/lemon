package com.mossle.bpm.cmd;

import java.util.List;

import com.mossle.core.spring.ApplicationContextHelper;

import org.activiti.engine.impl.ExecutionQueryImpl;
import org.activiti.engine.impl.JobQueryImpl;
import org.activiti.engine.impl.db.DbSqlSession;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.HistoricProcessInstanceEntity;
import org.activiti.engine.impl.persistence.entity.HistoricProcessInstanceEntityManager;
import org.activiti.engine.impl.persistence.entity.IdentityLinkEntity;
import org.activiti.engine.impl.persistence.entity.IdentityLinkEntityManager;
import org.activiti.engine.impl.persistence.entity.JobEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntityManager;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.Job;

import org.springframework.jdbc.core.JdbcTemplate;

public class MigrateCmd implements Command<Void> {
    private String processInstanceId;
    private String processDefinitionId;

    public MigrateCmd(String processInstanceId, String processDefinitionId) {
        this.processInstanceId = processInstanceId;
        this.processDefinitionId = processDefinitionId;
    }

    public Void execute(CommandContext commandContext) {
        ExecutionEntity processInstance = commandContext
                .getExecutionEntityManager().findExecutionById(
                        processInstanceId);
        ProcessDefinitionEntity processDefinition = commandContext
                .getProcessDefinitionEntityManager().findProcessDefinitionById(
                        processDefinitionId);

        if (processDefinition.findActivity(processInstance
                .getCurrentActivityId()) == null) {
            throw new IllegalStateException(processDefinitionId
                    + " didnot contains "
                    + processInstance.getCurrentActivityId()
                    + ", cannot migrate");
        }

        // 操作修改运行表中的PROC_DEF_ID_字段的值
        String processInstanceId = processInstance.getId();
        String processDefinitionId = processDefinition.getId();
        DbSqlSession dbSqlSession = commandContext.getDbSqlSession();

        // 操作Task表
        TaskEntityManager taskEntityManager = commandContext
                .getTaskEntityManager();
        List<TaskEntity> tasks = taskEntityManager
                .findTasksByProcessInstanceId(processInstanceId);

        for (TaskEntity taskEntity : tasks) {
            taskEntity.setProcessDefinitionId(processDefinitionId);
            taskEntity.update();
        }

        // 操作Execution表
        List<Execution> executions = new ExecutionQueryImpl(commandContext)
                .processInstanceId(processInstanceId).list();

        for (Execution execution : executions) {
            if (execution instanceof ExecutionEntity) {
                ((ExecutionEntity) execution)
                        .setProcessDefinitionId(processDefinitionId);
                dbSqlSession.update((ExecutionEntity) execution);
            }
        }

        // 操作Job表
        List<Job> jobs = new JobQueryImpl(commandContext).processInstanceId(
                processInstanceId).list();

        for (Job job : jobs) {
            ((JobEntity) job).setProcessDefinitionId(processDefinitionId);
            dbSqlSession.update((JobEntity) job);
        }

        JdbcTemplate jdbcTemplate = ApplicationContextHelper
                .getBean(JdbcTemplate.class);

        // 操作Identitylink表
        IdentityLinkEntityManager identityLinkEntityManager = commandContext
                .getIdentityLinkEntityManager();
        List<IdentityLinkEntity> identityLinks = identityLinkEntityManager
                .findIdentityLinksByProcessInstanceId(processInstanceId);

        for (IdentityLinkEntity identityLinkEntity : identityLinks) {
            // identityLinkEntity.setProcessDefId(processDefinitionId);
            // dbSqlSession.update(identityLinkEntity);
            jdbcTemplate
                    .update("update ACT_RU_IDENTITYLINK set PROC_DEF_ID_=? where ID_=?",
                            processDefinitionId, identityLinkEntity.getId());
        }

        // 操作historicProcessInstance表
        HistoricProcessInstanceEntityManager historicProcessInstanceEntityManager = commandContext
                .getHistoricProcessInstanceEntityManager();
        HistoricProcessInstanceEntity historicProcessInstance = historicProcessInstanceEntityManager
                .findHistoricProcessInstance(processInstanceId);
        historicProcessInstance.setProcessDefinitionId(processDefinitionId);
        dbSqlSession.update(historicProcessInstance);

        return null;
    }
}
