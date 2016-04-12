package com.mossle.bpm.web;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.api.process.ProcessConnector;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.bpm.cmd.ChangeSubTaskCmd;
import com.mossle.bpm.cmd.JumpCmd;
import com.mossle.bpm.cmd.ListActivityCmd;
import com.mossle.bpm.cmd.MigrateCmd;
import com.mossle.bpm.cmd.ProcessDefinitionDiagramCmd;
import com.mossle.bpm.cmd.ReOpenProcessCmd;
import com.mossle.bpm.cmd.SyncProcessCmd;
import com.mossle.bpm.cmd.UpdateProcessCmd;
import com.mossle.bpm.persistence.domain.BpmConfBase;
import com.mossle.bpm.persistence.domain.BpmConfCountersign;
import com.mossle.bpm.persistence.domain.BpmConfForm;
import com.mossle.bpm.persistence.domain.BpmConfListener;
import com.mossle.bpm.persistence.domain.BpmConfNode;
import com.mossle.bpm.persistence.domain.BpmConfNotice;
import com.mossle.bpm.persistence.domain.BpmConfOperation;
import com.mossle.bpm.persistence.domain.BpmConfRule;
import com.mossle.bpm.persistence.domain.BpmConfUser;
import com.mossle.bpm.persistence.manager.BpmConfBaseManager;

import com.mossle.core.page.Page;
import com.mossle.core.util.IoUtils;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;

import org.apache.commons.io.IOUtils;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 管理控制台.
 */
@Controller
@RequestMapping("bpm")
public class ConsoleController {
    private ProcessEngine processEngine;
    private ProcessConnector processConnector;
    private BpmConfBaseManager bpmConfBaseManager;
    private TenantHolder tenantHolder;

    /**
     * 部署列表.
     */
    @RequestMapping("console-listDeployments")
    public String listDeployments(@ModelAttribute Page page, Model model) {
        String tenantId = tenantHolder.getTenantId();
        page = processConnector.findDeployments(tenantId, page);
        model.addAttribute("page", page);

        return "bpm/console-listDeployments";
    }

    /**
     * 显示每个部署包里的资源.
     */
    @RequestMapping("console-listDeploymentResourceNames")
    public String listDeploymentResourceNames(
            @RequestParam("deploymentId") String deploymentId, Model model) {
        RepositoryService repositoryService = processEngine
                .getRepositoryService();
        List<String> deploymentResourceNames = repositoryService
                .getDeploymentResourceNames(deploymentId);
        model.addAttribute("deploymentResourceNames", deploymentResourceNames);

        return "bpm/console-listDeploymentResourceNames";
    }

    /**
     * 删除部署.
     */
    @RequestMapping("console-removeDeployment")
    public String removeDeployment(
            @RequestParam("deploymentId") String deploymentId) {
        RepositoryService repositoryService = processEngine
                .getRepositoryService();
        List<ProcessDefinition> processDefinitions = repositoryService
                .createProcessDefinitionQuery().deploymentId(deploymentId)
                .list();

        for (ProcessDefinition processDefinition : processDefinitions) {
            String hql = "from BpmConfBase where processDefinitionId=? or (processDefinitionKey=? and processDefinitionVersion=?)";
            List<BpmConfBase> bpmConfBases = bpmConfBaseManager.find(hql,
                    processDefinition.getId(), processDefinition.getKey(),
                    processDefinition.getVersion());

            for (BpmConfBase bpmConfBase : bpmConfBases) {
                for (BpmConfNode bpmConfNode : bpmConfBase.getBpmConfNodes()) {
                    for (BpmConfCountersign bpmConfCountersign : bpmConfNode
                            .getBpmConfCountersigns()) {
                        bpmConfBaseManager.remove(bpmConfCountersign);
                    }

                    for (BpmConfForm bpmConfForm : bpmConfNode
                            .getBpmConfForms()) {
                        bpmConfBaseManager.remove(bpmConfForm);
                    }

                    for (BpmConfListener bpmConfListener : bpmConfNode
                            .getBpmConfListeners()) {
                        bpmConfBaseManager.remove(bpmConfListener);
                    }

                    for (BpmConfNotice bpmConfNotice : bpmConfNode
                            .getBpmConfNotices()) {
                        bpmConfBaseManager.remove(bpmConfNotice);
                    }

                    for (BpmConfOperation bpmConfOperation : bpmConfNode
                            .getBpmConfOperations()) {
                        bpmConfBaseManager.remove(bpmConfOperation);
                    }

                    for (BpmConfRule bpmConfRule : bpmConfNode
                            .getBpmConfRules()) {
                        bpmConfBaseManager.remove(bpmConfRule);
                    }

                    for (BpmConfUser bpmConfUser : bpmConfNode
                            .getBpmConfUsers()) {
                        bpmConfBaseManager.remove(bpmConfUser);
                    }

                    bpmConfBaseManager.remove(bpmConfNode);
                }

                bpmConfBaseManager.remove(bpmConfBase);
            }
        }

        repositoryService.deleteDeployment(deploymentId, true);

        return "redirect:/bpm/console-listDeployments.do";
    }

