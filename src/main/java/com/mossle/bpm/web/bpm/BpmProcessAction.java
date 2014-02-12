package com.mossle.bpm.web.bpm;

import java.util.*;
import java.util.ArrayList;
import java.util.List;

import com.mossle.bpm.cmd.FindGraphCmd;
import com.mossle.bpm.cmd.FindTaskDefinitionsCmd;
import com.mossle.bpm.graph.ActivitiGraphBuilder;
import com.mossle.bpm.graph.Graph;
import com.mossle.bpm.graph.Node;
import com.mossle.bpm.persistence.domain.BpmCategory;
import com.mossle.bpm.persistence.domain.BpmConfBase;
import com.mossle.bpm.persistence.domain.BpmMailTemplate;
import com.mossle.bpm.persistence.domain.BpmProcess;
import com.mossle.bpm.persistence.domain.BpmTaskDef;
import com.mossle.bpm.persistence.domain.BpmTaskDefNotice;
import com.mossle.bpm.persistence.manager.BpmCategoryManager;
import com.mossle.bpm.persistence.manager.BpmConfBaseManager;
import com.mossle.bpm.persistence.manager.BpmMailTemplateManager;
import com.mossle.bpm.persistence.manager.BpmProcessManager;
import com.mossle.bpm.persistence.manager.BpmTaskDefManager;
import com.mossle.bpm.persistence.manager.BpmTaskDefNoticeManager;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.struts2.BaseAction;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.repository.ProcessDefinition;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

@Results({
        @Result(name = BpmProcessAction.RELOAD, location = "bpm-process.do?operationMode=RETRIEVE", type = "redirect"),
        @Result(name = BpmProcessAction.RELOAD_CONFIG, location = "bpm-process!config.do?id=${id}", type = "redirect") })
