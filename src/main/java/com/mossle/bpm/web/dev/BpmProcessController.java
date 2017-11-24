package com.mossle.bpm.web.dev;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.form.FormConnector;
import com.mossle.api.form.FormDTO;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.bpm.persistence.domain.BpmCategory;
import com.mossle.bpm.persistence.domain.BpmConfAssign;
import com.mossle.bpm.persistence.domain.BpmConfBase;
import com.mossle.bpm.persistence.domain.BpmConfForm;
import com.mossle.bpm.persistence.domain.BpmConfListener;
import com.mossle.bpm.persistence.domain.BpmConfNode;
import com.mossle.bpm.persistence.domain.BpmConfNotice;
import com.mossle.bpm.persistence.domain.BpmConfOperation;
import com.mossle.bpm.persistence.domain.BpmConfRule;
import com.mossle.bpm.persistence.domain.BpmConfUser;
import com.mossle.bpm.persistence.domain.BpmProcess;
import com.mossle.bpm.persistence.manager.BpmCategoryManager;
import com.mossle.bpm.persistence.manager.BpmConfAssignManager;
import com.mossle.bpm.persistence.manager.BpmConfBaseManager;
import com.mossle.bpm.persistence.manager.BpmConfFormManager;
import com.mossle.bpm.persistence.manager.BpmConfListenerManager;
import com.mossle.bpm.persistence.manager.BpmConfNodeManager;
import com.mossle.bpm.persistence.manager.BpmConfNoticeManager;
import com.mossle.bpm.persistence.manager.BpmConfOperationManager;
import com.mossle.bpm.persistence.manager.BpmConfRuleManager;
import com.mossle.bpm.persistence.manager.BpmConfUserManager;
import com.mossle.bpm.persistence.manager.BpmProcessManager;
import com.mossle.bpm.persistence.manager.BpmTaskDefManager;
import com.mossle.bpm.support.TaskDefinitionBuilder;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.spi.form.InternalFormConnector;
import com.mossle.spi.humantask.TaskDefinitionConnector;
import com.mossle.spi.humantask.TaskDefinitionDTO;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("com.mossle.bpm.web.dev.BpmProcessController")
@RequestMapping("bpm/dev")
public class BpmProcessController {
    private static Logger logger = LoggerFactory
            .getLogger(BpmProcessController.class);
    private BpmProcessManager bpmProcessManager;
    private BpmCategoryManager bpmCategoryManager;
    private BpmTaskDefManager bpmTaskDefManager;
    private BpmConfBaseManager bpmConfBaseManager;
    private BpmConfNodeManager bpmConfNodeManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private ProcessEngine processEngine;
    private MessageHelper messageHelper;
    private TenantHolder tenantHolder;
    private JsonMapper jsonMapper = new JsonMapper();
    private FormConnector formConnector;
    private BpmConfUserManager bpmConfUserManager;
    private BpmConfAssignManager bpmConfAssignManager;
    private BpmConfListenerManager bpmConfListenerManager;
    private BpmConfRuleManager bpmConfRuleManager;
    private BpmConfFormManager bpmConfFormManager;
    private BpmConfOperationManager bpmConfOperationManager;
    private BpmConfNoticeManager bpmConfNoticeManager;
    private InternalFormConnector internalFormConnector;
    private TaskDefinitionConnector taskDefinitionConnector;

    /**
     * 导出.
     */
    @RequestMapping("conf-export")
    public String confExport(@RequestParam("id") Long id, Model model)
            throws Exception {
        // bpmProcess
        BpmProcess bpmProcess = this.bpmProcessManager.get(id);
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("code", bpmProcess.getCode());
        map.put("name", bpmProcess.getName());
        map.put("useTaskConf",
                Integer.valueOf(1).equals(bpmProcess.getUseTaskConf()));
        map.put("description", bpmProcess.getDescn());

        // bpmConfBase
        BpmConfBase bpmConfBase = bpmProcess.getBpmConfBase();
        map.put("processDefinitionKey", bpmConfBase.getProcessDefinitionKey());
        map.put("processDefinitionVersion",
                bpmConfBase.getProcessDefinitionVersion());
        map.put("xml", this.readXml(bpmConfBase.getProcessDefinitionId()));

        // bpmConfNode
        List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
        map.put("nodes", nodes);

        for (BpmConfNode bpmConfNode : bpmConfBase.getBpmConfNodes()) {
            Map<String, Object> nodeMap = this.exportNode(bpmConfNode);
            nodes.add(nodeMap);
        }

        // json
        String json = jsonMapper.toJson(map);
        model.addAttribute("json", json);

        return "bpm/dev/conf-export";
    }

