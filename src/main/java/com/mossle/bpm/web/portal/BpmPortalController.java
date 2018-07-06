package com.mossle.bpm.web.portal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.api.humantask.HumanTaskConnector;
import com.mossle.api.humantask.HumanTaskDTO;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.bpm.persistence.domain.BpmProcess;
import com.mossle.bpm.persistence.manager.BpmProcessManager;

import com.mossle.core.page.Page;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.history.HistoricProcessInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("bpm/portal")
public class BpmPortalController {
    private static Logger logger = LoggerFactory
            .getLogger(BpmPortalController.class);
    private ProcessEngine processEngine;
    private BpmProcessManager bpmProcessManager;
    private CurrentUserHolder currentUserHolder;
    private TenantHolder tenantHolder;

    @RequestMapping("runningProcesses")
    public String runningProcesses() {
        String userId = currentUserHolder.getUserId();
        String tenantId = tenantHolder.getTenantId();
        List<HistoricProcessInstance> historicProcessInstances = processEngine
                .getHistoryService().createHistoricProcessInstanceQuery()
                .processInstanceTenantId(tenantId).startedBy(userId)
                .unfinished().orderByProcessInstanceStartTime().desc().list();

        StringBuilder buff = new StringBuilder();
        buff.append("<table class='table table-hover'>");
        buff.append("  <thead>");
        buff.append("    <tr>");
        buff.append("      <th>编号</th>");
        buff.append("      <th>名称</th>");
        buff.append("      <th width='20%'>&nbsp;</th>");
        buff.append("    </tr>");
        buff.append("  </thead>");
        buff.append("  <tbody>");

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (HistoricProcessInstance historicProcessInstance : historicProcessInstances) {
            buff.append("    <tr>");
            buff.append("      <td>" + historicProcessInstance.getBusinessKey()
                    + "</td>");
            buff.append("      <td>" + historicProcessInstance.getName()
                    + "</td>");
            buff.append("      <td>");
            buff.append("        <a href='" + ".."
                    + "/bpm/workspace-viewHistory.do?processInstanceId="
                    + historicProcessInstance.getId()
                    + "' class='btn btn-xs btn-primary'>详情</a>");
            buff.append("      </td>");
            buff.append("    </tr>");
        }

        buff.append("  </tbody>");
        buff.append("</table>");

        return buff.toString();
    }

    @RequestMapping("processes")
    public String processes() {
        String tenantId = tenantHolder.getTenantId();
        String hql = "from BpmProcess where tenantId=? order by priority";
        List<BpmProcess> bpmProcesses = bpmProcessManager.find(hql, tenantId);

        StringBuilder buff = new StringBuilder();
        buff.append("<table class='table table-hover'>");
        buff.append("  <thead>");
        buff.append("    <tr>");
        buff.append("      <th>名称</th>");
        buff.append("      <th width='15%'>&nbsp;</th>");
        buff.append("    </tr>");
        buff.append("  </thead>");
        buff.append("  <tbody>");

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (BpmProcess bpmProcess : bpmProcesses) {
            buff.append("    <tr>");
            // buff.append("      <td>" + bpmProcess.getName() + "(" + bpmProcess.getCode() + ")</td>");
            buff.append("      <td>" + bpmProcess.getName() + "</td>");
            buff.append("      <td>");
            buff.append("        <a href='"
                    + ".."
                    + "/operation/process-operation-viewStartForm.do?bpmProcessId="
                    + bpmProcess.getId()
                    + "' class='btn btn-xs btn-primary'>发起</a>");
            buff.append("      </td>");
            buff.append("    </tr>");
        }

        buff.append("  </tbody>");
        buff.append("</table>");

        return buff.toString();
    }

    @Resource
    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    @Resource
    public void setBpmProcessManager(BpmProcessManager bpmProcessManager) {
        this.bpmProcessManager = bpmProcessManager;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