    /**
     * 新建流程.
     */
    @RequestMapping("console-create")
    public String create() {
        return "bpm/console-create";
    }

    /**
     * 准备上传流程定义.
     */
    @RequestMapping("console-process-input")
    public String processInput() {
        return "bpm/console-process-input";
    }

    /**
     * 上传发布流程定义.
     */
    @RequestMapping("console-process-upload")
    public String processUpload(@RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes) throws Exception {
        String tenantId = tenantHolder.getTenantId();
        String fileName = file.getOriginalFilename();
        processEngine.getRepositoryService().createDeployment()
                .addInputStream(fileName, file.getInputStream())
                .tenantId(tenantId).deploy();

        return "redirect:/bpm/console-listProcessDefinitions.do";
    }

    /**
     * 发布流程.
     */
    @RequestMapping("console-deploy")
    public String deploy(@RequestParam("xml") String xml) throws Exception {
        RepositoryService repositoryService = processEngine
                .getRepositoryService();
        ByteArrayInputStream bais = new ByteArrayInputStream(
                xml.getBytes("UTF-8"));
        Deployment deployment = repositoryService.createDeployment()
                .addInputStream("process.bpmn20.xml", bais).deploy();
        List<ProcessDefinition> processDefinitions = repositoryService
                .createProcessDefinitionQuery()
                .deploymentId(deployment.getId()).list();

        for (ProcessDefinition processDefinition : processDefinitions) {
            processEngine.getManagementService().executeCommand(
                    new SyncProcessCmd(processDefinition.getId()));
        }

        return "redirect:/bpm/console-listProcessDefinitions.do";
    }

    /**
     * 显示流程定义列表.
     */
    @RequestMapping("console-listProcessDefinitions")
    public String listProcessDefinitions(@ModelAttribute Page page, Model model) {
        String tenantId = tenantHolder.getTenantId();
        page = processConnector.findProcessDefinitions(tenantId, page);
        model.addAttribute("page", page);

        return "bpm/console-listProcessDefinitions";
    }

    /**
     * 暂停流程定义.
     */
    @RequestMapping("console-suspendProcessDefinition")
    public String suspendProcessDefinition(
            @RequestParam("processDefinitionId") String processDefinitionId) {
        RepositoryService repositoryService = processEngine
                .getRepositoryService();
        repositoryService.suspendProcessDefinitionById(processDefinitionId,
                true, null);

        return "redirect:/bpm/console-listProcessDefinitions.do";
    }

    /**
     * 恢复流程定义.
     */
    @RequestMapping("console-activeProcessDefinition")
    public String activeProcessDefinition(
            @RequestParam("processDefinitionId") String processDefinitionId) {
        RepositoryService repositoryService = processEngine
                .getRepositoryService();

        repositoryService.activateProcessDefinitionById(processDefinitionId,
                true, null);

        return "redirect:/bpm/console-listProcessDefinitions.do";
    }

    /**
     * 显示流程定义图形.
     */
    @RequestMapping("console-graphProcessDefinition")
    public void graphProcessDefinition(
            @RequestParam("processDefinitionId") String processDefinitionId,
            HttpServletResponse response) throws Exception {
        Command<InputStream> cmd = new ProcessDefinitionDiagramCmd(
                processDefinitionId);

        InputStream is = processEngine.getManagementService().executeCommand(
                cmd);
        response.setContentType("image/png");

        IOUtils.copy(is, response.getOutputStream());
    }

