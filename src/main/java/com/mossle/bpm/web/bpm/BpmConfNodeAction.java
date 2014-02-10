package com.mossle.bpm.web.bpm;

import java.util.*;
import java.util.ArrayList;
import java.util.List;

import com.mossle.bpm.persistence.domain.BpmConfBase;
import com.mossle.bpm.persistence.domain.BpmConfNode;
import com.mossle.bpm.persistence.domain.BpmConfUser;
import com.mossle.bpm.persistence.domain.BpmProcess;
import com.mossle.bpm.persistence.manager.BpmConfBaseManager;
import com.mossle.bpm.persistence.manager.BpmConfNodeManager;
import com.mossle.bpm.persistence.manager.BpmConfUserManager;
import com.mossle.bpm.persistence.manager.BpmProcessManager;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.struts2.BaseAction;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.repository.ProcessDefinition;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

@Results(@Result(name = BpmConfNodeAction.RELOAD, location = "bpm-node.do?bpmConfNodeId=${bpmConfNodeId}", type = "redirect"))
public class BpmConfNodeAction extends BaseAction implements
        ModelDriven<BpmConfNode>, Preparable {
    public static final String RELOAD = "reload";
    private BpmConfUserManager bpmConfUserManager;
    private BpmConfNodeManager bpmConfNodeManager;
    private BpmConfBaseManager bpmConfBaseManager;
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private BpmConfNode model;
    private long id;
    private List<Long> selectedItem = new ArrayList<Long>();
    private Exportor exportor = new Exportor();
    private BeanMapper beanMapper = new BeanMapper();
    private ProcessEngine processEngine;
    private long bpmConfNodeId;
    private List<BpmConfNode> bpmConfNodes;
    private List<BpmConfUser> bpmConfUsers;
    private String processDefinitionId;
    private long bpmProcessId;
    private BpmProcessManager bpmProcessManager;
    private long bpmConfBaseId;

    public String execute() {
        BpmConfBase bpmConfBase = bpmConfBaseManager.get(bpmConfBaseId);
        bpmConfNodes = bpmConfNodeManager.findBy("bpmConfBase", bpmConfBase);

        return SUCCESS;
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public BpmConfNode getModel() {
        return model;
    }

    public void setBpmConfUserManager(BpmConfUserManager bpmConfUserManager) {
        this.bpmConfUserManager = bpmConfUserManager;
    }

    public void setBpmConfNodeManager(BpmConfNodeManager bpmConfNodeManager) {
        this.bpmConfNodeManager = bpmConfNodeManager;
    }

    public void setBpmConfBaseManager(BpmConfBaseManager bpmConfBaseManager) {
        this.bpmConfBaseManager = bpmConfBaseManager;
    }

    public void setBpmProcessManager(BpmProcessManager bpmProcessManager) {
        this.bpmProcessManager = bpmProcessManager;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    // ~ ======================================================================
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Page getPage() {
        return page;
    }

    public void setSelectedItem(List<Long> selectedItem) {
        this.selectedItem = selectedItem;
    }

    // ~ ======================================================================
    public long getBpmConfNodeId() {
        return bpmConfNodeId;
    }

    public void setBpmConfNodeId(long bpmConfNodeId) {
        this.bpmConfNodeId = bpmConfNodeId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public void setBpmProcessId(long bpmProcessId) {
        this.bpmProcessId = bpmProcessId;
    }

    public List<BpmConfNode> getBpmConfNodes() {
        return bpmConfNodes;
    }

    public List<BpmConfUser> getBpmConfUsers() {
        return bpmConfUsers;
    }

    public long getBpmConfBaseId() {
        return bpmConfBaseId;
    }

    public void setBpmConfBaseId(long bpmConfBaseId) {
        this.bpmConfBaseId = bpmConfBaseId;
    }
}
