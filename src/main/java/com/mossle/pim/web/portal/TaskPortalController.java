package com.mossle.pim.web.portal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.core.page.Page;

import com.mossle.pim.persistence.domain.PimTask;
import com.mossle.pim.persistence.manager.PimTaskManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("pim/portal")
public class TaskPortalController {
    private static Logger logger = LoggerFactory
            .getLogger(TaskPortalController.class);
    private CurrentUserHolder currentUserHolder;
    private TenantHolder tenantHolder;
    private PimTaskManager pimTaskManager;

    @RequestMapping("tasks")
    public String personalTasks() {
        String userId = currentUserHolder.getUserId();
        String tenantId = tenantHolder.getTenantId();

        String hql = "from PimTask where userId=? and status='active' order by priority";
        Page page = this.pimTaskManager.pagedQuery(hql, 1, 10, userId);

        List<PimTask> pimTasks = (List<PimTask>) page.getResult();

        StringBuilder buff = new StringBuilder();
        buff.append("<table class='table table-hover'>");
        buff.append("  <thead>");
        buff.append("    <tr>");
        buff.append("      <th>名称</th>");
        buff.append("      <th>时间</th>");
        buff.append("      <th width='20%'>&nbsp;</th>");
        buff.append("    </tr>");
        buff.append("  </thead>");
        buff.append("  <tbody>");

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (PimTask pimTask : pimTasks) {
            buff.append("    <tr>");
            buff.append("      <td>" + pimTask.getName() + "</td>");
            buff.append("      <td>"
                    + dateFormat.format(pimTask.getCreateTime()) + "</td>");
            buff.append("      <td>");
            buff.append("        <a href='" + ".."
                    + "/pim/pim-task-input.do?id=" + pimTask.getId()
                    + "' class='btn btn-xs btn-primary'>处理</a>");
            buff.append("      </td>");
            buff.append("    </tr>");
        }

        buff.append("  </tbody>");
        buff.append("</table>");

        return buff.toString();
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }

    @Resource
    public void setPimTaskManager(PimTaskManager pimTaskManager) {
        this.pimTaskManager = pimTaskManager;
    }
}
