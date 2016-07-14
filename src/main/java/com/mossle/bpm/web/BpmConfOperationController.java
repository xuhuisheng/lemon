package com.mossle.bpm.web;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.bpm.persistence.domain.BpmConfNode;
import com.mossle.bpm.persistence.domain.BpmConfOperation;
import com.mossle.bpm.persistence.manager.BpmConfNodeManager;
import com.mossle.bpm.persistence.manager.BpmConfOperationManager;
import com.mossle.bpm.persistence.manager.BpmProcessManager;

import com.mossle.core.mapper.BeanMapper;

import com.mossle.spi.humantask.TaskDefinitionConnector;

import org.activiti.engine.ProcessEngine;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("bpm")
public class BpmConfOperationController {
    private BpmConfNodeManager bpmConfNodeManager;
    private BpmConfOperationManager bpmConfOperationManager;
    private BeanMapper beanMapper = new BeanMapper();
    private ProcessEngine processEngine;
    private BpmProcessManager bpmProcessManager;
    private TaskDefinitionConnector taskDefinitionConnector;

    @RequestMapping("bpm-conf-operation-list")
    public String list(@RequestParam("bpmConfNodeId") Long bpmConfNodeId,
            Model model) {
        List<String> operations = new ArrayList<String>();
        operations.add("saveDraft");
        operations.add("completeTask");
        operations.add("rollbackPrevious");
        operations.add("rollbackInitiator");
        operations.add("transfer");
        operations.add("delegateTask");
        operations.add("delegateTaskCreate");
        operations.add("communicate");
        operations.add("callback");
        operations.add("addCounterSign");

        BpmConfNode bpmConfNode = bpmConfNodeManager.get(bpmConfNodeId);
        Long bpmConfBaseId = bpmConfNode.getBpmConfBase().getId();
        List<BpmConfOperation> bpmConfOperations = bpmConfOperationManager
                .findBy("bpmConfNode", bpmConfNode);

        for (Iterator<String> iterator = operations.iterator(); iterator
                .hasNext();) {
            String value = iterator.next();

            for (BpmConfOperation bpmConfOperation : bpmConfOperations) {
                if (value.equals(bpmConfOperation.getValue())) {
                    iterator.remove();

                    break;
                }
            }
        }

        model.addAttribute("bpmConfBaseId", bpmConfBaseId);
        model.addAttribute("bpmConfOperations", bpmConfOperations);
        model.addAttribute("operations", operations);

        return "bpm/bpm-conf-operation-list";
    }

    @RequestMapping("bpm-conf-operation-save")
    public String save(@ModelAttribute BpmConfOperation bpmConfOperation,
            @RequestParam("bpmConfNodeId") Long bpmConfNodeId) {
        if ((bpmConfOperation.getValue() == null)
                || "".equals(bpmConfOperation.getValue())) {
            return "redirect:/bpm/bpm-conf-operation-list.do?bpmConfNodeId="
                    + bpmConfNodeId;
        }

        bpmConfOperation.setBpmConfNode(bpmConfNodeManager.get(bpmConfNodeId));
        bpmConfOperationManager.save(bpmConfOperation);

        BpmConfOperation dest = bpmConfOperation;
        String taskDefinitionKey = dest.getBpmConfNode().getCode();
        String processDefinitionId = dest.getBpmConfNode().getBpmConfBase()
                .getProcessDefinitionId();
        String operation = dest.getValue();
        taskDefinitionConnector.addOperation(taskDefinitionKey,
                processDefinitionId, operation);

        return "redirect:/bpm/bpm-conf-operation-list.do?bpmConfNodeId="
                + bpmConfNodeId;
    }

    @RequestMapping("bpm-conf-operation-remove")
    public String remove(@RequestParam("id") Long id) {
        BpmConfOperation bpmConfOperation = bpmConfOperationManager.get(id);
        Long bpmConfNodeId = bpmConfOperation.getBpmConfNode().getId();
        bpmConfOperationManager.remove(bpmConfOperation);

        BpmConfOperation dest = bpmConfOperation;
        String taskDefinitionKey = dest.getBpmConfNode().getCode();
        String processDefinitionId = dest.getBpmConfNode().getBpmConfBase()
                .getProcessDefinitionId();
        String operation = dest.getValue();
        taskDefinitionConnector.removeOperation(taskDefinitionKey,
                processDefinitionId, operation);

        return "redirect:/bpm/bpm-conf-operation-list.do?bpmConfNodeId="
                + bpmConfNodeId;
    }

    // ~ ======================================================================
    @Resource
    public void setBpmConfNodeManager(BpmConfNodeManager bpmConfNodeManager) {
        this.bpmConfNodeManager = bpmConfNodeManager;
    }

    @Resource
    public void setBpmConfOperationManager(
            BpmConfOperationManager bpmConfOperationManager) {
        this.bpmConfOperationManager = bpmConfOperationManager;
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
    public void setTaskDefinitionConnector(
            TaskDefinitionConnector taskDefinitionConnector) {
        this.taskDefinitionConnector = taskDefinitionConnector;
    }
}
