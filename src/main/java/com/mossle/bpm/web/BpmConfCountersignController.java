package com.mossle.bpm.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.bpm.persistence.domain.BpmConfCountersign;
import com.mossle.bpm.persistence.domain.BpmConfNode;
import com.mossle.bpm.persistence.domain.BpmConfUser;
import com.mossle.bpm.persistence.domain.BpmProcess;
import com.mossle.bpm.persistence.manager.BpmConfCountersignManager;
import com.mossle.bpm.persistence.manager.BpmConfNodeManager;
import com.mossle.bpm.persistence.manager.BpmConfUserManager;
import com.mossle.bpm.persistence.manager.BpmProcessManager;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;

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
public class BpmConfCountersignController {
    private BpmConfNodeManager bpmConfNodeManager;
    private BpmConfUserManager bpmConfUserManager;
    private BeanMapper beanMapper = new BeanMapper();
    private ProcessEngine processEngine;
    private BpmProcessManager bpmProcessManager;
    private BpmConfCountersignManager bpmConfCountersignManager;

    @RequestMapping("bpm-conf-countersign-save")
    public String save(@ModelAttribute BpmConfCountersign bpmConfCountersign,
            @RequestParam("bpmConfNodeId") Long bpmConfNodeId) {
        BpmConfCountersign dest = bpmConfCountersignManager
                .get(bpmConfCountersign.getId());
        beanMapper.copy(bpmConfCountersign, dest);
        bpmConfCountersignManager.save(dest);

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
}