public class BpmProcessAction extends BaseAction implements
        ModelDriven<BpmProcess>, Preparable {
    public static final String RELOAD = "reload";
    public static final String RELOAD_CONFIG = "reload-config";
    private BpmProcessManager bpmProcessManager;
    private BpmCategoryManager bpmCategoryManager;
    private BpmTaskDefNoticeManager bpmTaskDefNoticeManager;
    private BpmMailTemplateManager bpmMailTemplateManager;
    private BpmTaskDefManager bpmTaskDefManager;
    private BpmConfBaseManager bpmConfBaseManager;
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private BpmProcess model;
    private long id;
    private List<Long> selectedItem = new ArrayList<Long>();
    private Exportor exportor = new Exportor();
    private BeanMapper beanMapper = new BeanMapper();
    private List<BpmCategory> bpmCategories;
    private Long bpmCategoryId;
    private List<TaskDefinition> taskDefinitions;
    private Map<TaskDefinition, List<?>> taskMap = new LinkedHashMap<TaskDefinition, List<?>>();
    private ProcessEngine processEngine;
    private List<BpmMailTemplate> bpmMailTemplates;
    private long bpmMailTemplateId;
    private Graph graph;
    private List<BpmTaskDef> bpmTaskDefs = new ArrayList<BpmTaskDef>();
    private List<BpmConfBase> bpmConfBases = new ArrayList<BpmConfBase>();
    private long bpmConfBaseId;

    public String execute() {
        return list();
    }

    public String list() {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = bpmProcessManager.pagedQuery(page, propertyFilters);

        return SUCCESS;
    }

    public void prepareSave() {
        model = new BpmProcess();
    }

    public String save() {
        BpmProcess dest = null;

        if (id > 0) {
            dest = bpmProcessManager.get(id);
            beanMapper.copy(model, dest);
        } else {
            dest = model;
        }

        dest.setBpmCategory(bpmCategoryManager.get(bpmCategoryId));
        dest.setBpmConfBase(bpmConfBaseManager.get(bpmConfBaseId));
        bpmProcessManager.save(dest);

        addActionMessage(messages.getMessage("core.success.save", "保存成功"));

        return RELOAD;
    }

    public String removeAll() {
        List<BpmProcess> bpmCategories = bpmProcessManager
                .findByIds(selectedItem);
        bpmProcessManager.removeAll(bpmCategories);
        addActionMessage(messages.getMessage("core.success.delete", "删除成功"));

        return RELOAD;
    }

    public String input() {
        if (id > 0) {
            model = bpmProcessManager.get(id);
        }

        bpmCategories = bpmCategoryManager.getAll();
        bpmConfBases = bpmConfBaseManager.getAll();

        return INPUT;
    }

    public void exportExcel() throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = bpmProcessManager.pagedQuery(page, propertyFilters);

        List<BpmProcess> bpmCategories = (List<BpmProcess>) page.getResult();
        TableModel tableModel = new TableModel();
        tableModel.setName("bpm-process");
        tableModel.addHeaders("id", "name");
        tableModel.setData(bpmCategories);
        exportor.exportExcel(ServletActionContext.getResponse(), tableModel);
    }

    /*
     * public String viewConfig() { model = bpmProcessManager.get(id);
     * 
     * String processDefinitionId = processService .getProcessDefinitionId(model);
     * 
     * graph = processEngine.getManagementService().executeCommand( new FindGraphCmd(processDefinitionId));
     * 
     * BpmTaskDef bpmTaskDef = null; bpmTaskDef = bpmTaskDefManager .findUnique(
     * "from BpmTaskDef where activityId=null and bpmProcess.id=?", id);
     * 
     * if (bpmTaskDef == null) { bpmTaskDef = new BpmTaskDef(); bpmTaskDef.setBpmProcess(model);
     * bpmTaskDef.setConfUser(2); bpmTaskDef.setConfEvent(0); bpmTaskDef.setConfRule(2); bpmTaskDef.setConfForm(0);
     * bpmTaskDef.setConfOperation(2); bpmTaskDef.setConfNotice(2); bpmTaskDefManager.save(bpmTaskDef); }
     * 
     * bpmTaskDefs.add(bpmTaskDef);
     * 
     * for (Node node : graph.getNodes()) { if ("exclusiveGateway".equals(node.getType())) { continue; }
     * 
     * bpmTaskDef = bpmTaskDefManager.findUnique( "from BpmTaskDef where activityId=? and bpmProcess.id=?",
     * node.getId(), id);
     * 
     * if (bpmTaskDef == null) { bpmTaskDef = new BpmTaskDef(); bpmTaskDef.setActivityId(node.getId());
     * bpmTaskDef.setActivityName(node.getName()); bpmTaskDef.setActivityType(node.getType());
     * bpmTaskDef.setBpmProcess(model);
     * 
     * if ("userTask".equals(node.getType())) { bpmTaskDef.setConfUser(0); bpmTaskDef.setConfEvent(0);
     * bpmTaskDef.setConfRule(0); bpmTaskDef.setConfForm(0); bpmTaskDef.setConfOperation(0);
     * bpmTaskDef.setConfNotice(0); }
     * 
     * if ("startEvent".equals(node.getType())) { bpmTaskDef.setConfUser(2); bpmTaskDef.setConfEvent(0);
     * bpmTaskDef.setConfRule(2); bpmTaskDef.setConfForm(2); bpmTaskDef.setConfOperation(2);
     * bpmTaskDef.setConfNotice(0); }
     * 
     * if ("endEvent".equals(node.getType())) { bpmTaskDef.setConfUser(2); bpmTaskDef.setConfEvent(0);
     * bpmTaskDef.setConfRule(2); bpmTaskDef.setConfForm(2); bpmTaskDef.setConfOperation(2);
     * bpmTaskDef.setConfNotice(0); }
     * 
     * bpmTaskDefManager.save(bpmTaskDef); }
     * 
     * bpmTaskDefs.add(bpmTaskDef); }
     * 
     * return "viewConfig"; }
     */

    /*
     * public String config() { model = bpmProcessManager.get(id);
     * 
     * ProcessDefinition processDefinition = processEngine .getRepositoryService().createProcessDefinitionQuery()
     * .processDefinitionKey(model.getProcessDefinitionKey())
     * .processDefinitionVersion(model.getProcessDefinitionVersion()) .singleResult(); FindTaskDefinitionsCmd cmd = new
     * FindTaskDefinitionsCmd( processDefinition.getId()); taskDefinitions =
     * processEngine.getManagementService().executeCommand( cmd);
     * 
     * for (TaskDefinition taskDefinition : taskDefinitions) { List<BpmTaskDefNotice> bpmTaskDefNotices =
     * bpmTaskDefNoticeManager .find("from BpmTaskDefNotice where taskDefinitionKey=? and bpmProcess=?",
     * taskDefinition.getKey(), model); taskMap.put(taskDefinition, bpmTaskDefNotices); }
     * 
     * return "config"; }
     */

    // ~ ======================================================================
    public void prepare() {
    }

    public BpmProcess getModel() {
        return model;
    }

    public void setBpmProcessManager(BpmProcessManager bpmProcessManager) {
        this.bpmProcessManager = bpmProcessManager;
    }

    public void setBpmCategoryManager(BpmCategoryManager bpmCategoryManager) {
        this.bpmCategoryManager = bpmCategoryManager;
    }

    public void setBpmTaskDefNoticeManager(
            BpmTaskDefNoticeManager bpmTaskDefNoticeManager) {
        this.bpmTaskDefNoticeManager = bpmTaskDefNoticeManager;
    }

    public void setBpmMailTemplate(BpmMailTemplateManager bpmMailTemplateManager) {
        this.bpmMailTemplateManager = bpmMailTemplateManager;
    }

    public void setBpmTaskDefManager(BpmTaskDefManager bpmTaskDefManager) {
        this.bpmTaskDefManager = bpmTaskDefManager;
    }

    public void setBpmConfBaseManager(BpmConfBaseManager bpmConfBaseManager) {
        this.bpmConfBaseManager = bpmConfBaseManager;
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
    public List<BpmCategory> getBpmCategories() {
        return bpmCategories;
    }

    public void setBpmCategoryId(Long bpmCategoryId) {
        this.bpmCategoryId = bpmCategoryId;
    }

    public List<TaskDefinition> getTaskDefinitions() {
        return taskDefinitions;
    }

    public Map<TaskDefinition, List<?>> getTaskMap() {
        return taskMap;
    }

    public List<BpmMailTemplate> getBpmMailTemplates() {
        return bpmMailTemplates;
    }

    public void setBpmMailTemplateId(long bpmMailTemplateId) {
        this.bpmMailTemplateId = bpmMailTemplateId;
    }

    public Graph getGraph() {
        return graph;
    }

    public List<BpmTaskDef> getBpmTaskDefs() {
        return bpmTaskDefs;
    }

    public List<BpmConfBase> getBpmConfBases() {
        return bpmConfBases;
    }

    public void setBpmConfBaseId(long bpmConfBaseId) {
        this.bpmConfBaseId = bpmConfBaseId;
    }
}
