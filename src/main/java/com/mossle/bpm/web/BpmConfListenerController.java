package com.mossle.bpm.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.bpm.persistence.domain.BpmConfListener;
import com.mossle.bpm.persistence.domain.BpmConfNode;
import com.mossle.bpm.persistence.domain.BpmProcess;
import com.mossle.bpm.persistence.manager.BpmConfListenerManager;
import com.mossle.bpm.persistence.manager.BpmConfNodeManager;
import com.mossle.bpm.persistence.manager.BpmProcessManager;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.repository.ProcessDefinition;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
