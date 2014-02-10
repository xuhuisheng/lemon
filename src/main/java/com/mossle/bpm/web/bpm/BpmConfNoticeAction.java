package com.mossle.bpm.web.bpm;

import java.util.*;
import java.util.ArrayList;
import java.util.List;

import com.mossle.bpm.persistence.domain.BpmConfNode;
import com.mossle.bpm.persistence.domain.BpmConfNotice;
import com.mossle.bpm.persistence.domain.BpmMailTemplate;
import com.mossle.bpm.persistence.domain.BpmProcess;
import com.mossle.bpm.persistence.manager.BpmConfNodeManager;
import com.mossle.bpm.persistence.manager.BpmConfNoticeManager;
import com.mossle.bpm.persistence.manager.BpmMailTemplateManager;
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

@Results(@Result(name = BpmConfNoticeAction.RELOAD, location = "bpm-conf-notice.do?bpmConfNodeId=${bpmConfNodeId}", type = "redirect"))
public class BpmConfNoticeAction extends BaseAction implements
        ModelDriven<BpmConfNotice>, Preparable {
    public static final String RELOAD = "reload";
    private BpmConfNodeManager bpmConfNodeManager;
    private BpmConfNoticeManager bpmConfNoticeManager;
    private BpmMailTemplateManager bpmMailTemplateManager;
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private BpmConfNotice model;
    private long id;
    private List<Long> selectedItem = new ArrayList<Long>();
    private Exportor exportor = new Exportor();
    private BeanMapper beanMapper = new BeanMapper();
    private ProcessEngine processEngine;
    private long bpmConfNoticeId;
    private List<BpmConfNotice> bpmConfNotices;
    private String processDefinitionId;
    private long bpmProcessId;
    private BpmProcessManager bpmProcessManager;
    private long bpmConfNodeId;
    private long bpmConfBaseId;
    private List<BpmMailTemplate> bpmMailTemplates;
    private long bpmMailTemplateId;

    public String execute() {
        BpmConfNode bpmConfNode = bpmConfNodeManager.get(bpmConfNodeId);
        bpmConfBaseId = bpmConfNode.getBpmConfBase().getId();
        bpmConfNotices = bpmConfNoticeManager
                .findBy("bpmConfNode", bpmConfNode);
        bpmMailTemplates = bpmMailTemplateManager.getAll();

        return SUCCESS;
    }

    public void prepareSave() {
        model = new BpmConfNotice();
    }

    public String save() {
        model.setBpmConfNode(bpmConfNodeManager.get(bpmConfNodeId));
        model.setBpmMailTemplate(bpmMailTemplateManager.get(bpmMailTemplateId));
        bpmConfNoticeManager.save(model);

        return RELOAD;
    }

    public String remove() {
        BpmConfNotice bpmConfNotice = bpmConfNoticeManager.get(id);
        bpmConfNodeId = bpmConfNotice.getBpmConfNode().getId();
        bpmConfNoticeManager.remove(bpmConfNotice);

        return RELOAD;
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public BpmConfNotice getModel() {
        return model;
    }

    public void setBpmConfNodeManager(BpmConfNodeManager bpmConfNodeManager) {
        this.bpmConfNodeManager = bpmConfNodeManager;
    }

    public void setBpmConfNoticeManager(
            BpmConfNoticeManager bpmConfNoticeManager) {
        this.bpmConfNoticeManager = bpmConfNoticeManager;
    }

    public void setBpmProcessManager(BpmProcessManager bpmProcessManager) {
        this.bpmProcessManager = bpmProcessManager;
    }

    public void setBpmMailTemplateManager(
            BpmMailTemplateManager bpmMailTemplateManager) {
        this.bpmMailTemplateManager = bpmMailTemplateManager;
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
    public long getBpmConfNoticeId() {
        return bpmConfNoticeId;
    }

    public void setBpmConfNoticeId(long bpmConfNoticeId) {
        this.bpmConfNoticeId = bpmConfNoticeId;
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

    public List<BpmConfNotice> getBpmConfNotices() {
        return bpmConfNotices;
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

    public List<BpmMailTemplate> getBpmMailTemplates() {
        return bpmMailTemplates;
    }

    public void setBpmMailTemplateId(long bpmMailTemplateId) {
        this.bpmMailTemplateId = bpmMailTemplateId;
    }
}
