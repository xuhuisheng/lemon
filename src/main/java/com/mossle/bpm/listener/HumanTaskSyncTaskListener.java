package com.mossle.bpm.listener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import com.mossle.api.humantask.HumanTaskConnector;
import com.mossle.api.humantask.HumanTaskDTO;

import com.mossle.bpm.expr.Expr;
import com.mossle.bpm.expr.ExprProcessor;
import com.mossle.bpm.persistence.domain.BpmConfUser;
import com.mossle.bpm.persistence.manager.BpmConfUserManager;
import com.mossle.bpm.support.DefaultTaskListener;

import com.mossle.core.mapper.BeanMapper;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.el.ExpressionManager;
import org.activiti.engine.impl.persistence.entity.TaskEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

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
