package com.mossle.bpm.web.bpm;

import java.util.*;
import java.util.ArrayList;
import java.util.List;

import com.mossle.bpm.persistence.domain.BpmConfNode;
import com.mossle.bpm.persistence.domain.BpmConfUser;
import com.mossle.bpm.persistence.domain.BpmProcess;
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

@Results(@Result(name = BpmConfUserAction.RELOAD, location = "bpm-conf-user.do?bpmConfNodeId=${bpmConfNodeId}", type = "redirect"))
public class BpmConfUserAction extends BaseAction implements
        ModelDriven<BpmConfUser>, Preparable {
    public static final String RELOAD = "reload";
    private BpmConfNodeManager bpmConfNodeManager;
    private BpmConfUserManager bpmConfUserManager;
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private BpmConfUser model;
    private long id;
    private List<Long> selectedItem = new ArrayList<Long>();
    private Exportor exportor = new Exportor();
    private BeanMapper beanMapper = new BeanMapper();
    private ProcessEngine processEngine;
    private long bpmConfUserId;
    private List<BpmConfUser> bpmConfUsers;
    private String processDefinitionId;
    private long bpmProcessId;
    private BpmProcessManager bpmProcessManager;
    private long bpmConfNodeId;
    private long bpmConfBaseId;

    public void prepareSave() {
        model = new BpmConfUser();
    }

    public String execute() {
        BpmConfNode bpmConfNode = bpmConfNodeManager.get(bpmConfNodeId);
        bpmConfBaseId = bpmConfNode.getBpmConfBase().getId();
        bpmConfUsers = bpmConfUserManager.findBy("bpmConfNode", bpmConfNode);

        return SUCCESS;
    }

    public String save() {
        model.setPriority(0);
        model.setStatus(1);
        model.setBpmConfNode(bpmConfNodeManager.get(bpmConfNodeId));
        bpmConfUserManager.save(model);

        return RELOAD;
    }

    public String remove() {
        model = bpmConfUserManager.get(id);
        bpmConfNodeId = model.getBpmConfNode().getId();

        if (model.getStatus() == 0) {
            model.setStatus(2);
            bpmConfUserManager.save(model);
        } else if (model.getStatus() == 1) {
            bpmConfUserManager.remove(model);
        } else if (model.getStatus() == 2) {
            model.setStatus(0);
            bpmConfUserManager.save(model);
        }

        return RELOAD;
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public BpmConfUser getModel() {
        return model;
    }

    public void setBpmConfNodeManager(BpmConfNodeManager bpmConfNodeManager) {
        this.bpmConfNodeManager = bpmConfNodeManager;
    }

    public void setBpmConfUserManager(BpmConfUserManager bpmConfUserManager) {
        this.bpmConfUserManager = bpmConfUserManager;
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
    public long getBpmConfUserId() {
        return bpmConfUserId;
    }

    public void setBpmConfUserId(long bpmConfUserId) {
        this.bpmConfUserId = bpmConfUserId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public long getBpmProcessId() {
        return bpmProcessId;
    }

    public void setBpmProcessId(long bpmProcessId) {
        this.bpmProcessId = bpmProcessId;
    }

    public List<BpmConfUser> getBpmConfUsers() {
        return bpmConfUsers;
    }

    public long getBpmConfNodeId() {
        return bpmConfNodeId;
    }

    public void setBpmConfNodeId(long bpmConfNodeId) {
        this.bpmConfNodeId = bpmConfNodeId;
    }

    public long getBpmConfBaseId() {
        return bpmConfBaseId;
    }

    public void setBpmConfBaseId(long bpmConfBaseId) {
        this.bpmConfBaseId = bpmConfBaseId;
    }
}
