package com.mossle.bpm.scheduler;

import java.util.List;

import javax.annotation.Resource;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.task.Task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskTimeoutJob {
    private static Logger logger = LoggerFactory
            .getLogger(TaskTimeoutJob.class);
    private ProcessEngine processEngine;

    public void execute() throws Exception {
        List<Task> tasks = processEngine.getTaskService().createTaskQuery()
                .list();

        for (Task task : tasks) {
            if ((task.getDueDate() != null)
                    && (task.getDueDate().getTime() < System
                            .currentTimeMillis())) {
                logger.info("task : {} is timeout.", task.getId());
            }
        }
    }

    @Resource
    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }
}
