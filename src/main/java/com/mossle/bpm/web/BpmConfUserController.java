package com.mossle.bpm.web;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.bpm.persistence.domain.BpmConfAssign;
import com.mossle.bpm.persistence.domain.BpmConfCountersign;
import com.mossle.bpm.persistence.domain.BpmConfNode;
import com.mossle.bpm.persistence.domain.BpmConfUser;
import com.mossle.bpm.persistence.manager.BpmConfAssignManager;
import com.mossle.bpm.persistence.manager.BpmConfCountersignManager;
import com.mossle.bpm.persistence.manager.BpmConfNodeManager;
import com.mossle.bpm.persistence.manager.BpmConfUserManager;
import com.mossle.bpm.persistence.manager.BpmProcessManager;

import com.mossle.core.mapper.BeanMapper;

import com.mossle.spi.humantask.TaskDefinitionConnector;
import com.mossle.spi.humantask.TaskUserDTO;

import org.activiti.engine.ProcessEngine;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("bpm")
public class BpmConfUserController {
    private static Logger logger = LoggerFactory
            .getLogger(BpmConfUserController.class);
    private BpmConfNodeManager bpmConfNodeManager;
    private BpmConfUserManager bpmConfUserManager;
    private BeanMapper beanMapper = new BeanMapper();
    private ProcessEngine processEngine;
    private BpmProcessManager bpmProcessManager;
    private BpmConfCountersignManager bpmConfCountersignManager;
    private BpmConfAssignManager bpmConfAssignManager;
    private TaskDefinitionConnector taskDefinitionConnector;

    @RequestMapping("bpm-conf-user-list")
    public String list(@RequestParam("bpmConfNodeId") Long bpmConfNodeId,
            Model model) {
        BpmConfNode bpmConfNode = bpmConfNodeManager.get(bpmConfNodeId);
        Long bpmConfBaseId = bpmConfNode.getBpmConfBase().getId();
        List<BpmConfUser> bpmConfUsers = bpmConfUserManager.findBy(
                "bpmConfNode", bpmConfNode);
        BpmConfCountersign bpmConfCountersign = bpmConfCountersignManager
                .findUniqueBy("bpmConfNode", bpmConfNode);
        BpmConfAssign bpmConfAssign = bpmConfAssignManager.findUniqueBy(
                "bpmConfNode", bpmConfNode);
        model.addAttribute("bpmConfBase", bpmConfNode.getBpmConfBase());
        model.addAttribute("bpmConfNode", bpmConfNode);
        model.addAttribute("bpmConfBaseId", bpmConfBaseId);
        model.addAttribute("bpmConfUsers", bpmConfUsers);
        model.addAttribute("bpmConfCountersign", bpmConfCountersign);
        model.addAttribute("bpmConfAssign", bpmConfAssign);

        return "bpm/bpm-conf-user-list";
    }

