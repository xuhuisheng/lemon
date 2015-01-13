package com.mossle.bridge.humantask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.form.FormDTO;
import com.mossle.api.humantask.HumanTaskConnector;
import com.mossle.api.humantask.HumanTaskDTO;
import com.mossle.api.humantask.HumanTaskDefinition;

import com.mossle.bpm.cmd.CompleteTaskWithCommentCmd;
import com.mossle.bpm.cmd.DeleteTaskWithCommentCmd;
import com.mossle.bpm.cmd.FindTaskDefinitionsCmd;
import com.mossle.bpm.cmd.RollbackTaskCmd;
import com.mossle.bpm.cmd.WithdrawTaskCmd;
import com.mossle.bpm.persistence.domain.BpmConfForm;
import com.mossle.bpm.persistence.domain.BpmConfOperation;
import com.mossle.bpm.persistence.domain.BpmTaskConf;
import com.mossle.bpm.persistence.manager.BpmConfFormManager;
import com.mossle.bpm.persistence.manager.BpmConfOperationManager;
import com.mossle.bpm.persistence.manager.BpmTaskConfManager;

import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;

import com.mossle.form.domain.FormTemplate;
import com.mossle.form.manager.FormTemplateManager;

import com.mossle.humantask.persistence.domain.HtHumantask;
import com.mossle.humantask.persistence.domain.HtParticipant;
import com.mossle.humantask.persistence.manager.HtHumantaskManager;
import com.mossle.humantask.persistence.manager.HtParticipantManager;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.Task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

public class HumanTaskConnectorImpl implements HumanTaskConnector {
    private Logger logger = LoggerFactory
            .getLogger(HumanTaskConnectorImpl.class);
    private ProcessEngine processEngine;
    private BpmConfOperationManager bpmConfOperationManager;
    private BpmConfFormManager bpmConfFormManager;
    private FormTemplateManager formTemplateManager;
    private BpmTaskConfManager bpmTaskConfManager;
    private JdbcTemplate jdbcTemplate;
    private HtHumantaskManager htHumantaskManager;
    private HtParticipantManager htParticipantManager;
    private BeanMapper beanMapper = new BeanMapper();

    public HumanTaskDTO createHumanTask() {
        return new HumanTaskDTO();
    }

    public void removeHumanTask(String humanTaskId) {
        HtHumantask htHumantask = htHumantaskManager.get(Long
                .parseLong(humanTaskId));
        htHumantaskManager.remove(htHumantask);
    }

    public void removeHumanTaskByTaskId(String taskId) {
        HtHumantask htHumantask = htHumantaskManager.findUniqueBy("taskId",
                taskId);
        htHumantaskManager.remove(htHumantask);
    }

    public void removeHumanTaskByProcessInstanceId(String processInstanceId) {
        String hql = "from HtHumantask where status='active' and processInstanceId=?";
        List<HtHumantask> htHumantasks = htHumantaskManager.find(hql,
                processInstanceId);

        for (HtHumantask htHumantask : htHumantasks) {
            htHumantaskManager.remove(htHumantask);
        }
    }

