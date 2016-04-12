package com.mossle.bpm.rs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.List;

import javax.annotation.Resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.bpm.persistence.domain.BpmProcess;
import com.mossle.bpm.persistence.manager.BpmProcessManager;

import com.mossle.core.auth.CurrentUserHolder;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.history.HistoricProcessInstance;

import org.springframework.stereotype.Component;

@Component
@Path("bpm/widget")
public class BpmWidgetResource {
    private ProcessEngine processEngine;
    private BpmProcessManager bpmProcessManager;
    private CurrentUserHolder currentUserHolder;
    private TenantHolder tenantHolder;

    @GET
    @Path("runningProcesses")
    @Produces(MediaType.TEXT_HTML)
    public String runningProcesses() {
        String userId = currentUserHolder.getUserId();
        String tenantId = tenantHolder.getTenantId();
        List<HistoricProcessInstance> historicProcessInstances = processEngine
                .getHistoryService().createHistoricProcessInstanceQuery()
                .processInstanceTenantId(tenantId).startedBy(userId)
                .unfinished().list();

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
            buff.append("      <td>" + historicProcessInstance.getId()
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

    @GET
    @Path("processes")
    @Produces(MediaType.TEXT_HTML)
    public String processes() {
        String tenantId = tenantHolder.getTenantId();
        String hql = "from BpmProcess where tenantId=? order by priority";
        List<BpmProcess> bpmProcesses = bpmProcessManager.find(hql, tenantId);

        StringBuilder buff = new StringBuilder();
        buff.append("<table class='table table-hover'>");
        buff.append("  <thead>");
        buff.append("    <tr>");
        buff.append("      <th>名称</th>");
        buff.append("      <th width='20%'>&nbsp;</th>");
        buff.append("    </tr>");
        buff.append("  </thead>");
        buff.append("  <tbody>");

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (BpmProcess bpmProcess : bpmProcesses) {
            buff.append("    <tr>");
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
