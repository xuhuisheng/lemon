package com.mossle.bpm.web;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.bpm.persistence.domain.BpmConfNode;
import com.mossle.bpm.persistence.domain.BpmConfRule;
import com.mossle.bpm.persistence.manager.BpmConfNodeManager;
import com.mossle.bpm.persistence.manager.BpmConfRuleManager;
import com.mossle.bpm.persistence.manager.BpmProcessManager;

import com.mossle.core.mapper.BeanMapper;

import org.activiti.engine.ProcessEngine;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("bpm")
public class BpmConfRuleController {
    private BpmConfNodeManager bpmConfNodeManager;
    private BpmConfRuleManager bpmConfRuleManager;
    private BeanMapper beanMapper = new BeanMapper();
    private ProcessEngine processEngine;
    private BpmProcessManager bpmProcessManager;

    @RequestMapping("bpm-conf-rule-list")
    public String list(@RequestParam("bpmConfNodeId") Long bpmConfNodeId,
            Model model) {
        BpmConfNode bpmConfNode = bpmConfNodeManager.get(bpmConfNodeId);
        Long bpmConfBaseId = bpmConfNode.getBpmConfBase().getId();
        List<BpmConfRule> bpmConfRules = bpmConfRuleManager.findBy(
                "bpmConfNode", bpmConfNode);

        model.addAttribute("bpmConfBaseId", bpmConfBaseId);
        model.addAttribute("bpmConfRules", bpmConfRules);

        return "bpm/bpm-conf-rule-list";
    }

    @RequestMapping("bpm-conf-rule-save")
    public String save(@ModelAttribute BpmConfRule bpmConfRule,
            @RequestParam("bpmConfNodeId") Long bpmConfNodeId) {
        if ((bpmConfRule.getValue() == null)
                || "".equals(bpmConfRule.getValue())) {
            return "redirect:/bpm/bpm-conf-rule-list.do?bpmConfNodeId="
                    + bpmConfNodeId;
        }

        bpmConfRule.setBpmConfNode(bpmConfNodeManager.get(bpmConfNodeId));
        bpmConfRuleManager.save(bpmConfRule);

        return "redirect:/bpm/bpm-conf-rule-list.do?bpmConfNodeId="
                + bpmConfNodeId;
    }

    @RequestMapping("bpm-conf-rule-remove")
    public String remove(@RequestParam("id") Long id) {
        BpmConfRule bpmConfRule = bpmConfRuleManager.get(id);
        Long bpmConfNodeId = bpmConfRule.getBpmConfNode().getId();
        bpmConfRuleManager.remove(bpmConfRule);

        return "redirect:/bpm/bpm-conf-rule-list.do?bpmConfNodeId="
                + bpmConfNodeId;
    }

    // ~ ======================================================================
    @Resource
    public void setBpmConfNodeManager(BpmConfNodeManager bpmConfNodeManager) {
        this.bpmConfNodeManager = bpmConfNodeManager;
    }

    @Resource
    public void setBpmConfRuleManager(BpmConfRuleManager bpmConfRuleManager) {
        this.bpmConfRuleManager = bpmConfRuleManager;
    }

    @Resource
    public void setBpmProcessManager(BpmProcessManager bpmProcessManager) {
        this.bpmProcessManager = bpmProcessManager;
    }

    @Resource
    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }
}
