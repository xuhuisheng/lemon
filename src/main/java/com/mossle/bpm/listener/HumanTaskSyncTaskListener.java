package com.mossle.bpm.listener;

import javax.annotation.Resource;

import com.mossle.api.humantask.HumanTaskConnector;
import com.mossle.api.humantask.HumanTaskDTO;

import com.mossle.bpm.support.DefaultTaskListener;

import org.activiti.engine.delegate.DelegateTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HumanTaskSyncTaskListener extends DefaultTaskListener {
    public static final int TYPE_COPY = 3;
    private static Logger logger = LoggerFactory
            .getLogger(HumanTaskSyncTaskListener.class);
    private HumanTaskConnector humanTaskConnector;

    @Override
    public void onCreate(DelegateTask delegateTask) throws Exception {
        HumanTaskDTO humanTaskDto = humanTaskConnector
                .findHumanTaskByTaskId(delegateTask.getId());
        delegateTask.setOwner(humanTaskDto.getOwner());
        delegateTask.setAssignee(humanTaskDto.getAssignee());
    }

    @Resource
    public void setHumanTaskConnector(HumanTaskConnector humanTaskConnector) {
        this.humanTaskConnector = humanTaskConnector;
    }
}