    @RequestMapping("bpm-conf-user-save")
    public String save(@ModelAttribute BpmConfUser bpmConfUser,
            @RequestParam("bpmConfNodeId") Long bpmConfNodeId) {
        if (StringUtils.isBlank(bpmConfUser.getValue())) {
            logger.info("bpmConfUser cannot blank");

            return "redirect:/bpm/bpm-conf-user-list.do?bpmConfNodeId="
                    + bpmConfNodeId;
        }

        // 如果存在默认的负责人，要设置成删除状态
        if (bpmConfUser.getType() == 0) {
            String hql = "from BpmConfUser where bpmConfNode.id=? and type=0 and status=0";
            BpmConfUser targetBpmConfUser = bpmConfUserManager.findUnique(hql,
                    bpmConfNodeId);

            if (targetBpmConfUser != null) {
                targetBpmConfUser.setStatus(2);
                bpmConfUserManager.save(targetBpmConfUser);
            }
        }

        // 如果存在添加的负责人，直接更新
        if (bpmConfUser.getType() == 0) {
            String hql = "from BpmConfUser where bpmConfNode.id=? and type=0 and status=1";
            BpmConfUser targetBpmConfUser = bpmConfUserManager.findUnique(hql,
                    bpmConfNodeId);

            if (targetBpmConfUser != null) {
                targetBpmConfUser.setValue(bpmConfUser.getValue());
                bpmConfUser = targetBpmConfUser;
            }
        }

        bpmConfUser.setPriority(0);
        bpmConfUser.setStatus(1);
        bpmConfUser.setBpmConfNode(bpmConfNodeManager.get(bpmConfNodeId));
        bpmConfUserManager.save(bpmConfUser);

        BpmConfUser dest = bpmConfUser;
        String taskDefinitionKey = dest.getBpmConfNode().getCode();
        String processDefinitionId = dest.getBpmConfNode().getBpmConfBase()
                .getProcessDefinitionId();
        Integer type = dest.getType();
        String value = dest.getValue();
        TaskUserDTO taskUser = new TaskUserDTO();

        if (type == 0) {
            taskUser.setCatalog("assignee");
        } else if ((type == 1) || (type == 2)) {
            taskUser.setCatalog("candidate");
        } else if (type == 3) {
            taskUser.setCatalog("notification");
        }

        if ((type == 0) || (type == 1)) {
            taskUser.setType("user");
        } else if (type == 2) {
            taskUser.setType("group");
        }

        taskUser.setValue(value);
        taskDefinitionConnector.addTaskUser(taskDefinitionKey,
                processDefinitionId, taskUser);

        return "redirect:/bpm/bpm-conf-user-list.do?bpmConfNodeId="
                + bpmConfNodeId;
    }

    @RequestMapping("bpm-conf-user-remove")
    public String remove(@RequestParam("id") Long id) {
        BpmConfUser bpmConfUser = bpmConfUserManager.get(id);
        Long bpmConfNodeId = bpmConfUser.getBpmConfNode().getId();

        if (bpmConfUser.getStatus() == 0) {
            // 默认 -> 删除
            bpmConfUser.setStatus(2);
            bpmConfUserManager.save(bpmConfUser);
        } else if (bpmConfUser.getStatus() == 1) {
            // 删除添加
            bpmConfUserManager.remove(bpmConfUser);
        } else if (bpmConfUser.getStatus() == 2) {
            // 删除 -> 默认
            bpmConfUser.setStatus(0);
            bpmConfUserManager.save(bpmConfUser);
        }

        BpmConfUser dest = bpmConfUser;
        String taskDefinitionKey = dest.getBpmConfNode().getCode();
        String processDefinitionId = dest.getBpmConfNode().getBpmConfBase()
                .getProcessDefinitionId();

        Integer type = dest.getType();
        String value = dest.getValue();
        TaskUserDTO taskUser = new TaskUserDTO();

        if (type == 0) {
            taskUser.setCatalog("assignee");
        } else if ((type == 1) || (type == 2)) {
            taskUser.setCatalog("candidate");
        } else if (type == 3) {
            taskUser.setCatalog("notification");
        }

        if ((type == 0) || (type == 1)) {
            taskUser.setType("user");
        } else if (type == 2) {
            taskUser.setType("group");
        }

        taskUser.setValue(value);

        if (bpmConfUser.getStatus() == 0) {
            // 默认 > 删除
            taskDefinitionConnector.updateTaskUser(taskDefinitionKey,
                    processDefinitionId, taskUser, "active");
        } else if (bpmConfUser.getStatus() == 1) {
            // 删除添加
            taskDefinitionConnector.removeTaskUser(taskDefinitionKey,
                    processDefinitionId, taskUser);
        } else if (bpmConfUser.getStatus() == 2) {
            // 删除 > 默认
            taskDefinitionConnector.updateTaskUser(taskDefinitionKey,
                    processDefinitionId, taskUser, "disable");
        }

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
