package com.mossle.spi.humantask;

import java.util.ArrayList;
import java.util.List;

public class TaskDefinitionDTO {
    public static final String CATALOG_ASSIGNEE = "assignee";
    public static final String CATALOG_CANDIDATE = "candidate";
    public static final String CATALOG_NOTIFICATION = "notification";
    public static final String TYPE_USER = "user";
    public static final String TYPE_GROUP = "group";
    private String id;
    private String code;
    private String name;
    private String assignStrategy;
    private FormDTO form;
    private CounterSignDTO counterSign;
    private List<String> operations = new ArrayList<String>();
    private List<TaskUserDTO> taskUsers = new ArrayList<TaskUserDTO>();
    private List<DeadlineDTO> deadlines = new ArrayList<DeadlineDTO>();
    private String processDefinitionId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAssignStrategy() {
        return assignStrategy;
    }

    public void setAssignStrategy(String assignStrategy) {
        this.assignStrategy = assignStrategy;
    }

    public FormDTO getForm() {
        return form;
    }

    public void setForm(FormDTO form) {
        this.form = form;
    }

    public CounterSignDTO getCounterSign() {
        return counterSign;
    }

    public void setCounterSign(CounterSignDTO counterSign) {
        this.counterSign = counterSign;
    }

    public List<String> getOperations() {
        return operations;
    }

    public void setOperations(List<String> operations) {
        this.operations = operations;
    }

    public List<TaskUserDTO> getTaskUsers() {
        return taskUsers;
    }

    public void setTaskUsers(List<TaskUserDTO> taskUsers) {
        this.taskUsers = taskUsers;
    }

    public List<DeadlineDTO> getDeadlines() {
        return deadlines;
    }

    public void setDeadlines(List<DeadlineDTO> deadlines) {
        this.deadlines = deadlines;
    }

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public void setAssignee(String assignee) {
        for (TaskUserDTO taskUser : taskUsers) {
            if (CATALOG_ASSIGNEE.equals(taskUser.getCatalog())) {
                taskUser.setValue(assignee);

                return;
            }
        }

        TaskUserDTO taskUser = new TaskUserDTO();
        taskUser.setCatalog(CATALOG_ASSIGNEE);
        taskUser.setType(TYPE_USER);
        taskUser.setValue(assignee);
        taskUsers.add(taskUser);
    }

    public void addCandidateUser(String candidateUser) {
        TaskUserDTO taskUser = new TaskUserDTO();
        taskUser.setCatalog(CATALOG_CANDIDATE);
        taskUser.setType(TYPE_USER);
        taskUser.setValue(candidateUser);
        taskUsers.add(taskUser);
    }

    public void addCandidateGroup(String candidateGroup) {
        TaskUserDTO taskUser = new TaskUserDTO();
        taskUser.setCatalog(CATALOG_CANDIDATE);
        taskUser.setType(TYPE_GROUP);
        taskUser.setValue(candidateGroup);
        taskUsers.add(taskUser);
    }
}
