package com.mossle.bpm.behavior.usertask;

import com.mossle.bpm.behavior.ProcessEngineBeanFactory;

import org.activiti.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.activiti.engine.impl.task.TaskDefinition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

public class CustomUserTaskActivityBehavior extends UserTaskActivityBehavior {
    private static Logger log = LoggerFactory
            .getLogger(CustomUserTaskActivityBehavior.class);
    private JdbcTemplate jdbcTemplate; // 只是一个通过activiti引擎获取spring对象的例子

    public CustomUserTaskActivityBehavior(TaskDefinition taskDefinition) {
        super(taskDefinition);
        jdbcTemplate = ProcessEngineBeanFactory.getBean("jdbcTemplate");
    }

    public void setMultiInstanceActivityBehavior(
            MultiInstanceActivityBehavior multiInstanceActivityBehavior) {
        // TODO Auto-generated method stub
        log.info("MultiInstance usertask  -- {}", multiInstanceActivityBehavior);
        super.setMultiInstanceActivityBehavior(multiInstanceActivityBehavior);
        log.info("jdbcTemplate : {}", jdbcTemplate);
    }

    public void execute(ActivityExecution execution) throws Exception {
        log.info("{}:{} begin execute", execution.getCurrentActivityId(),
                execution.getCurrentActivityName());
        super.execute(execution);
        log.info("{}:{} after execute", execution.getCurrentActivityId(),
                execution.getCurrentActivityName());
    }

    // 自定义行为可以监控到各个元素的各种行为, 只需要覆盖相应方法即可
}
