package com.mossle.modeler.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.bpm.cmd.SyncProcessCmd;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;

import org.activiti.editor.language.json.converter.BpmnJsonConverter;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * modeler.
 * 
 * @author Lingo
 */
@Controller
@RequestMapping("modeler")
public class ModelerController {
    private ProcessEngine processEngine;

    @RequestMapping("modeler-list")
    public String list(org.springframework.ui.Model model) {
        List<Model> models = processEngine.getRepositoryService()
                .createModelQuery().list();
        model.addAttribute("models", models);

        return "modeler/modeler-list";
    }

    @RequestMapping("modeler-open")
    public String open(@RequestParam(value = "id", required = false) String id)
            throws Exception {
        RepositoryService repositoryService = processEngine
                .getRepositoryService();
        Model model = repositoryService.getModel(id);

        if (model == null) {
            model = repositoryService.newModel();
            repositoryService.saveModel(model);
            id = model.getId();
        }

        return "redirect:/widgets/modeler/editor.html?id=" + id;
    }

    @RequestMapping("modeler-remove")
    public String remove(@RequestParam("id") String id) {
        processEngine.getRepositoryService().deleteModel(id);

        return "redirect:/modeler/modeler-list.do";
    }

    @RequestMapping("modeler-deploy")
    public String deploy(@RequestParam("id") String id) throws Exception {
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
        Deployment deployment = repositoryService.createDeployment()
                .name(modelData.getName())
                .addString(processName, new String(bpmnBytes, "UTF-8"))
                .deploy();
        modelData.setDeploymentId(deployment.getId());
        repositoryService.saveModel(modelData);

        List<ProcessDefinition> processDefinitions = repositoryService
                .createProcessDefinitionQuery()
                .deploymentId(deployment.getId()).list();

        for (ProcessDefinition processDefinition : processDefinitions) {
            processEngine.getManagementService().executeCommand(
                    new SyncProcessCmd(processDefinition.getId()));
        }

        return "redirect:/modeler/modeler-list.do";
    }

    // ~ ==================================================
    @Resource
    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }
}
