package com.mossle.bpm.web;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.bpm.persistence.domain.BpmConfForm;
import com.mossle.bpm.persistence.domain.BpmConfNode;
import com.mossle.bpm.persistence.manager.BpmConfFormManager;
import com.mossle.bpm.persistence.manager.BpmConfNodeManager;
import com.mossle.bpm.persistence.manager.BpmProcessManager;

import com.mossle.core.mapper.BeanMapper;

import com.mossle.spi.humantask.FormDTO;
import com.mossle.spi.humantask.TaskDefinitionConnector;

import org.activiti.engine.ProcessEngine;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("bpm")
public class BpmConfFormController {
    private BpmConfNodeManager bpmConfNodeManager;
    private BpmConfFormManager bpmConfFormManager;
    private BeanMapper beanMapper = new BeanMapper();
    private ProcessEngine processEngine;
    private BpmProcessManager bpmProcessManager;
    private TaskDefinitionConnector taskDefinitionConnector;

    @RequestMapping("bpm-conf-form-list")
    public String list(@RequestParam("bpmConfNodeId") Long bpmConfNodeId,
            Model model) {
        BpmConfNode bpmConfNode = bpmConfNodeManager.get(bpmConfNodeId);
        Long bpmConfBaseId = bpmConfNode.getBpmConfBase().getId();
        List<BpmConfForm> bpmConfForms = bpmConfFormManager.findBy(
                "bpmConfNode", bpmConfNode);
        model.addAttribute("bpmConfBaseId", bpmConfBaseId);
        model.addAttribute("bpmConfForms", bpmConfForms);

        return "bpm/bpm-conf-form-list";
    }

    @RequestMapping("bpm-conf-form-save")
    public String save(@ModelAttribute BpmConfForm bpmConfForm,
            @RequestParam("bpmConfNodeId") Long bpmConfNodeId) {
        BpmConfForm dest = bpmConfFormManager.findUnique(
                "from BpmConfForm where bpmConfNode.id=?", bpmConfNodeId);

        if (dest == null) {
            // 如果不存在，就创建一个
            dest = bpmConfForm;
            dest.setBpmConfNode(bpmConfNodeManager.get(bpmConfNodeId));
            dest.setStatus(1);
            bpmConfFormManager.save(dest);
        } else {
            dest.setValue(bpmConfForm.getValue());
            dest.setType(bpmConfForm.getType());
            dest.setStatus(1);
            bpmConfFormManager.save(dest);
        }

        String taskDefinitionKey = dest.getBpmConfNode().getCode();
        String processDefinitionId = dest.getBpmConfNode().getBpmConfBase()
                .getProcessDefinitionId();
        FormDTO form = new FormDTO();
        form.setType((bpmConfForm.getType() == 0) ? "internal" : "external");
        form.setKey(bpmConfForm.getValue());
        taskDefinitionConnector.saveForm(taskDefinitionKey,
                processDefinitionId, form);

        return "redirect:/bpm/bpm-conf-form-list.do?bpmConfNodeId="
                + bpmConfNodeId;
    }

    @RequestMapping("bpm-conf-form-remove")
    public String remove(@RequestParam("id") Long id) {
        BpmConfForm bpmConfForm = bpmConfFormManager.get(id);
        Long bpmConfNodeId = bpmConfForm.getBpmConfNode().getId();

        if (bpmConfForm.getStatus() == 0) {
            bpmConfForm.setStatus(2);
            bpmConfFormManager.save(bpmConfForm);
        } else if (bpmConfForm.getStatus() == 2) {
            bpmConfForm.setStatus(0);
            bpmConfFormManager.save(bpmConfForm);
        } else if (bpmConfForm.getStatus() == 1) {
            if (bpmConfForm.getOriginValue() == null) {
                bpmConfFormManager.remove(bpmConfForm);
            } else {
                bpmConfForm.setStatus(0);
                bpmConfForm.setValue(bpmConfForm.getOriginValue());
                bpmConfForm.setType(bpmConfForm.getOriginType());
                bpmConfFormManager.save(bpmConfForm);
            }
        }

        BpmConfForm dest = bpmConfForm;
        String taskDefinitionKey = dest.getBpmConfNode().getCode();
        String processDefinitionId = dest.getBpmConfNode().getBpmConfBase()
                .getProcessDefinitionId();
        FormDTO form = new FormDTO();
        form.setType((bpmConfForm.getType() == 0) ? "internal" : "external");
        form.setKey(bpmConfForm.getValue());
        taskDefinitionConnector.saveForm(taskDefinitionKey,
                processDefinitionId, form);

        return "redirect:/bpm/bpm-conf-form-list.do?bpmConfNodeId="
                + bpmConfNodeId;
    }

    // ~ ======================================================================
    @Resource
    public void setBpmConfNodeManager(BpmConfNodeManager bpmConfNodeManager) {
        this.bpmConfNodeManager = bpmConfNodeManager;
    }

    @Resource
    public void setBpmConfFormManager(BpmConfFormManager bpmConfFormManager) {
        this.bpmConfFormManager = bpmConfFormManager;
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
