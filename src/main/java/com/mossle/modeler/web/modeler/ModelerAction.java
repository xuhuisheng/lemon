package com.mossle.modeler.web.modeler;

import java.util.List;

import com.mossle.core.struts2.BaseAction;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;

import org.activiti.editor.language.json.converter.BpmnJsonConverter;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;

import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

/**
 * modeler.
 * 
 * @author Lingo
 */
@Results({
        @Result(name = ModelerAction.RELOAD, location = "modeler!list.do?operationMode=RETRIEVE", type = "redirect"),
        @Result(name = ModelerAction.RELOAD_OPEN, location = "../widgets/modeler/editor.html?id=${id}", type = "redirect") })
public class ModelerAction extends BaseAction {
    public static final String RELOAD = "reload";
    public static final String RELOAD_OPEN = "reload-open";
    private ProcessEngine processEngine;
    private List<Model> models;
    private String id;

    public String list() {
        models = processEngine.getRepositoryService().createModelQuery().list();

        return "list";
    }

    public String open() throws Exception {
        RepositoryService repositoryService = processEngine
                .getRepositoryService();
        Model model = repositoryService.getModel(id);

        if (model == null) {
            model = repositoryService.newModel();
            repositoryService.saveModel(model);
            id = model.getId();
        }

        return RELOAD_OPEN;
    }

    public String removeModel() {
        processEngine.getRepositoryService().deleteModel(id);

        return RELOAD;
    }

    public String deploy() throws Exception {
        RepositoryService repositoryService = processEngine
                .getRepositoryService();
        Model modelData = repositoryService.getModel(id);
        ObjectNode modelNode = (ObjectNode) new ObjectMapper()
                .readTree(repositoryService.getModelEditorSource(modelData
                        .getId()));
        byte[] bpmnBytes = null;

        BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
        bpmnBytes = new BpmnXMLConverter().convertToXML(model);

        String processName = modelData.getName() + ".bpmn20.xml";
        repositoryService.createDeployment().name(modelData.getName())
                .addString(processName, new String(bpmnBytes, "UTF-8"))
                .deploy();

        return RELOAD;
    }

    // ~ ==================================================
    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    public List<Model> getModels() {
        return models;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
