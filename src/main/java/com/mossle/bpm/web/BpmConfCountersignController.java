package com.mossle.bpm.web;

import javax.annotation.Resource;

import com.mossle.bpm.persistence.domain.BpmConfCountersign;
import com.mossle.bpm.persistence.manager.BpmConfCountersignManager;
import com.mossle.bpm.persistence.manager.BpmConfNodeManager;
import com.mossle.bpm.persistence.manager.BpmConfUserManager;
import com.mossle.bpm.persistence.manager.BpmProcessManager;

import com.mossle.core.mapper.BeanMapper;

import com.mossle.spi.humantask.CounterSignDTO;
import com.mossle.spi.humantask.TaskDefinitionConnector;

import org.activiti.engine.ProcessEngine;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("bpm")
public class BpmConfCountersignController {
    private BpmConfNodeManager bpmConfNodeManager;
    private BpmConfUserManager bpmConfUserManager;
    private BeanMapper beanMapper = new BeanMapper();
    private ProcessEngine processEngine;
    private BpmProcessManager bpmProcessManager;
    private BpmConfCountersignManager bpmConfCountersignManager;
    private TaskDefinitionConnector taskDefinitionConnector;

    @RequestMapping("bpm-conf-countersign-save")
    public String save(@ModelAttribute BpmConfCountersign bpmConfCountersign,
            @RequestParam("bpmConfNodeId") Long bpmConfNodeId) {
        BpmConfCountersign dest = bpmConfCountersignManager
                .get(bpmConfCountersign.getId());
        beanMapper.copy(bpmConfCountersign, dest);
        bpmConfCountersignManager.save(dest);

        String taskDefinitionKey = dest.getBpmConfNode().getCode();
        String processDefinitionId = dest.getBpmConfNode().getBpmConfBase()
                .getProcessDefinitionId();
        CounterSignDTO counterSign = new CounterSignDTO();
        counterSign.setStrategy((dest.getType() == 0) ? "all" : "percent");
        counterSign.setRate(dest.getRate());
        taskDefinitionConnector.saveCounterSign(taskDefinitionKey,
                processDefinitionId, counterSign);

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
    public void setBpmConfCountersignManager(
            BpmConfCountersignManager bpmConfCountersignManager) {
        this.bpmConfCountersignManager = bpmConfCountersignManager;
    }

    @Resource
    public void setTaskDefinitionConnector(
            TaskDefinitionConnector taskDefinitionConnector) {
        this.taskDefinitionConnector = taskDefinitionConnector;
    }
}