    /**
     * 显示流程定义的xml.
     */
    @RequestMapping("console-viewXml")
    public void viewXml(
            @RequestParam("processDefinitionId") String processDefinitionId,
            HttpServletResponse response) throws Exception {
        RepositoryService repositoryService = processEngine
                .getRepositoryService();
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId).singleResult();
        String resourceName = processDefinition.getResourceName();
        InputStream resourceAsStream = repositoryService.getResourceAsStream(
                processDefinition.getDeploymentId(), resourceName);
        response.setContentType("text/xml;charset=UTF-8");
        IOUtils.copy(resourceAsStream, response.getOutputStream());
    }

    /**
     * 显示流程实例列表.
     */
    @RequestMapping("console-listProcessInstances")
    public String listProcessInstances(@ModelAttribute Page page, Model model) {
        String tenantId = tenantHolder.getTenantId();
        page = processConnector.findProcessInstances(tenantId, page);
        model.addAttribute("page", page);

        return "bpm/console-listProcessInstances";
    }

    /**
     * 删除流程实例.
     */
    @RequestMapping("console-removeProcessInstance")
    public String removeProcessInstance(
            @RequestParam("processInstanceId") String processInstanceId,
            @RequestParam("deleteReason") String deleteReason) {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        runtimeService.deleteProcessInstance(processInstanceId, deleteReason);

        return "redirect:/bpm/console-listProcessInstances.do";
    }

    /**
     * 暂停流程实例.
     */
    @RequestMapping("console-suspendProcessInstance")
    public String suspendProcessInstance(
            @RequestParam("processInstanceId") String processInstanceId) {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        runtimeService.suspendProcessInstanceById(processInstanceId);

        return "redirect:/bpm/console-listProcessInstances.do";
    }

    /**
     * 恢复流程实例.
     */
    @RequestMapping("console-activeProcessInstance")
    public String activeProcessInstance(
            @RequestParam("processInstanceId") String processInstanceId) {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        runtimeService.activateProcessInstanceById(processInstanceId);

        return "redirect:/bpm/console-listProcessInstances.do";
    }

    /**
     * 删除流程实例，包含历史.
     */
    @RequestMapping("console-deleteProcessInstance")
    public String deleteProcessInstance(@RequestParam("id") String id) {
        processEngine.getRuntimeService().deleteProcessInstance(id, "delete");
        processEngine.getHistoryService().deleteHistoricProcessInstance(id);

        return "redirect:/bpm/console-listProcessInstances.do";
    }

    /**
     * 显示任务列表.
     */
    @RequestMapping("console-listTasks")
    public String listTasks(@ModelAttribute Page page, Model model) {
        String tenantId = tenantHolder.getTenantId();
        page = processConnector.findTasks(tenantId, page);
        model.addAttribute("page", page);

        return "bpm/console-listTasks";
    }

    // The task cannot be deleted because is part of a running process
    /**
     * 显示历史流程实例.
     */
    @RequestMapping("console-listHistoricProcessInstances")
    public String listHistoricProcessInstances(@ModelAttribute Page page,
            Model model) {
        String tenantId = tenantHolder.getTenantId();
        page = processConnector.findHistoricProcessInstances(tenantId, page);

        model.addAttribute("page", page);

        return "bpm/console-listHistoricProcessInstances";
    }

    /**
     * 显示历史节点实例.
     */
    @RequestMapping("console-listHistoricActivityInstances")
    public String listHistoricActivityInstances(@ModelAttribute Page page,
            Model model) {
        String tenantId = tenantHolder.getTenantId();
        page = processConnector.findHistoricActivityInstances(tenantId, page);
        model.addAttribute("page", page);

        return "bpm/console-listHistoricActivityInstances";
    }

    /**
     * 显示历史任务.
     */
    @RequestMapping("console-listHistoricTasks")
    public String listHistoricTasks(@ModelAttribute Page page, Model model) {
        String tenantId = tenantHolder.getTenantId();
        page = processConnector.findHistoricTaskInstances(tenantId, page);
        model.addAttribute("page", page);

        return "bpm/console-listHistoricTasks";
    }

    // ~ ======================================================================
    /**
     * 自由流执行之前，选择跳转到哪个节点.
     */
    @RequestMapping("console-prepareJump")
    public String prepareJump(@RequestParam("executionId") String executionId,
            Model model) {
        Command<Map<String, String>> cmd = new ListActivityCmd(executionId);

        Map activityMap = processEngine.getManagementService().executeCommand(
                cmd);

        model.addAttribute("activityMap", activityMap);

        return "bpm/console-prepareJump";
    }

    /**
     * 自由流.
     */
    @RequestMapping("console-jump")
    public String jump(@RequestParam("executionId") String executionId,
            @RequestParam("activityId") String activityId) {
        Command<Object> cmd = new JumpCmd(executionId, activityId);

        processEngine.getManagementService().executeCommand(cmd);

        return "redirect:/bpm/console-listTasks.do";
    }

    /**
     * 更新流程之前，填写xml.
     */
    @RequestMapping("console-beforeUpdateProcess")
    public String beforeUpdateProcess(
            @RequestParam("processDefinitionId") String processDefinitionId,
            Model model) throws Exception {
        ProcessDefinition processDefinition = processEngine
                .getRepositoryService().getProcessDefinition(
                        processDefinitionId);
        InputStream is = processEngine.getRepositoryService()
                .getResourceAsStream(processDefinition.getDeploymentId(),
                        processDefinition.getResourceName());
        String xml = IoUtils.readString(is);

        model.addAttribute("xml", xml);

        return "bpm/console-beforeUpdateProcess";
    }

    /**
     * 更新流程，不生成新版本.
     */
    @RequestMapping("console-doUpdateProcess")
    public String doUpdateProcess(
            @RequestParam("processDefinitionId") String processDefinitionId,
            @RequestParam("xml") String xml) throws Exception {
        byte[] bytes = xml.getBytes("utf-8");
        UpdateProcessCmd updateProcessCmd = new UpdateProcessCmd(
                processDefinitionId, bytes);
        processEngine.getManagementService().executeCommand(updateProcessCmd);

        return "redirect:/bpm/console-listProcessInstances.do";
    }

    /**
     * 准备迁移流程.
     */
    @RequestMapping("console-migrateInput")
    public String migrateInput(
            @RequestParam("processInstanceId") String processInstanceId,
            Model model) {
        model.addAttribute("processInstanceId", processInstanceId);
        model.addAttribute("processDefinitions", processEngine
                .getRepositoryService().createProcessDefinitionQuery().list());

        return "bpm/console-migrateInput";
    }

    /**
     * 迁移流程实例.
     */
    @RequestMapping("console-migrateSave")
    public String migrateInput(
            @RequestParam("processInstanceId") String processInstanceId,
            @RequestParam("processDefinitionId") String processDefinitionId) {
        processEngine.getManagementService().executeCommand(
                new MigrateCmd(processInstanceId, processDefinitionId));

        return "redirect:/bpm/console-listProcessInstances.do";
    }

    /**
     * 重新开启流程.
     */
    @RequestMapping("console-reopen")
    public String reopen(
            @RequestParam("processInstanceId") String processInstanceId) {
        processEngine.getManagementService().executeCommand(
                new ReOpenProcessCmd(processInstanceId));

        return "redirect:/bpm/console-listHistoricProcessInstances.do";
    }

    /**
     * 添加子任务，之前，设置添加的子任务的执行人.
     */
    @RequestMapping("console-addSubTaskInput")
    public String addSubTaskInput(@RequestParam("taskId") String taskId) {
        return "bpm/console-addSubTaskInput";
    }

    /**
     * 添加子任务.
     */
    @RequestMapping("console-addSubTask")
    public String addSubTask(@RequestParam("taskId") String taskId,
            @RequestParam("userId") String userId) {
        processEngine.getManagementService().executeCommand(
                new ChangeSubTaskCmd(taskId, userId));

        return "redirect:/bpm/console-listTasks.do";
    }

    /**
     * 直接完成任务.
     */
    @RequestMapping("console-completeTask")
    public String completeTask(@RequestParam("taskId") String taskId) {
        processEngine.getTaskService().complete(taskId);

        return "redirect:/bpm/console-listTasks.do";
    }

    // ~ ======================================================================
    @Resource
    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    @Resource
    public void setProcessConnector(ProcessConnector processConnector) {
        this.processConnector = processConnector;
    }

    @Resource
    public void setBpmConfBaseManager(BpmConfBaseManager bpmConfBaseManager) {
        this.bpmConfBaseManager = bpmConfBaseManager;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
