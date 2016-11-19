package com.mossle.humantask.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.humantask.persistence.domain.TaskConfUser;
import com.mossle.humantask.persistence.domain.TaskDefBase;
import com.mossle.humantask.persistence.domain.TaskDefDeadline;
import com.mossle.humantask.persistence.domain.TaskDefNotification;
import com.mossle.humantask.persistence.domain.TaskDefOperation;
import com.mossle.humantask.persistence.domain.TaskDefUser;
import com.mossle.humantask.persistence.manager.TaskConfUserManager;
import com.mossle.humantask.persistence.manager.TaskDefBaseManager;
import com.mossle.humantask.persistence.manager.TaskDefDeadlineManager;
import com.mossle.humantask.persistence.manager.TaskDefNotificationManager;
import com.mossle.humantask.persistence.manager.TaskDefOperationManager;
import com.mossle.humantask.persistence.manager.TaskDefUserManager;

import com.mossle.spi.humantask.CounterSignDTO;
import com.mossle.spi.humantask.DeadlineDTO;
import com.mossle.spi.humantask.FormDTO;
import com.mossle.spi.humantask.TaskDefinitionConnector;
import com.mossle.spi.humantask.TaskDefinitionDTO;
import com.mossle.spi.humantask.TaskNotificationDTO;
import com.mossle.spi.humantask.TaskUserDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskDefinitionConnectorImpl implements TaskDefinitionConnector {
    private static Logger logger = LoggerFactory
            .getLogger(TaskDefinitionConnectorImpl.class);
    private TaskDefBaseManager taskDefBaseManager;
    private TaskDefOperationManager taskDefOperationManager;
    private TaskDefUserManager taskDefUserManager;
    private TaskConfUserManager taskConfUserManager;
    private TaskDefNotificationManager taskDefNotificationManager;
    private TaskDefDeadlineManager taskDefDeadlineManager;

    /**
     * 分配策略.
     */
    public String findTaskAssignStrategy(String taskDefinitionKey,
            String processDefinitionId) {
        String hql = "from TaskDefBase where code=? and processDefinitionId=?";
        TaskDefBase taskDefBase = taskDefBaseManager.findUnique(hql,
                taskDefinitionKey, processDefinitionId);

        if (taskDefBase == null) {
            return null;
        }

        return taskDefBase.getAssignStrategy();
    }

    /**
     * 会签.
     */
    public CounterSignDTO findCounterSign(String taskDefinitionKey,
            String processDefinitionId) {
        String hql = "from TaskDefBase where code=? and processDefinitionId=?";
        TaskDefBase taskDefBase = taskDefBaseManager.findUnique(hql,
                taskDefinitionKey, processDefinitionId);

        if (taskDefBase == null) {
            return null;
        }

        CounterSignDTO counterSignDto = new CounterSignDTO();
        counterSignDto.setType(taskDefBase.getCountersignType());
        counterSignDto.setParticipants(taskDefBase.getCountersignUser());
        counterSignDto.setStrategy(taskDefBase.getCountersignStrategy());
        counterSignDto.setRate(taskDefBase.getCountersignRate());

        return counterSignDto;
    }

    /**
     * 表单.
     */
    public FormDTO findForm(String taskDefinitionKey, String processDefinitionId) {
        String hql = "from TaskDefBase where code=? and processDefinitionId=?";
        TaskDefBase taskDefBase = taskDefBaseManager.findUnique(hql,
                taskDefinitionKey, processDefinitionId);

        if (taskDefBase == null) {
            return null;
        }

        if (taskDefBase.getFormType() == null) {
            return null;
        }

        FormDTO formDto = new FormDTO();
        formDto.setType(taskDefBase.getFormType());
        formDto.setKey(taskDefBase.getFormKey());

        return formDto;
    }

    /**
     * 操作.
     */
    public List<String> findOperations(String taskDefinitionKey,
            String processDefinitionId) {
        String hql = "from TaskDefOperation where taskDefBase.code=? and taskDefBase.processDefinitionId=?";
        List<TaskDefOperation> taskDefOperations = taskDefOperationManager
                .find(hql, taskDefinitionKey, processDefinitionId);

        if (taskDefOperations.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> list = new ArrayList<String>();

        for (TaskDefOperation taskDefOperation : taskDefOperations) {
            list.add(taskDefOperation.getValue());
        }

        return list;
    }

    /**
     * 参与者.
     */
    public List<TaskUserDTO> findTaskUsers(String taskDefinitionKey,
            String processDefinitionId) {
        String hql = "from TaskDefUser where taskDefBase.code=? and taskDefBase.processDefinitionId=?";
        List<TaskDefUser> taskDefUsers = taskDefUserManager.find(hql,
                taskDefinitionKey, processDefinitionId);

        if (taskDefUsers.isEmpty()) {
            return Collections.emptyList();
        }

        List<TaskUserDTO> taskUserDtos = new ArrayList<TaskUserDTO>();

        for (TaskDefUser taskDefUser : taskDefUsers) {
            if ("disable".equals(taskDefUser.getStatus())) {
                continue;
            }

            TaskUserDTO taskUserDto = new TaskUserDTO();
            taskUserDto.setCatalog(taskDefUser.getCatalog());
            taskUserDto.setType(taskDefUser.getType());
            taskUserDto.setValue(taskDefUser.getValue());
            taskUserDtos.add(taskUserDto);
        }

        return taskUserDtos;
    }

    /**
     * 截止日期.
     */
    public List<DeadlineDTO> findDeadlines(String taskDefinitionKey,
            String processDefinitionId) {
        String hql = "from TaskDefDeadline where taskDefBase.code=? and taskDefBase.processDefinitionId=?";
        List<TaskDefDeadline> taskDefDeadlines = taskDefDeadlineManager.find(
                hql, taskDefinitionKey, processDefinitionId);

        if (taskDefDeadlines.isEmpty()) {
            return Collections.emptyList();
        }

        List<DeadlineDTO> deadlines = new ArrayList<DeadlineDTO>();

        for (TaskDefDeadline taskDefDeadline : taskDefDeadlines) {
            DeadlineDTO deadline = new DeadlineDTO();
            deadline.setType(taskDefDeadline.getType());
            deadline.setDuration(taskDefDeadline.getDuration());
            deadline.setNotificationType(taskDefDeadline.getNotificationType());
            deadline.setNotificationReceiver(taskDefDeadline
                    .getNotificationReceiver());
            deadline.setNotificationTemplateCode(taskDefDeadline
                    .getNotificationTemplateCode());
            deadlines.add(deadline);
        }

        return deadlines;
    }

    /**
     * 实例对应的参与者.
     */
    public String findTaskConfUser(String taskDefinitionKey, String businessKey) {
        String hql = "from TaskConfUser where code=? and businessKey=?";
        TaskConfUser taskConfUser = taskConfUserManager.findUnique(hql,
                taskDefinitionKey, businessKey);

        if (taskConfUser == null) {
            return null;
        }

        return taskConfUser.getValue();
    }

    /**
     * 提醒.
     */
    public List<TaskNotificationDTO> findTaskNotifications(
            String taskDefinitionKey, String processDefinitionId,
            String eventName) {
        String hql = "from TaskDefNotification where taskDefBase.code=? and taskDefBase.processDefinitionId=? and eventName=?";
        List<TaskDefNotification> taskDefNotifications = taskDefNotificationManager
                .find(hql, taskDefinitionKey, processDefinitionId, eventName);

        if (taskDefNotifications.isEmpty()) {
            return Collections.emptyList();
        }

        List<TaskNotificationDTO> taskNotifications = new ArrayList<TaskNotificationDTO>();

        for (TaskDefNotification taskDefNotification : taskDefNotifications) {
            TaskNotificationDTO taskNotification = new TaskNotificationDTO();
            taskNotification.setEventName(eventName);
            taskNotification.setType(taskDefNotification.getType());
            taskNotification.setReceiver(taskDefNotification.getReceiver());
            taskNotification.setTemplateCode(taskDefNotification
                    .getTemplateCode());
            taskNotifications.add(taskNotification);
        }

        return taskNotifications;
    }

    /**
     * 创建TaskDefinition.
     */
    public void create(TaskDefinitionDTO taskDefinition) {
        logger.info("create task definition : {}", taskDefinition.getCode());

        String hql = "from TaskDefBase where code=? and processDefinitionKey=? and processDefinitionVersion=?";
        String processDefinitionId = taskDefinition.getProcessDefinitionId();
        String processDefinitionKey = processDefinitionId.split("\\:")[0];
        int processDefinitionVersion = Integer.parseInt(processDefinitionId
                .split("\\:")[1]);
        TaskDefBase taskDefBase = taskDefBaseManager.findUnique(hql,
                taskDefinition.getCode(), processDefinitionKey,
                processDefinitionVersion);

        if (taskDefBase == null) {
            taskDefBase = new TaskDefBase();
            taskDefBase.setCode(taskDefinition.getCode());
            taskDefBase.setProcessDefinitionKey(processDefinitionKey);
            taskDefBase.setProcessDefinitionVersion(processDefinitionVersion);
        }

        if (taskDefBase.getProcessDefinitionId() == null) {
            taskDefBase.setProcessDefinitionId(processDefinitionId);
        }

        taskDefBase.setName(taskDefinition.getName());
        taskDefBase.setAssignStrategy(taskDefinition.getAssignStrategy());

        if (taskDefinition.getForm() != null) {
            taskDefBase.setFormType(taskDefinition.getForm().getType());
            taskDefBase.setFormKey(taskDefinition.getForm().getKey());
        }

        if (taskDefinition.getCounterSign() != null) {
            taskDefBase.setCountersignType(taskDefinition.getCounterSign()
                    .getType());
            taskDefBase.setCountersignUser(taskDefinition.getCounterSign()
                    .getParticipants());
            taskDefBase.setCountersignStrategy(taskDefinition.getCounterSign()
                    .getStrategy());
            taskDefBase.setCountersignRate(taskDefinition.getCounterSign()
                    .getRate());
        }

        taskDefBaseManager.save(taskDefBase);

        for (TaskUserDTO taskUser : taskDefinition.getTaskUsers()) {
            String value = taskUser.getValue();
            String type = taskUser.getType();
            String catalog = taskUser.getCatalog();
            String hqlFindTaskDefUser = "from TaskDefUser where taskDefBase=? and value=? and type=? and catalog=?";
            TaskDefUser taskDefUser = taskDefUserManager.findUnique(
                    hqlFindTaskDefUser, taskDefBase, value, type, catalog);

            if (taskDefUser != null) {
                continue;
            }

            taskDefUser = new TaskDefUser();
            taskDefUser.setType(type);
            taskDefUser.setCatalog(catalog);
            taskDefUser.setValue(value);
            taskDefUser.setTaskDefBase(taskDefBase);
            taskDefUserManager.save(taskDefUser);
        }
    }

    /**
     * 保存分配策略.
     */
    public void saveAssignStrategy(String taskDefinitionKey,
            String processDefinitionId, String assignStrategy) {
        String hql = "from TaskDefBase where code=? and processDefinitionId=?";
        TaskDefBase taskDefBase = taskDefBaseManager.findUnique(hql,
                taskDefinitionKey, processDefinitionId);

        if (taskDefBase == null) {
            return;
        }

        taskDefBase.setAssignStrategy(assignStrategy);
        taskDefBaseManager.save(taskDefBase);
    }

    /**
     * 保存会签.
     */
    public void saveCounterSign(String taskDefinitionKey,
            String processDefinitionId, CounterSignDTO counterSign) {
        String hql = "from TaskDefBase where code=? and processDefinitionId=?";
        TaskDefBase taskDefBase = taskDefBaseManager.findUnique(hql,
                taskDefinitionKey, processDefinitionId);

        if (taskDefBase == null) {
            return;
        }

        if (counterSign.getStrategy() != null) {
            taskDefBase.setCountersignStrategy(counterSign.getStrategy());
        }

        if (counterSign.getRate() != 0) {
            taskDefBase.setCountersignRate(counterSign.getRate());
        }

        taskDefBaseManager.save(taskDefBase);
    }

    /**
     * 保存表单.
     */
    public void saveForm(String taskDefinitionKey, String processDefinitionId,
            FormDTO form) {
        String hql = "from TaskDefBase where code=? and processDefinitionId=?";
        TaskDefBase taskDefBase = taskDefBaseManager.findUnique(hql,
                taskDefinitionKey, processDefinitionId);

        if (taskDefBase == null) {
            return;
        }

        if (form.getKey() != null) {
            taskDefBase.setFormKey(form.getKey());
        }

        if (form.getType() != null) {
            taskDefBase.setFormType(form.getType());
        }

        taskDefBaseManager.save(taskDefBase);
    }

    /**
     * 添加操作.
     */
    public void addOperation(String taskDefinitionKey,
            String processDefinitionId, String operation) {
        String hql = "from TaskDefBase where code=? and processDefinitionId=?";
        TaskDefBase taskDefBase = taskDefBaseManager.findUnique(hql,
                taskDefinitionKey, processDefinitionId);

        if (taskDefBase == null) {
            return;
        }

        String hqlOperation = "from TaskDefOperation where taskDefBase=? and value=?";
        TaskDefOperation taskDefOperation = taskDefOperationManager.findUnique(
                hqlOperation, taskDefBase, operation);

        if (taskDefOperation != null) {
            return;
        }

        taskDefOperation = new TaskDefOperation();
        taskDefOperation.setTaskDefBase(taskDefBase);
        taskDefOperation.setValue(operation);
        taskDefOperationManager.save(taskDefOperation);
    }

    /**
     * 删除操作
     */
    public void removeOperation(String taskDefinitionKey,
            String processDefinitionId, String operation) {
        String hql = "from TaskDefBase where code=? and processDefinitionId=?";
        TaskDefBase taskDefBase = taskDefBaseManager.findUnique(hql,
                taskDefinitionKey, processDefinitionId);

        if (taskDefBase == null) {
            return;
        }

        String hqlOperation = "from TaskDefOperation where taskDefBase=? and value=?";
        TaskDefOperation taskDefOperation = taskDefOperationManager.findUnique(
                hqlOperation, taskDefBase, operation);

        if (taskDefOperation == null) {
            return;
        }

        taskDefOperationManager.remove(taskDefOperation);
    }

    /**
     * 添加参与者.
     */
    public void addTaskUser(String taskDefinitionKey,
            String processDefinitionId, TaskUserDTO taskUser) {
        String hql = "from TaskDefBase where code=? and processDefinitionId=?";
        TaskDefBase taskDefBase = taskDefBaseManager.findUnique(hql,
                taskDefinitionKey, processDefinitionId);

        if (taskDefBase == null) {
            logger.info("cannot find taskDefBase {} {}", taskDefinitionKey,
                    processDefinitionId);

            return;
        }

        String hqlUser = "from TaskDefUser where taskDefBase=? and catalog=? and type=? and value=?";
        TaskDefUser taskDefUser = taskDefUserManager.findUnique(hqlUser,
                taskDefBase, taskUser.getCatalog(), taskUser.getType(),
                taskUser.getValue());

        if (taskDefUser != null) {
            logger.info("cannot find taskDefUser {} {} {} {}",
                    taskDefBase.getId(), taskUser.getCatalog(),
                    taskUser.getType(), taskUser.getValue());

            return;
        }

        taskDefUser = new TaskDefUser();
        taskDefUser.setTaskDefBase(taskDefBase);
        taskDefUser.setCatalog(taskUser.getCatalog());
        taskDefUser.setType(taskUser.getType());
        taskDefUser.setValue(taskUser.getValue());
        taskDefBaseManager.save(taskDefUser);
    }

    /**
     * 删除参与者.
     */
    public void removeTaskUser(String taskDefinitionKey,
            String processDefinitionId, TaskUserDTO taskUser) {
        String hql = "from TaskDefBase where code=? and processDefinitionId=?";
        TaskDefBase taskDefBase = taskDefBaseManager.findUnique(hql,
                taskDefinitionKey, processDefinitionId);

        if (taskDefBase == null) {
            logger.info("cannot find taskDefBase {} {}", taskDefinitionKey,
                    processDefinitionId);

            return;
        }

        String hqlUser = "from TaskDefUser where taskDefBase=? and catalog=? and type=? and value=?";
        TaskDefUser taskDefUser = taskDefUserManager.findUnique(hqlUser,
                taskDefBase, taskUser.getCatalog(), taskUser.getType(),
                taskUser.getValue());

        if (taskDefUser == null) {
            logger.info("cannot find taskDefUser {} {} {} {}",
                    taskDefBase.getId(), taskUser.getCatalog(),
                    taskUser.getType(), taskUser.getValue());

            return;
        }

        taskDefBaseManager.remove(taskDefUser);
    }

    /**
     * 更新参与者.
     */
    public void updateTaskUser(String taskDefinitionKey,
            String processDefinitionId, TaskUserDTO taskUser, String status) {
        String hql = "from TaskDefBase where code=? and processDefinitionId=?";
        TaskDefBase taskDefBase = taskDefBaseManager.findUnique(hql,
                taskDefinitionKey, processDefinitionId);

        if (taskDefBase == null) {
            logger.info("cannot find taskDefBase {} {}", taskDefinitionKey,
                    processDefinitionId);

            return;
        }

        String hqlUser = "from TaskDefUser where taskDefBase=? and catalog=? and type=? and value=?";
        TaskDefUser taskDefUser = taskDefUserManager.findUnique(hqlUser,
                taskDefBase, taskUser.getCatalog(), taskUser.getType(),
                taskUser.getValue());

        if (taskDefUser == null) {
            logger.info("cannot find taskDefUser {} {} {} {}",
                    taskDefBase.getId(), taskUser.getCatalog(),
                    taskUser.getType(), taskUser.getValue());

            return;
        }

        taskDefUser.setStatus(status);
        taskDefBaseManager.save(taskDefUser);
    }

    /**
     * 添加提醒.
     */
    public void addTaskNotification(String taskDefinitionKey,
            String processDefinitionId, TaskNotificationDTO taskNotification) {
        String hql = "from TaskDefBase where code=? and processDefinitionId=?";
        TaskDefBase taskDefBase = taskDefBaseManager.findUnique(hql,
                taskDefinitionKey, processDefinitionId);

        if (taskDefBase == null) {
            return;
        }

        String hqlNotification = "from TaskDefNotification where taskDefBase=? and eventName=? and templateCode=?";
        TaskDefNotification taskDefNotification = taskDefNotificationManager
                .findUnique(hqlNotification, taskDefBase,
                        taskNotification.getEventName(),
                        taskNotification.getTemplateCode());

        if (taskDefNotification == null) {
            taskDefNotification = new TaskDefNotification();
            taskDefNotification.setTaskDefBase(taskDefBase);
            taskDefNotification.setEventName(taskNotification.getEventName());
            taskDefNotification.setTemplateCode(taskNotification
                    .getTemplateCode());
            taskDefNotification.setType(taskNotification.getType());
        }

        taskDefNotification.setReceiver(taskNotification.getReceiver());
        taskDefNotification.setType(taskNotification.getType());
        taskDefNotificationManager.save(taskDefNotification);
    }

    /**
     * 删除提醒.
     */
    public void removeTaskNotification(String taskDefinitionKey,
            String processDefinitionId, TaskNotificationDTO taskNotification) {
        String hql = "from TaskDefBase where code=? and processDefinitionId=?";
        TaskDefBase taskDefBase = taskDefBaseManager.findUnique(hql,
                taskDefinitionKey, processDefinitionId);

        if (taskDefBase == null) {
            return;
        }

        String hqlNotification = "from TaskDefNotification where taskDefBase=? and eventName=? and templateCode=?";
        TaskDefNotification taskDefNotification = taskDefNotificationManager
                .findUnique(hqlNotification, taskDefBase,
                        taskNotification.getEventName(),
                        taskNotification.getTemplateCode());

        if (taskDefNotification == null) {
            return;
        }

        taskDefNotificationManager.remove(taskDefNotification);
    }

    /**
     * 添加截止日期.
     */
    public void addDeadline(String taskDefinitionKey,
            String processDefinitionId, DeadlineDTO deadline) {
        String hql = "from TaskDefBase where code=? and processDefinitionId=?";
        TaskDefBase taskDefBase = taskDefBaseManager.findUnique(hql,
                taskDefinitionKey, processDefinitionId);

        if (taskDefBase == null) {
            return;
        }

        String hqlDeadline = "from TaskDefDeadline where taskDefBase=? and type=? and duration=?";
        TaskDefDeadline taskDefDeadline = taskDefDeadlineManager.findUnique(
                hqlDeadline, taskDefBase, deadline.getType(),
                deadline.getDuration());

        if (taskDefDeadline == null) {
            taskDefDeadline = new TaskDefDeadline();
            taskDefDeadline.setTaskDefBase(taskDefBase);
            taskDefDeadline.setType(deadline.getType());
            taskDefDeadline.setDuration(deadline.getDuration());
        }

        taskDefDeadline.setNotificationType(deadline.getNotificationType());
        taskDefDeadline.setNotificationReceiver(deadline
                .getNotificationReceiver());
        taskDefDeadline.setNotificationTemplateCode(deadline
                .getNotificationTemplateCode());

        taskDefDeadlineManager.save(taskDefDeadline);
    }

    /**
     * 删除截止日期.
     */
    public void removeDeadline(String taskDefinitionKey,
            String processDefinitionId, DeadlineDTO deadline) {
        String hql = "from TaskDefBase where code=? and processDefinitionId=?";
        TaskDefBase taskDefBase = taskDefBaseManager.findUnique(hql,
                taskDefinitionKey, processDefinitionId);

        if (taskDefBase == null) {
            return;
        }

        String hqlDeadline = "from TaskDefDeadline where taskDefBase=? and type=? and duration=?";
        TaskDefDeadline taskDefDeadline = taskDefDeadlineManager.findUnique(
                hqlDeadline, taskDefBase, deadline.getType(),
                deadline.getDuration());

        if (taskDefDeadline == null) {
            return;
        }

        taskDefDeadlineManager.remove(taskDefDeadline);
    }

    @Resource
    public void setTaskDefBaseManager(TaskDefBaseManager taskDefBaseManager) {
        this.taskDefBaseManager = taskDefBaseManager;
    }

    @Resource
    public void setTaskDefOperationManager(
            TaskDefOperationManager taskDefOperationManager) {
        this.taskDefOperationManager = taskDefOperationManager;
    }

    @Resource
    public void setTaskDefUserManager(TaskDefUserManager taskDefUserManager) {
        this.taskDefUserManager = taskDefUserManager;
    }

    @Resource
    public void setTaskConfUserManager(TaskConfUserManager taskConfUserManager) {
        this.taskConfUserManager = taskConfUserManager;
    }

    @Resource
    public void setTaskDefNotificationManager(
            TaskDefNotificationManager taskDefNotificationManager) {
        this.taskDefNotificationManager = taskDefNotificationManager;
    }

    @Resource
    public void setTaskDefDeadlineManager(
            TaskDefDeadlineManager taskDefDeadlineManager) {
        this.taskDefDeadlineManager = taskDefDeadlineManager;
    }
}
