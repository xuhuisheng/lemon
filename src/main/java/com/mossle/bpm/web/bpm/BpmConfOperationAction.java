package com.mossle.bpm.web.bpm;

import java.util.*;
import java.util.ArrayList;
import java.util.List;

import com.mossle.bpm.persistence.domain.BpmConfNode;
import com.mossle.bpm.persistence.domain.BpmConfOperation;
import com.mossle.bpm.persistence.domain.BpmProcess;
import com.mossle.bpm.persistence.manager.BpmConfNodeManager;
import com.mossle.bpm.persistence.manager.BpmConfOperationManager;
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

@Results(@Result(name = BpmConfOperationAction.RELOAD, location = "bpm-conf-operation.do?bpmConfNodeId=${bpmConfNodeId}", type = "redirect"))
public class BpmConfOperationAction extends BaseAction implements
        ModelDriven<BpmConfOperation>, Preparable {
    public static final String RELOAD = "reload";
    private BpmConfNodeManager bpmConfNodeManager;
    private BpmConfOperationManager bpmConfOperationManager;
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private BpmConfOperation model;
    private long id;
    private List<Long> selectedItem = new ArrayList<Long>();
    private Exportor exportor = new Exportor();
    private BeanMapper beanMapper = new BeanMapper();
    private ProcessEngine processEngine;
    private long bpmConfOperationId;
    private List<BpmConfOperation> bpmConfOperations;
    private String processDefinitionId;
    private long bpmProcessId;
    private BpmProcessManager bpmProcessManager;
    private long bpmConfNodeId;
    private long bpmConfBaseId;
    private List<String> operations = new ArrayList<String>();

    public BpmConfOperationAction() {
        operations.add("保存草稿");
        operations.add("完成任务");
        operations.add("驳回");
        operations.add("转办");
        operations.add("协办");
    }

    public String execute() {
        BpmConfNode bpmConfNode = bpmConfNodeManager.get(bpmConfNodeId);
        bpmConfBaseId = bpmConfNode.getBpmConfBase().getId();
        bpmConfOperations = bpmConfOperationManager.findBy("bpmConfNode",
                bpmConfNode);

        for (Iterator<String> iterator = operations.iterator(); iterator
                .hasNext();) {
            String value = iterator.next();

            for (BpmConfOperation bpmConfOperation : bpmConfOperations) {
                if (value.equals(bpmConfOperation.getValue())) {
                    iterator.remove();

                    break;
                }
            }
        }

        return SUCCESS;
    }

    public void prepareSave() {
        model = new BpmConfOperation();
    }

    public String save() {
        if ((model.getValue() == null) || "".equals(model.getValue())) {
            return RELOAD;
        }

        model.setBpmConfNode(bpmConfNodeManager.get(bpmConfNodeId));
        bpmConfOperationManager.save(model);

        return RELOAD;
    }

    public String remove() {
        BpmConfOperation bpmConfOperation = bpmConfOperationManager.get(id);
        bpmConfNodeId = bpmConfOperation.getBpmConfNode().getId();
        bpmConfOperationManager.remove(bpmConfOperation);

        return RELOAD;
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public BpmConfOperation getModel() {
        return model;
    }

    public void setBpmConfNodeManager(BpmConfNodeManager bpmConfNodeManager) {
        this.bpmConfNodeManager = bpmConfNodeManager;
    }

    public void setBpmConfOperationManager(
            BpmConfOperationManager bpmConfOperationManager) {
        this.bpmConfOperationManager = bpmConfOperationManager;
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
    public long getBpmConfOperationId() {
        return bpmConfOperationId;
    }

    public void setBpmConfOperationId(long bpmConfOperationId) {
        this.bpmConfOperationId = bpmConfOperationId;
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

    public List<BpmConfOperation> getBpmConfOperations() {
        return bpmConfOperations;
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

    public List<String> getOperations() {
        return operations;
    }
}