    @RequestMapping("conf-import-input")
    public String confImportInput() {
        return "bpm/dev/conf-import-input";
    }

    /**
     * 导入.
     */
    @RequestMapping("conf-import-save")
    public String confImportSave(@RequestParam("text") String text)
            throws Exception {
        Map<String, Object> json = jsonMapper.fromJson(text, Map.class);

        // xml
        ProcessDefinition processDefinition = this.importXml((String) json
                .get("xml"));

        // process
        BpmProcess bpmProcess = this.importProcess(json);

        // conf
        BpmConfBase bpmConfBase = this.importConf(json, processDefinition);
        bpmProcess.setBpmConfBase(bpmConfBase);
        bpmProcessManager.save(bpmProcess);

        return "bpm/dev/conf-import-save";
    }

    // 导出
    public String readXml(String processDefinitionId) throws Exception {
        RepositoryService repositoryService = processEngine
                .getRepositoryService();
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId).singleResult();
        String resourceName = processDefinition.getResourceName();
        InputStream resourceAsStream = repositoryService.getResourceAsStream(
                processDefinition.getDeploymentId(), resourceName);

        return IOUtils.toString(resourceAsStream, "UTF-8");
    }

    public Map<String, Object> exportNode(BpmConfNode bpmConfNode) {
        Map<String, Object> nodeMap = new LinkedHashMap<String, Object>();
        nodeMap.put("code", bpmConfNode.getCode());
        nodeMap.put("name", bpmConfNode.getName());
        nodeMap.put("type", bpmConfNode.getType());
        nodeMap.put("priority", bpmConfNode.getPriority());
        nodeMap.put("confUser", bpmConfNode.getConfUser());
        nodeMap.put("confListener", bpmConfNode.getConfListener());
        nodeMap.put("confRule", bpmConfNode.getConfRule());
        nodeMap.put("confForm", bpmConfNode.getConfForm());
        nodeMap.put("confOperation", bpmConfNode.getConfOperation());
        nodeMap.put("confNotice", bpmConfNode.getConfNotice());

        List<Map<String, Object>> users = new ArrayList<Map<String, Object>>();
        nodeMap.put("users", users);

        for (BpmConfUser bpmConfUser : bpmConfNode.getBpmConfUsers()) {
            users.add(this.exportUser(bpmConfUser));
        }

        List<Map<String, Object>> assigns = new ArrayList<Map<String, Object>>();
        nodeMap.put("assigns", assigns);

        for (BpmConfAssign bpmConfAssign : bpmConfNode.getBpmConfAssigns()) {
            assigns.add(this.exportAssign(bpmConfAssign));
        }

        List<Map<String, Object>> listeners = new ArrayList<Map<String, Object>>();
        nodeMap.put("listeners", listeners);

        for (BpmConfListener bpmConfListener : bpmConfNode
                .getBpmConfListeners()) {
            listeners.add(this.exportListener(bpmConfListener));
        }

        List<Map<String, Object>> rules = new ArrayList<Map<String, Object>>();
        nodeMap.put("rules", rules);

        for (BpmConfRule bpmConfRule : bpmConfNode.getBpmConfRules()) {
            rules.add(this.exportRule(bpmConfRule));
        }

        List<Map<String, Object>> forms = new ArrayList<Map<String, Object>>();
        nodeMap.put("forms", forms);

        for (BpmConfForm bpmConfForm : bpmConfNode.getBpmConfForms()) {
            forms.add(this.exportForm(bpmConfForm));
        }

        List<Map<String, Object>> operations = new ArrayList<Map<String, Object>>();
        nodeMap.put("operations", operations);

        for (BpmConfOperation bpmConfOperation : bpmConfNode
                .getBpmConfOperations()) {
            operations.add(this.exportOperation(bpmConfOperation));
        }

        List<Map<String, Object>> notices = new ArrayList<Map<String, Object>>();
        nodeMap.put("notices", notices);

        for (BpmConfNotice bpmConfNotice : bpmConfNode.getBpmConfNotices()) {
            notices.add(this.exportNotice(bpmConfNotice));
        }

        return nodeMap;
    }

    public Map<String, Object> exportUser(BpmConfUser bpmConfUser) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("value", bpmConfUser.getValue());
        map.put("type", bpmConfUser.getType());
        map.put("status", bpmConfUser.getStatus());
        map.put("priority", bpmConfUser.getPriority());

        return map;
    }

    public Map<String, Object> exportAssign(BpmConfAssign bpmConfAssign) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("name", bpmConfAssign.getName());

        return map;
    }

    public Map<String, Object> exportListener(BpmConfListener bpmConfListener) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("value", bpmConfListener.getValue());
        map.put("type", bpmConfListener.getType());
        map.put("status", bpmConfListener.getStatus());
        map.put("priority", bpmConfListener.getPriority());

        return map;
    }

    public Map<String, Object> exportRule(BpmConfRule bpmConfRule) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("value", bpmConfRule.getValue());

        return map;
    }

    public Map<String, Object> exportForm(BpmConfForm bpmConfForm) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("value", bpmConfForm.getValue());
        map.put("type", bpmConfForm.getType());
        map.put("originValue", bpmConfForm.getOriginValue());
        map.put("originType", bpmConfForm.getOriginType());
        map.put("status", bpmConfForm.getStatus());

        FormDTO formDto = formConnector.findForm(bpmConfForm.getValue(), "1");

        if (formDto != null) {
            map.put("formContent", formDto.getContent());
        } else {
            logger.info("cannot find form : {}", bpmConfForm.getValue());
        }

        return map;
    }

    public Map<String, Object> exportOperation(BpmConfOperation bpmConfOperation) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("value", bpmConfOperation.getValue());
        map.put("priority", bpmConfOperation.getPriority());

        return map;
    }

    public Map<String, Object> exportNotice(BpmConfNotice bpmConfNotice) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("type", bpmConfNotice.getType());
        map.put("receiver", bpmConfNotice.getReceiver());
        map.put("dueDate", bpmConfNotice.getDueDate());
        map.put("templateCode", bpmConfNotice.getTemplateCode());
        map.put("notificationType", bpmConfNotice.getNotificationType());

        return map;
    }

    // 导入
    public ProcessDefinition importXml(String xml) throws Exception {
        String fileName = "process.bpmn20.xml";
        InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
        String tenantId = "1";
        Deployment deployment = processEngine.getRepositoryService()
                .createDeployment().addInputStream(fileName, is)
                .tenantId(tenantId).deploy();
        List<ProcessDefinition> processDefinitions = processEngine
                .getRepositoryService().createProcessDefinitionQuery()
                .deploymentId(deployment.getId()).list();

        return processDefinitions.get(0);
    }

    public BpmProcess importProcess(Map<String, Object> json) {
        BpmProcess bpmProcess = new BpmProcess();
        bpmProcess.setCode((String) json.get("code"));
        bpmProcess.setName((String) json.get("name"));

        if (Boolean.TRUE.equals(json.get("useTaskConf"))) {
            bpmProcess.setUseTaskConf(1);
        } else {
            bpmProcess.setUseTaskConf(0);
        }

        bpmProcess.setDescn((String) json.get("description"));

        bpmProcessManager.save(bpmProcess);

        return bpmProcess;
    }

    public BpmConfBase importConf(Map<String, Object> json,
            ProcessDefinition processDefinition) {
        BpmConfBase bpmConfBase = new BpmConfBase();
        bpmConfBase.setProcessDefinitionId(processDefinition.getId());
        bpmConfBase.setProcessDefinitionKey(processDefinition.getKey());
        bpmConfBase.setProcessDefinitionVersion(processDefinition.getVersion());
        bpmConfBaseManager.save(bpmConfBase);

        List<Map<String, Object>> nodes = (List<Map<String, Object>>) json
                .get("nodes");

        for (Map<String, Object> nodeMap : nodes) {
            this.importNode(nodeMap, bpmConfBase);
        }

        return bpmConfBase;
    }

    public void importNode(Map<String, Object> map, BpmConfBase bpmConfBase) {
        BpmConfNode bpmConfNode = new BpmConfNode();
        bpmConfNode.setBpmConfBase(bpmConfBase);
        bpmConfNode.setCode((String) map.get("code"));
        bpmConfNode.setName((String) map.get("name"));
        bpmConfNode.setType((String) map.get("type"));
        bpmConfNode.setPriority((Integer) map.get("priority"));
        bpmConfNode.setConfUser((Integer) map.get("confUser"));
        bpmConfNode.setConfListener((Integer) map.get("confListener"));
        bpmConfNode.setConfRule((Integer) map.get("confRule"));
        bpmConfNode.setConfForm((Integer) map.get("confForm"));
        bpmConfNode.setConfOperation((Integer) map.get("confOperation"));
        bpmConfNode.setConfNotice((Integer) map.get("confNotice"));

        bpmConfNodeManager.save(bpmConfNode);

        List<Map<String, Object>> users = (List<Map<String, Object>>) map
                .get("users");

        for (Map<String, Object> userMap : users) {
            this.importUser(userMap, bpmConfNode);
        }

        List<Map<String, Object>> assigns = (List<Map<String, Object>>) map
                .get("assigns");

        for (Map<String, Object> assignMap : assigns) {
            this.importAssign(assignMap, bpmConfNode);
        }

        List<Map<String, Object>> listeners = (List<Map<String, Object>>) map
                .get("listeners");

        for (Map<String, Object> listenerMap : listeners) {
            this.importListener(listenerMap, bpmConfNode);
        }

        List<Map<String, Object>> rules = (List<Map<String, Object>>) map
                .get("rules");

        for (Map<String, Object> ruleMap : rules) {
            this.importRule(ruleMap, bpmConfNode);
        }

        List<Map<String, Object>> forms = (List<Map<String, Object>>) map
                .get("forms");

        for (Map<String, Object> formMap : forms) {
            this.importForm(formMap, bpmConfNode);
        }

        List<Map<String, Object>> operations = (List<Map<String, Object>>) map
                .get("operations");

        for (Map<String, Object> operationMap : operations) {
            this.importOperation(operationMap, bpmConfNode);
        }

        List<Map<String, Object>> notices = (List<Map<String, Object>>) map
                .get("notices");

        for (Map<String, Object> noticeMap : notices) {
            this.importNotice(noticeMap, bpmConfNode);
        }

        if ("userTask".equals(bpmConfNode.getType())) {
            TaskDefinitionDTO taskDefinition = new TaskDefinitionDTO();
            taskDefinition.setCode(bpmConfNode.getCode());
            taskDefinition.setName(bpmConfNode.getName());
            taskDefinition.setProcessDefinitionId(bpmConfBase
                    .getProcessDefinitionId());

            for (Map<String, Object> userMap : users) {
                if (Integer.valueOf(0).equals(userMap.get("type"))) {
                    if (Integer.valueOf(0).equals(userMap.get("status"))
                            || Integer.valueOf(1).equals(userMap.get("status"))) {
                        taskDefinition.setAssignee((String) userMap
                                .get("value"));
                    }
                } else if (Integer.valueOf(1).equals(userMap.get("type"))) {
                    taskDefinition.addCandidateUser((String) userMap
                            .get("value"));
                } else if (Integer.valueOf(2).equals(userMap.get("type"))) {
                    taskDefinition.addCandidateGroup((String) userMap
                            .get("value"));
                }
            }

            for (Map<String, Object> formMap : forms) {
                com.mossle.spi.humantask.FormDTO formDto = new com.mossle.spi.humantask.FormDTO();
                formDto.setKey((String) formMap.get("value"));
                formDto.setType("internal");
                taskDefinition.setForm(formDto);
            }

            taskDefinitionConnector.create(taskDefinition);
        }
    }

    public void importUser(Map<String, Object> map, BpmConfNode bpmConfNode) {
        BpmConfUser bpmConfUser = new BpmConfUser();
        bpmConfUser.setBpmConfNode(bpmConfNode);
        bpmConfUser.setValue((String) map.get("value"));
        bpmConfUser.setType((Integer) map.get("type"));
        bpmConfUser.setStatus((Integer) map.get("status"));
        bpmConfUser.setPriority((Integer) map.get("priority"));
        bpmConfUserManager.save(bpmConfUser);
    }

    public void importAssign(Map<String, Object> map, BpmConfNode bpmConfNode) {
        BpmConfAssign bpmConfAssign = new BpmConfAssign();
        bpmConfAssign.setBpmConfNode(bpmConfNode);
        bpmConfAssign.setName((String) map.get("name"));
        bpmConfAssignManager.save(bpmConfAssign);
    }

    public void importListener(Map<String, Object> map, BpmConfNode bpmConfNode) {
        BpmConfListener bpmConfListener = new BpmConfListener();
        bpmConfListener.setBpmConfNode(bpmConfNode);
        bpmConfListener.setValue((String) map.get("value"));
        bpmConfListener.setType((Integer) map.get("type"));
        bpmConfListener.setStatus((Integer) map.get("status"));
        bpmConfListener.setPriority((Integer) map.get("priority"));
        bpmConfListenerManager.save(bpmConfListener);
    }

    public void importRule(Map<String, Object> map, BpmConfNode bpmConfNode) {
        BpmConfRule bpmConfRule = new BpmConfRule();
        bpmConfRule.setBpmConfNode(bpmConfNode);
        bpmConfRule.setValue((String) map.get("value"));
        bpmConfRuleManager.save(bpmConfRule);
    }

    public void importForm(Map<String, Object> map, BpmConfNode bpmConfNode) {
        BpmConfForm bpmConfForm = new BpmConfForm();
        bpmConfForm.setBpmConfNode(bpmConfNode);
        bpmConfForm.setValue((String) map.get("value"));
        bpmConfForm.setType((Integer) map.get("type"));
        bpmConfForm.setOriginValue((String) map.get("originValue"));
        bpmConfForm.setOriginType((Integer) map.get("originType"));
        bpmConfForm.setStatus((Integer) map.get("status"));
        bpmConfFormManager.save(bpmConfForm);
        // formTemplate
        internalFormConnector.save(bpmConfForm.getValue(),
                (String) map.get("formContent"), bpmConfForm.getType());
    }

    public void importOperation(Map<String, Object> map, BpmConfNode bpmConfNode) {
        BpmConfOperation bpmConfOperation = new BpmConfOperation();
        bpmConfOperation.setBpmConfNode(bpmConfNode);
        bpmConfOperation.setValue((String) map.get("value"));
        bpmConfOperation.setPriority((Integer) map.get("priority"));
        bpmConfOperationManager.save(bpmConfOperation);
    }

    public void importNotice(Map<String, Object> map, BpmConfNode bpmConfNode) {
        BpmConfNotice bpmConfNotice = new BpmConfNotice();
        bpmConfNotice.setBpmConfNode(bpmConfNode);
        bpmConfNotice.setType((Integer) map.get("type"));
        bpmConfNotice.setReceiver((String) map.get("receiver"));
        bpmConfNotice.setDueDate((String) map.get("dueDate"));
        bpmConfNotice.setTemplateCode((String) map.get("templateCode"));
        bpmConfNotice.setNotificationType((String) map.get("noticationType"));
        bpmConfNoticeManager.save(bpmConfNotice);
    }

    // ~ ======================================================================
    @Resource
    public void setBpmProcessManager(BpmProcessManager bpmProcessManager) {
        this.bpmProcessManager = bpmProcessManager;
    }

    @Resource
    public void setBpmCategoryManager(BpmCategoryManager bpmCategoryManager) {
        this.bpmCategoryManager = bpmCategoryManager;
    }

    @Resource
    public void setBpmTaskDefManager(BpmTaskDefManager bpmTaskDefManager) {
        this.bpmTaskDefManager = bpmTaskDefManager;
    }

    @Resource
    public void setBpmConfBaseManager(BpmConfBaseManager bpmConfBaseManager) {
        this.bpmConfBaseManager = bpmConfBaseManager;
    }

    @Resource
    public void setBpmConfNodeManager(BpmConfNodeManager bpmConfNodeManager) {
        this.bpmConfNodeManager = bpmConfNodeManager;
    }

    @Resource
    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }

    @Resource
    public void setFormConnector(FormConnector formConnector) {
        this.formConnector = formConnector;
    }

    @Resource
    public void setBpmConfUserManager(BpmConfUserManager bpmConfUserManager) {
        this.bpmConfUserManager = bpmConfUserManager;
    }

    @Resource
    public void setBpmConfAssignManager(
            BpmConfAssignManager bpmConfAssignManager) {
        this.bpmConfAssignManager = bpmConfAssignManager;
    }

    @Resource
    public void setBpmConfListenerManager(
            BpmConfListenerManager bpmConfListenerManager) {
        this.bpmConfListenerManager = bpmConfListenerManager;
    }

    @Resource
    public void setBpmConfRuleManger(BpmConfRuleManager bpmConfRuleManager) {
        this.bpmConfRuleManager = bpmConfRuleManager;
    }

    @Resource
    public void setBpmConfFormManager(BpmConfFormManager bpmConfFormManager) {
        this.bpmConfFormManager = bpmConfFormManager;
    }

    @Resource
    public void setBpmConfOperationManager(
            BpmConfOperationManager bpmConfOperationManager) {
        this.bpmConfOperationManager = bpmConfOperationManager;
    }

    @Resource
    public void setBpmConfNoticeManager(
            BpmConfNoticeManager bpmConfNoticeManager) {
        this.bpmConfNoticeManager = bpmConfNoticeManager;
    }

    @Resource
    public void setInternalFormConnector(
            InternalFormConnector internalFormConnector) {
        this.internalFormConnector = internalFormConnector;
    }

    @Resource
    public void setTaskDefinitionConnector(
            TaskDefinitionConnector taskDefinitionConnector) {
        this.taskDefinitionConnector = taskDefinitionConnector;
    }
}
