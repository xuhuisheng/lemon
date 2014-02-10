package com.mossle.bpm.component;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.user.UserConnector;

import com.mossle.bpm.support.TaskDTO;

import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;

import org.springframework.stereotype.Component;

@Component
public class TaskConverter {
    private UserConnector userConnector;

    public List<TaskDTO> convertTasks(List<Task> tasks) {
        List<TaskDTO> taskDtos = new ArrayList<TaskDTO>();

        for (Task task : tasks) {
            TaskDTO taskDto = convertTask(task);
            taskDtos.add(taskDto);
        }

        return taskDtos;
    }

    public TaskDTO convertTask(Task task) {
        String userId = task.getAssignee();
        TaskDTO taskDto = new TaskDTO();
        taskDto.setId(task.getId());
        taskDto.setName(task.getName());
        taskDto.setUserId(userId);

        String username = userConnector.findById(task.getAssignee())
                .getDisplayName();

        if ((task.getOwner() != null)
                && (!task.getOwner().equals(task.getAssignee()))) {
            username += ("(原执行人："
                    + userConnector.findById(task.getOwner()).getDisplayName() + ")");
        }

        taskDto.setUsername(username);
        taskDto.setCreateTime(task.getCreateTime());
        taskDto.setSuspended(task.isSuspended());
        taskDto.setProcessInstanceId(task.getProcessInstanceId());
        taskDto.setExecutionId(task.getExecutionId());

        return taskDto;
    }

    public List<TaskDTO> convertHistoryTasks(
            List<HistoricTaskInstance> historicTaskInstances) {
        List<TaskDTO> taskDtos = new ArrayList<TaskDTO>();

        for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
            TaskDTO taskDto = convertHistoryTask(historicTaskInstance);
            taskDtos.add(taskDto);
        }

        return taskDtos;
    }

    public TaskDTO convertHistoryTask(HistoricTaskInstance historicTaskInstance) {
        TaskDTO taskDto = new TaskDTO();
        taskDto.setId(historicTaskInstance.getId());
        taskDto.setName(historicTaskInstance.getName());
        taskDto.setUserId(historicTaskInstance.getAssignee());

        String username = userConnector.findById(
                historicTaskInstance.getAssignee()).getDisplayName();

        if ((historicTaskInstance.getOwner() != null)
                && (!historicTaskInstance.getOwner().equals(
                        historicTaskInstance.getAssignee()))) {
            username += ("(原执行人："
                    + userConnector.findById(historicTaskInstance.getOwner())
                            .getDisplayName() + ")");
        }

        taskDto.setUsername(username);
        taskDto.setStartTime(historicTaskInstance.getStartTime());
        taskDto.setEndTime(historicTaskInstance.getEndTime());
        taskDto.setDeleteReason(historicTaskInstance.getDeleteReason());
        taskDto.setProcessInstanceId(historicTaskInstance
                .getProcessInstanceId());
        taskDto.setClaimTime(historicTaskInstance.getClaimTime());
        taskDto.setProcessDefinitionId(historicTaskInstance
                .getProcessDefinitionId());

        return taskDto;
    }

    @Resource
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }
}
