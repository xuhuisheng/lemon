package com.mossle.humantask.web.portal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.api.humantask.HumanTaskConnector;
import com.mossle.api.humantask.HumanTaskDTO;
import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.page.Page;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("humantask/portal")
public class HumantaskPortalController {
    private static Logger logger = LoggerFactory
            .getLogger(HumantaskPortalController.class);
    private HumanTaskConnector humanTaskConnector;
    private CurrentUserHolder currentUserHolder;
    private TenantHolder tenantHolder;

    @RequestMapping("personalTasks")
    public String personalTasks() {
        String userId = currentUserHolder.getUserId();
        String tenantId = tenantHolder.getTenantId();
        Page page = humanTaskConnector.findPersonalTasks(userId, tenantId, 1,
                10);
        List<HumanTaskDTO> humanTaskDtos = (List<HumanTaskDTO>) page
                .getResult();

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

        for (HumanTaskDTO humanTaskDto : humanTaskDtos) {
            buff.append("    <tr>");
            buff.append("      <td>" + humanTaskDto.getBusinessKey() + "</td>");
            buff.append("      <td>" + humanTaskDto.getPresentationSubject()
                    + "</td>");
            buff.append("      <td>");
            buff.append("        <a href='" + ".."
                    + "/operation/task-operation-viewTaskForm.do?humanTaskId="
                    + humanTaskDto.getId()
                    + "' class='btn btn-xs btn-primary'>处理</a>");
            buff.append("      </td>");
            buff.append("    </tr>");
        }

        buff.append("  </tbody>");
        buff.append("</table>");

        return buff.toString();
    }

    @Resource
    public void setHumanTaskConnector(HumanTaskConnector humanTaskConnector) {
        this.humanTaskConnector = humanTaskConnector;
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
