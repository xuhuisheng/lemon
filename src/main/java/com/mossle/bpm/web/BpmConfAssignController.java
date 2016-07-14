package com.mossle.bpm.web;

import javax.annotation.Resource;

import com.mossle.bpm.persistence.domain.BpmConfAssign;
import com.mossle.bpm.persistence.manager.BpmConfAssignManager;
import com.mossle.bpm.persistence.manager.BpmConfNodeManager;
import com.mossle.bpm.persistence.manager.BpmConfUserManager;
import com.mossle.bpm.persistence.manager.BpmProcessManager;

import com.mossle.core.mapper.BeanMapper;

import com.mossle.spi.humantask.TaskDefinitionConnector;

import org.activiti.engine.ProcessEngine;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("bpm")
public class BpmConfAssignController {
    private BpmConfNodeManager bpmConfNodeManager;
    private BpmConfUserManager bpmConfUserManager;
    private BeanMapper beanMapper = new BeanMapper();
    private ProcessEngine processEngine;
    private BpmProcessManager bpmProcessManager;
    private BpmConfAssignManager bpmConfAssignManager;
    private TaskDefinitionConnector taskDefinitionConnector;

    @RequestMapping("bpm-conf-assign-save")
    public String save(@ModelAttribute BpmConfAssign bpmConfAssign,
            @RequestParam("bpmConfNodeId") Long bpmConfNodeId) {
        Long id = bpmConfAssign.getId();
        BpmConfAssign dest = null;

        if (id != null) {
            dest = bpmConfAssignManager.get(bpmConfAssign.getId());
        } else {
            dest = new BpmConfAssign();
            dest.setBpmConfNode(bpmConfNodeManager.get(bpmConfNodeId));
        }

        beanMapper.copy(bpmConfAssign, dest);
        bpmConfAssignManager.save(dest);

        String taskDefinitionKey = dest.getBpmConfNode().getCode();
        String processDefinitionId = dest.getBpmConfNode().getBpmConfBase()
                .getProcessDefinitionId();
        String assignStrategy = dest.getName();
        taskDefinitionConnector.saveAssignStrategy(taskDefinitionKey,
                processDefinitionId, assignStrategy);

        return "redirect:/bpm/bpm-conf-user-list.do?bpmConfNodeId="
                + bpmConfNodeId;
    }

    // ~ ======================================================================
    @Resource
    public void setBpmConfNodeManager(BpmConfNodeManager bpmConfNodeManager) {
        this.bpmConfNodeManager = bpmConfNodeManager;
    }

    @Resource
    public void setBpmConfUserManager(BpmConfUserManager bpmConfUserManager) {
        this.bpmConfUserManager = bpmConfUserManager;
    }

    @Resource
    public void setBpmProcessManager(BpmProcessManager bpmProcessManager) {
        this.bpmProcessManager = bpmProcessManager;
    }

    @Resource
    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    @Resource
    public void setBpmConfAssignManager(
            BpmConfAssignManager bpmConfAssignManager) {
        this.bpmConfAssignManager = bpmConfAssignManager;
    }

    @Resource
    public void setTaskDefinitionConnector(
            TaskDefinitionConnector taskDefinitionConnector) {
        this.taskDefinitionConnector = taskDefinitionConnector;
    }
}
