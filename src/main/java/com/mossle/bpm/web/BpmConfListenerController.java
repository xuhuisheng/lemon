package com.mossle.bpm.web;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.bpm.persistence.domain.BpmConfListener;
import com.mossle.bpm.persistence.domain.BpmConfNode;
import com.mossle.bpm.persistence.manager.BpmConfListenerManager;
import com.mossle.bpm.persistence.manager.BpmConfNodeManager;
import com.mossle.bpm.persistence.manager.BpmProcessManager;

import com.mossle.core.mapper.BeanMapper;

import org.activiti.engine.ProcessEngine;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("bpm")
public class BpmConfListenerController {
    private BpmConfNodeManager bpmConfNodeManager;
    private BpmConfListenerManager bpmConfListenerManager;
    private BeanMapper beanMapper = new BeanMapper();
    private ProcessEngine processEngine;
    private BpmProcessManager bpmProcessManager;

    @RequestMapping("bpm-conf-listener-list")
    public String list(@RequestParam("bpmConfNodeId") Long bpmConfNodeId,
            Model model) {
        BpmConfNode bpmConfNode = bpmConfNodeManager.get(bpmConfNodeId);
        Long bpmConfBaseId = bpmConfNode.getBpmConfBase().getId();
        List<BpmConfListener> bpmConfListeners = bpmConfListenerManager.findBy(
                "bpmConfNode", bpmConfNode);
        model.addAttribute("bpmConfBaseId", bpmConfBaseId);
        model.addAttribute("bpmConfListeners", bpmConfListeners);

        return "bpm/bpm-conf-listener-list";
    }

    @RequestMapping("bpm-conf-listener-save")
    public String save(@RequestParam("bpmConfNodeId") Long bpmConfNodeId,
            @RequestParam("value") String value,
            @RequestParam("type") Integer type) {
        BpmConfNode bpmConfNode = bpmConfNodeManager.get(bpmConfNodeId);
        BpmConfListener bpmConfListener = new BpmConfListener();
        bpmConfListener.setBpmConfNode(bpmConfNode);
        bpmConfListener.setValue(value);
        bpmConfListener.setType(type);
        bpmConfListener.setStatus(1);
        bpmConfListener.setPriority(0);
        bpmConfListenerManager.save(bpmConfListener);

        return "redirect:/bpm/bpm-conf-listener-list.do?bpmConfNodeId="
                + bpmConfNodeId;
    }

    @RequestMapping("bpm-conf-listener-remove")
    public String remove(@RequestParam("bpmConfNodeId") Long bpmConfNodeId,
            @RequestParam("id") Long id) {
        bpmConfListenerManager.removeById(id);

        return "redirect:/bpm/bpm-conf-listener-list.do?bpmConfNodeId="
                + bpmConfNodeId;
    }

    // ~ ======================================================================
    @Resource
    public void setBpmConfNodeManager(BpmConfNodeManager bpmConfNodeManager) {
        this.bpmConfNodeManager = bpmConfNodeManager;
    }

    @Resource
    public void setBpmConfListenerManager(
            BpmConfListenerManager bpmConfListenerManager) {
        this.bpmConfListenerManager = bpmConfListenerManager;
    }

    @Resource
    public void setBpmProcessManager(BpmProcessManager bpmProcessManager) {
        this.bpmProcessManager = bpmProcessManager;
    }

    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }
}
