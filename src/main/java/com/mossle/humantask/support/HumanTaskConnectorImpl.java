package com.mossle.humantask.support;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.form.FormConnector;
import com.mossle.api.form.FormDTO;
import com.mossle.api.humantask.HumanTaskConnector;
import com.mossle.api.humantask.HumanTaskDTO;
import com.mossle.api.humantask.HumanTaskDefinition;
import com.mossle.api.humantask.ParticipantDTO;

import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;

import com.mossle.humantask.persistence.domain.TaskInfo;
import com.mossle.humantask.persistence.domain.TaskParticipant;
import com.mossle.humantask.persistence.manager.TaskInfoManager;
import com.mossle.humantask.persistence.manager.TaskParticipantManager;

import com.mossle.spi.process.InternalProcessConnector;
import com.mossle.spi.process.ProcessTaskDefinition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

public class HumanTaskConnectorImpl implements HumanTaskConnector {
    private Logger logger = LoggerFactory
            .getLogger(HumanTaskConnectorImpl.class);
    private JdbcTemplate jdbcTemplate;
    private TaskInfoManager taskInfoManager;
    private TaskParticipantManager taskParticipantManager;
    private InternalProcessConnector internalProcessConnector;
    private FormConnector formConnector;
    private BeanMapper beanMapper = new BeanMapper();

    // ~
    /**
     * 创建一个任务.
     */
    public HumanTaskDTO createHumanTask() {
        return new HumanTaskDTO();
    }

    // ~

    /**
     * 删除任务.
     */
    public void removeHumanTask(String humanTaskId) {
        TaskInfo taskInfo = taskInfoManager.get(Long.parseLong(humanTaskId));
        this.removeHumanTask(taskInfo);
    }

    public void removeHumanTaskByTaskId(String taskId) {
        TaskInfo taskInfo = taskInfoManager.findUniqueBy("taskId", taskId);
        this.removeHumanTask(taskInfo);
    }

    public void removeHumanTaskByProcessInstanceId(String processInstanceId) {
        String hql = "from TaskInfo where status='active' and processInstanceId=?";
        List<TaskInfo> taskInfos = taskInfoManager.find(hql, processInstanceId);

        for (TaskInfo taskInfo : taskInfos) {
            this.removeHumanTask(taskInfo);
        }
    }

    public void removeHumanTask(TaskInfo taskInfo) {
        taskInfoManager.remove(taskInfo);
    }

