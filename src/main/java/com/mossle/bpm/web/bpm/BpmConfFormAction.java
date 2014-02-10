package com.mossle.bpm.web.bpm;

import java.util.*;
import java.util.ArrayList;
import java.util.List;

import com.mossle.bpm.persistence.domain.BpmConfForm;
import com.mossle.bpm.persistence.domain.BpmConfNode;
import com.mossle.bpm.persistence.domain.BpmProcess;
import com.mossle.bpm.persistence.manager.BpmConfFormManager;
import com.mossle.bpm.persistence.manager.BpmConfNodeManager;
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

@Results(@Result(name = BpmConfFormAction.RELOAD, location = "bpm-conf-form.do?bpmConfNodeId=${bpmConfNodeId}", type = "redirect"))
public class BpmConfFormAction extends BaseAction implements
        ModelDriven<BpmConfForm>, Preparable {
    public static final String RELOAD = "reload";
    private BpmConfNodeManager bpmConfNodeManager;
    private BpmConfFormManager bpmConfFormManager;
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private BpmConfForm model;
    private long id;
    private List<Long> selectedItem = new ArrayList<Long>();
    private Exportor exportor = new Exportor();
    private BeanMapper beanMapper = new BeanMapper();
    private ProcessEngine processEngine;
    private long bpmConfFormId;
    private List<BpmConfForm> bpmConfForms;
    private String processDefinitionId;
    private long bpmProcessId;
    private BpmProcessManager bpmProcessManager;
    private long bpmConfNodeId;
    private long bpmConfBaseId;

    public String execute() {
        BpmConfNode bpmConfNode = bpmConfNodeManager.get(bpmConfNodeId);
        bpmConfBaseId = bpmConfNode.getBpmConfBase().getId();
        bpmConfForms = bpmConfFormManager.findBy("bpmConfNode", bpmConfNode);

        return SUCCESS;
    }

    public void prepareSave() {
        model = new BpmConfForm();
    }

    public String save() {
        BpmConfForm bpmConfForm = bpmConfFormManager.findUnique(
                "from BpmConfForm where bpmConfNode.id=?", bpmConfNodeId);

        if (bpmConfForm == null) {
            // 如果不存在，就创建一个
            model.setBpmConfNode(bpmConfNodeManager.get(bpmConfNodeId));
            model.setStatus(1);
            bpmConfFormManager.save(model);
        } else {
            bpmConfForm.setValue(model.getValue());
            bpmConfForm.setType(model.getType());
            bpmConfForm.setStatus(1);
            bpmConfFormManager.save(bpmConfForm);
        }

        return RELOAD;
    }

    public String remove() {
        BpmConfForm bpmConfForm = bpmConfFormManager.get(id);
        bpmConfNodeId = bpmConfForm.getBpmConfNode().getId();

        if (bpmConfForm.getStatus() == 0) {
            bpmConfForm.setStatus(2);
            bpmConfFormManager.save(bpmConfForm);
        } else if (bpmConfForm.getStatus() == 2) {
            bpmConfForm.setStatus(1);
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

        return RELOAD;
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public BpmConfForm getModel() {
        return model;
    }

    public void setBpmConfNodeManager(BpmConfNodeManager bpmConfNodeManager) {
        this.bpmConfNodeManager = bpmConfNodeManager;
    }

    public void setBpmConfFormManager(BpmConfFormManager bpmConfFormManager) {
        this.bpmConfFormManager = bpmConfFormManager;
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
    public long getBpmConfFormId() {
        return bpmConfFormId;
    }

    public void setBpmConfFormId(long bpmConfFormId) {
        this.bpmConfFormId = bpmConfFormId;
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

    public List<BpmConfForm> getBpmConfForms() {
        return bpmConfForms;
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