    public HumanTaskDTO saveHumanTask(HumanTaskDTO humanTaskDto) {
        Long id = null;

        if (humanTaskDto.getId() != null) {
            try {
                id = Long.parseLong(humanTaskDto.getId());
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }

        HtHumantask htHumantask = new HtHumantask();

        if (id != null) {
            htHumantask = htHumantaskManager.get(id);
        }

        beanMapper.copy(humanTaskDto, htHumantask);
        htHumantaskManager.save(htHumantask);
        humanTaskDto.setId(Long.toString(htHumantask.getId()));

        return humanTaskDto;
    }

    public HumanTaskDTO findHumanTaskByTaskId(String taskId) {
        HtHumantask htHumantask = htHumantaskManager.findUniqueBy("taskId",
                taskId);
        HumanTaskDTO humanTaskDto = new HumanTaskDTO();
        beanMapper.copy(htHumantask, humanTaskDto);

        return humanTaskDto;
    }

    public HumanTaskDTO findHumanTask(String humanTaskId) {
        HtHumantask htHumantask = htHumantaskManager.get(Long
                .parseLong(humanTaskId));

        return convertHumanTaskDto(htHumantask);
    }

    public FormDTO findTaskForm(String humanTaskId) {
        HumanTaskDTO humanTaskDto = this.findHumanTask(humanTaskId);

        FormDTO formDto = new FormDTO();
        formDto.setTaskId(humanTaskId);

        List<BpmConfOperation> bpmConfOperations = bpmConfOperationManager
                .find("from BpmConfOperation where bpmConfNode.bpmConfBase.processDefinitionId=? and bpmConfNode.code=?",
                        humanTaskDto.getProcessDefinitionId(),
                        humanTaskDto.getCode());

        for (BpmConfOperation bpmConfOperation : bpmConfOperations) {
            formDto.getButtons().add(bpmConfOperation.getValue());
        }

        String processDefinitionId = humanTaskDto.getProcessDefinitionId();
        String activityId = humanTaskDto.getCode();
        formDto.setProcessDefinitionId(processDefinitionId);
        formDto.setActivityId(activityId);

        List<BpmConfForm> bpmConfForms = bpmConfFormManager
                .find("from BpmConfForm where bpmConfNode.bpmConfBase.processDefinitionId=? and bpmConfNode.code=?",
                        processDefinitionId, activityId);

        if (!bpmConfForms.isEmpty()) {
            BpmConfForm bpmConfForm = bpmConfForms.get(0);

            if (!Integer.valueOf(2).equals(bpmConfForm.getStatus())) {
                // 外部表单
                if (Integer.valueOf(1).equals(bpmConfForm.getType())) {
                    formDto.setRedirect(true);
                    formDto.setUrl(bpmConfForm.getValue());
                } else {
                    formDto.setCode(bpmConfForm.getValue());
                }
            }
        }

        FormTemplate formTemplate = formTemplateManager.findUniqueBy("code",
                formDto.getCode());

        if (formTemplate == null) {
            logger.error("cannot find form : {}", formDto.getCode());

            return formDto;
        }

        if (Integer.valueOf(1).equals(formTemplate.getType())) {
            formDto.setRedirect(true);
            formDto.setUrl(formTemplate.getContent());
        } else {
            formDto.setContent(formTemplate.getContent());
        }

        return formDto;
    }

    public List<HumanTaskDefinition> findHumanTaskDefinitions(
            String processDefinitionId) {
        FindTaskDefinitionsCmd cmd = new FindTaskDefinitionsCmd(
                processDefinitionId);
        List<TaskDefinition> taskDefinitions = processEngine
                .getManagementService().executeCommand(cmd);

        List<HumanTaskDefinition> humanTaskDefinitions = new ArrayList<HumanTaskDefinition>();

        for (TaskDefinition taskDefinition : taskDefinitions) {
            HumanTaskDefinition humanTaskDefinition = new HumanTaskDefinition();
            humanTaskDefinition.setKey(taskDefinition.getKey());

            if (taskDefinition.getNameExpression() != null) {
                humanTaskDefinition.setName(taskDefinition.getNameExpression()
                        .getExpressionText());
            }

            if (taskDefinition.getAssigneeExpression() != null) {
                humanTaskDefinition.setAssignee(taskDefinition
                        .getAssigneeExpression().getExpressionText());
            }

            humanTaskDefinitions.add(humanTaskDefinition);
        }

        return humanTaskDefinitions;
    }

    public void configTaskDefinitions(String businessKey,
            List<String> taskDefinitionKeys, List<String> taskAssignees) {
        if (taskDefinitionKeys == null) {
            return;
        }

        // 如果是从配置任务负责人的页面过来，就保存TaskConf，再从草稿中得到数据启动流程
        int index = 0;

        for (String taskDefinitionKey : taskDefinitionKeys) {
            String taskAssignee = taskAssignees.get(index++);
            BpmTaskConf bpmTaskConf = new BpmTaskConf();
            bpmTaskConf.setBusinessKey(businessKey);
            bpmTaskConf.setTaskDefinitionKey(taskDefinitionKey);
            bpmTaskConf.setAssignee(taskAssignee);
            bpmTaskConfManager.save(bpmTaskConf);
        }
    }

    /**
     * 完成任务.
     */
    public void completeTask(String humanTaskId, String userId,
            Map<String, Object> taskParameters) {
        HumanTaskDTO humanTaskDto = findHumanTask(humanTaskId);

        if (humanTaskDto == null) {
            throw new IllegalStateException("任务不存在");
        }

        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery()
                .taskId(humanTaskDto.getTaskId()).singleResult();

        if (task == null) {
            throw new IllegalStateException("任务不存在");
        }

        // 处理抄送任务
        if ("copy".equals(humanTaskDto.getCategory())) {
            humanTaskDto.setStatus("complete");
            humanTaskDto.setCompleteTime(new Date());
            this.saveHumanTask(humanTaskDto);

            return;
        }

        // 先设置登录用户
        IdentityService identityService = processEngine.getIdentityService();
        identityService.setAuthenticatedUserId(userId);

        logger.info("{}", humanTaskDto.getDelegateStatus());

        // 处理协办任务
        if ("pending".equals(humanTaskDto.getDelegateStatus())) {
            humanTaskDto.setAssignee(humanTaskDto.getOwner());
            this.saveHumanTask(humanTaskDto);

            return;
        }

        // 处理委办任务
        if (DelegationState.PENDING == task.getDelegationState()) {
            taskService.resolveTask(humanTaskDto.getTaskId());

            return;
        }

        String taskId = humanTaskDto.getTaskId();

        // 处理子任务
        if ("subtask".equals(task.getCategory())) {
            processEngine.getManagementService()
                    .executeCommand(
                            new DeleteTaskWithCommentCmd(humanTaskDto
                                    .getTaskId(), "完成"));

            int count = jdbcTemplate.queryForObject(
                    "select count(*) from ACT_RU_TASK where PARENT_TASK_ID_=?",
                    Integer.class, task.getParentTaskId());

            if (count > 1) {
                return;
            }

            taskId = task.getParentTaskId();
        }

        processEngine.getManagementService().executeCommand(
                new CompleteTaskWithCommentCmd(taskId, taskParameters, "完成"));
    }

    /**
     * 待办任务.
     */
    public Page findPersonalTasks(String userId, int pageNo, int pageSize) {
        Page page = htHumantaskManager.pagedQuery(
                "from HtHumantask where assignee=? and status='active'",
                pageNo, pageSize, userId);
        List<HtHumantask> htHumantasks = (List<HtHumantask>) page.getResult();
        List<HumanTaskDTO> humanTaskDtos = this
                .convertHumanTaskDtos(htHumantasks);
        page.setResult(humanTaskDtos);

        return page;
    }

    /**
     * 已办任务.
     */
    public Page findFinishedTasks(String userId, int pageNo, int pageSize) {
        Page page = htHumantaskManager.pagedQuery(
                "from HtHumantask where assignee=? and status='complete'",
                pageNo, pageSize, userId);
        List<HtHumantask> htHumantasks = (List<HtHumantask>) page.getResult();
        List<HumanTaskDTO> humanTaskDtos = this
                .convertHumanTaskDtos(htHumantasks);
        page.setResult(humanTaskDtos);

        return page;
    }

    /**
     * 回退到上一步.
     */
    public void rollbackPrevious(String humanTaskId) {
        HumanTaskDTO humanTaskDto = findHumanTask(humanTaskId);

        if (humanTaskDto == null) {
            throw new IllegalStateException("任务不存在");
        }

        Command<Integer> cmd = new RollbackTaskCmd(humanTaskDto.getTaskId());

        processEngine.getManagementService().executeCommand(cmd);
    }

    /**
     * 撤销.
     */
    public void withdraw(String humanTaskId) {
        HumanTaskDTO humanTaskDto = findHumanTask(humanTaskId);

        if (humanTaskDto == null) {
            throw new IllegalStateException("任务不存在");
        }

        Command<Integer> cmd = new WithdrawTaskCmd(humanTaskDto.getTaskId());

        processEngine.getManagementService().executeCommand(cmd);
    }

    /**
     * 转办.
     */
    public void transfer(String humanTaskId, String userId) {
        HumanTaskDTO humanTaskDto = this.findHumanTask(humanTaskId);
        humanTaskDto.setOwner(humanTaskDto.getAssignee());
        humanTaskDto.setAssignee(userId);
        this.saveHumanTask(humanTaskDto);
        processEngine.getTaskService().setAssignee(humanTaskDto.getTaskId(),
                humanTaskDto.getAssignee());
        processEngine.getTaskService().setOwner(humanTaskDto.getTaskId(),
                humanTaskDto.getOwner());
    }

    /**
     * 协办.
     */
    public void delegateTask(String humanTaskId, String userId) {
        HumanTaskDTO humanTaskDto = this.findHumanTask(humanTaskId);
        humanTaskDto.setOwner(humanTaskDto.getAssignee());
        humanTaskDto.setAssignee(userId);
        humanTaskDto.setDelegateStatus("pending");
        this.saveHumanTask(humanTaskDto);
        processEngine.getTaskService().setAssignee(humanTaskDto.getTaskId(),
                humanTaskDto.getAssignee());
        processEngine.getTaskService().setOwner(humanTaskDto.getTaskId(),
                humanTaskDto.getOwner());
    }

    // ~ ==================================================
    public List<HumanTaskDTO> convertHumanTaskDtos(
            List<HtHumantask> htHumantasks) {
        List<HumanTaskDTO> humanTaskDtos = new ArrayList<HumanTaskDTO>();

        for (HtHumantask htHumantask : htHumantasks) {
            humanTaskDtos.add(convertHumanTaskDto(htHumantask));
        }

        return humanTaskDtos;
    }

    public HumanTaskDTO convertHumanTaskDto(HtHumantask htHumantask) {
        HumanTaskDTO humanTaskDto = new HumanTaskDTO();
        beanMapper.copy(htHumantask, humanTaskDto);

        return humanTaskDto;
    }

    // ~ ==================================================
    @Resource
    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    @Resource
    public void setBpmConfOperationManager(
            BpmConfOperationManager bpmConfOperationManager) {
        this.bpmConfOperationManager = bpmConfOperationManager;
    }

    @Resource
    public void setBpmConfFormManager(BpmConfFormManager bpmConfFormManager) {
        this.bpmConfFormManager = bpmConfFormManager;
    }

    @Resource
    public void setFormTemplateManager(FormTemplateManager formTemplateManager) {
        this.formTemplateManager = formTemplateManager;
    }

    @Resource
    public void setBpmTaskConfManager(BpmTaskConfManager bpmTaskConfManager) {
        this.bpmTaskConfManager = bpmTaskConfManager;
    }

    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Resource
    public void setHtHumantaskManager(HtHumantaskManager htHumantaskManager) {
        this.htHumantaskManager = htHumantaskManager;
    }

    @Resource
    public void setHtParticipantManager(
            HtParticipantManager htParticipantManager) {
        this.htParticipantManager = htParticipantManager;
    }
}