    // ~
    /**
     * 保存任务.
     */
    public HumanTaskDTO saveHumanTask(HumanTaskDTO humanTaskDto) {
        Long id = null;

        if (humanTaskDto.getId() != null) {
            try {
                id = Long.parseLong(humanTaskDto.getId());
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }

        TaskInfo taskInfo = new TaskInfo();

        if (id != null) {
            taskInfo = taskInfoManager.get(id);
        }

        beanMapper.copy(humanTaskDto, taskInfo);
        taskInfoManager.save(taskInfo);
        humanTaskDto.setId(Long.toString(taskInfo.getId()));

        return humanTaskDto;
    }

    public HumanTaskDTO findHumanTaskByTaskId(String taskId) {
        TaskInfo taskInfo = taskInfoManager.findUniqueBy("taskId", taskId);
        HumanTaskDTO humanTaskDto = new HumanTaskDTO();
        beanMapper.copy(taskInfo, humanTaskDto);

        return humanTaskDto;
    }

    public HumanTaskDTO findHumanTask(String humanTaskId) {
        TaskInfo taskInfo = taskInfoManager.get(Long.parseLong(humanTaskId));

        return convertHumanTaskDto(taskInfo);
    }

    /**
     * 获取任务表单.
     */
    public FormDTO findTaskForm(String humanTaskId) {
        HumanTaskDTO humanTaskDto = this.findHumanTask(humanTaskId);

        FormDTO formDto = internalProcessConnector.findTaskForm(humanTaskDto
                .getTaskId());
        formDto.setTaskId(humanTaskId);

        FormDTO contentFormDto = formConnector.findForm(formDto.getCode());

        if (contentFormDto == null) {
            logger.error("cannot find form : {}", formDto.getCode());

            return formDto;
        }

        formDto.setRedirect(contentFormDto.isRedirect());
        formDto.setUrl(contentFormDto.getUrl());
        formDto.setContent(contentFormDto.getContent());

        return formDto;
    }

    /**
     * 根据流程定义获得所有任务定义.
     */
    public List<HumanTaskDefinition> findHumanTaskDefinitions(
            String processDefinitionId) {
        List<ProcessTaskDefinition> processTaskDefinitions = internalProcessConnector
                .findTaskDefinitions(processDefinitionId);

        List<HumanTaskDefinition> humanTaskDefinitions = new ArrayList<HumanTaskDefinition>();

        for (ProcessTaskDefinition processTaskDefinition : processTaskDefinitions) {
            HumanTaskDefinition humanTaskDefinition = new HumanTaskDefinition();
            beanMapper.copy(processTaskDefinition, humanTaskDefinition);
            humanTaskDefinitions.add(humanTaskDefinition);
        }

        return humanTaskDefinitions;
    }

    public void configTaskDefinitions(String businessKey,
            List<String> taskDefinitionKeys, List<String> taskAssignees) {
        internalProcessConnector.configTaskDefinitions(businessKey,
                taskDefinitionKeys, taskAssignees);
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

        // 处理抄送任务
        if ("copy".equals(humanTaskDto.getCategory())) {
            humanTaskDto.setStatus("complete");
            humanTaskDto.setCompleteTime(new Date());
            this.saveHumanTask(humanTaskDto);

            return;
        }

        logger.debug("{}", humanTaskDto.getDelegateStatus());

        // 处理协办任务
        if ("pending".equals(humanTaskDto.getDelegateStatus())) {
            humanTaskDto.setAssignee(humanTaskDto.getOwner());
            humanTaskDto.setDelegateStatus("resolved");
            this.saveHumanTask(humanTaskDto);
            internalProcessConnector.resolveTask(humanTaskDto.getTaskId());

            return;
        }

        internalProcessConnector.completeTask(humanTaskDto.getTaskId(), userId,
                taskParameters);
    }

    /**
     * 待办任务.
     */
    public Page findPersonalTasks(String userId, int pageNo, int pageSize) {
        Page page = taskInfoManager.pagedQuery(
                "from TaskInfo where assignee=? and status='active'", pageNo,
                pageSize, userId);
        List<TaskInfo> taskInfos = (List<TaskInfo>) page.getResult();
        List<HumanTaskDTO> humanTaskDtos = this.convertHumanTaskDtos(taskInfos);
        page.setResult(humanTaskDtos);

        return page;
    }

    /**
     * 已办任务.
     */
    public Page findFinishedTasks(String userId, int pageNo, int pageSize) {
        Page page = taskInfoManager.pagedQuery(
                "from TaskInfo where assignee=? and status='complete'", pageNo,
                pageSize, userId);
        List<TaskInfo> taskInfos = (List<TaskInfo>) page.getResult();
        List<HumanTaskDTO> humanTaskDtos = this.convertHumanTaskDtos(taskInfos);
        page.setResult(humanTaskDtos);

        return page;
    }

    /**
     * 领取任务.
     */
    public void claimTask(String humanTaskId, String userId) {
        TaskInfo taskInfo = taskInfoManager.get(Integer.parseInt(humanTaskId));

        if (taskInfo.getAssignee() != null) {
            throw new IllegalStateException("task " + humanTaskId
                    + " already be claimed by " + taskInfo.getAssignee());
        }

        taskInfo.setAssignee(userId);
        taskInfoManager.save(taskInfo);
    }

    /**
     * 释放任务.
     */
    public void releaseTask(String humanTaskId) {
        TaskInfo taskInfo = taskInfoManager.get(Integer.parseInt(humanTaskId));

        taskInfo.setAssignee(null);
        taskInfoManager.save(taskInfo);
    }

    /**
     * 转办.
     */
    public void transfer(String humanTaskId, String userId) {
        HumanTaskDTO humanTaskDto = this.findHumanTask(humanTaskId);
        humanTaskDto.setOwner(humanTaskDto.getAssignee());
        humanTaskDto.setAssignee(userId);
        this.saveHumanTask(humanTaskDto);

        internalProcessConnector.transfer(humanTaskDto.getTaskId(),
                humanTaskDto.getAssignee(), humanTaskDto.getOwner());
    }

    /**
     * 回退，指定节点，重新分配.
     */
    public void rollbackActivity(String humanTaskId, String activityId) {
        HumanTaskDTO humanTaskDto = findHumanTask(humanTaskId);

        if (humanTaskDto == null) {
            throw new IllegalStateException("任务不存在");
        }

        String taskId = humanTaskDto.getTaskId();
        internalProcessConnector.rollback(taskId, activityId, null);
    }

    /**
     * 回退，指定节点，上个执行人.
     */
    public void rollbackActivityLast(String humanTaskId, String activityId) {
        HumanTaskDTO humanTaskDto = findHumanTask(humanTaskId);

        if (humanTaskDto == null) {
            throw new IllegalStateException("任务不存在");
        }

        String taskId = humanTaskDto.getTaskId();
        internalProcessConnector.rollbackAuto(taskId, activityId);
    }

    /**
     * 回退，指定节点，指定执行人.
     */
    public void rollbackActivityAssignee(String humanTaskId, String activityId,
            String userId) {
        HumanTaskDTO humanTaskDto = findHumanTask(humanTaskId);

        if (humanTaskDto == null) {
            throw new IllegalStateException("任务不存在");
        }

        String taskId = humanTaskDto.getTaskId();
        internalProcessConnector.rollback(taskId, activityId, userId);
    }

    /**
     * 回退，上个节点，重新分配.
     */
    public void rollbackPrevious(String humanTaskId) {
        HumanTaskDTO humanTaskDto = findHumanTask(humanTaskId);

        if (humanTaskDto == null) {
            throw new IllegalStateException("任务不存在");
        }

        String taskId = humanTaskDto.getTaskId();
        internalProcessConnector.rollback(taskId, null, null);
    }

    /**
     * 回退，上个节点，上个执行人.
     */
    public void rollbackPreviousLast(String humanTaskId) {
        HumanTaskDTO humanTaskDto = findHumanTask(humanTaskId);

        if (humanTaskDto == null) {
            throw new IllegalStateException("任务不存在");
        }

        String taskId = humanTaskDto.getTaskId();
        internalProcessConnector.rollbackAuto(taskId, null);
    }

    /**
     * 回退，上个节点，指定执行人.
     */
    public void rollbackPreviousAssignee(String humanTaskId, String userId) {
        HumanTaskDTO humanTaskDto = findHumanTask(humanTaskId);

        if (humanTaskDto == null) {
            throw new IllegalStateException("任务不存在");
        }

        String taskId = humanTaskDto.getTaskId();
        internalProcessConnector.rollback(taskId, null, userId);
    }

    /**
     * 撤销.
     */
    public void withdraw(String humanTaskId) {
        HumanTaskDTO humanTaskDto = findHumanTask(humanTaskId);

        if (humanTaskDto == null) {
            throw new IllegalStateException("任务不存在");
        }

        internalProcessConnector.withdrawTask(humanTaskDto.getTaskId());
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
        internalProcessConnector.delegateTask(humanTaskDto.getTaskId(), userId);
    }

    // ~ ==================================================
    public List<HumanTaskDTO> convertHumanTaskDtos(List<TaskInfo> taskInfos) {
        List<HumanTaskDTO> humanTaskDtos = new ArrayList<HumanTaskDTO>();

        for (TaskInfo taskInfo : taskInfos) {
            humanTaskDtos.add(convertHumanTaskDto(taskInfo));
        }

        return humanTaskDtos;
    }

    public HumanTaskDTO convertHumanTaskDto(TaskInfo taskInfo) {
        HumanTaskDTO humanTaskDto = new HumanTaskDTO();
        beanMapper.copy(taskInfo, humanTaskDto);

        return humanTaskDto;
    }

    public void saveParticipant(ParticipantDTO participantDto) {
        TaskParticipant taskParticipant = new TaskParticipant();
        taskParticipant.setRef(participantDto.getCode());
        taskParticipant.setType(participantDto.getType());
        taskParticipant.setTaskInfo(taskInfoManager.get(Long
                .parseLong(participantDto.getHumanTaskId())));
        taskParticipantManager.save(taskParticipant);
    }

    // ~ ==================================================
    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Resource
    public void setTaskInfoManager(TaskInfoManager taskInfoManager) {
        this.taskInfoManager = taskInfoManager;
    }

    @Resource
    public void setTaskParticipantManager(
            TaskParticipantManager taskParticipantManager) {
        this.taskParticipantManager = taskParticipantManager;
    }

    @Resource
    public void setInternalProcessConnector(
            InternalProcessConnector internalProcessConnector) {
        this.internalProcessConnector = internalProcessConnector;
    }

    @Resource
    public void setFormConnector(FormConnector formConnector) {
        this.formConnector = formConnector;
    }
}
