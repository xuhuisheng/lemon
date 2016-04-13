package com.mossle.plm.rs;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.mossle.api.user.UserConnector;
import com.mossle.api.user.UserDTO;

import com.mossle.core.auth.CurrentUserHolder;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.util.BaseDTO;

import com.mossle.plm.persistence.domain.PlmIssue;
import com.mossle.plm.persistence.domain.PlmSprint;
import com.mossle.plm.persistence.domain.PlmStep;
import com.mossle.plm.persistence.manager.PlmIssueManager;
import com.mossle.plm.persistence.manager.PlmSprintManager;
import com.mossle.plm.persistence.manager.PlmStepManager;
import com.mossle.plm.service.PlmLogService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
@Path("plm")
public class PlmKanbanResource {
    private static Logger logger = LoggerFactory
            .getLogger(PlmKanbanResource.class);
    private PlmIssueManager plmIssueManager;
    private PlmSprintManager plmSprintManager;
    private PlmStepManager plmStepManager;
    private PlmLogService plmLogService;
    private UserConnector userConnector;
    private CurrentUserHolder currentUserHolder;
    private BeanMapper beanMapper = new BeanMapper();

    @GET
    @Path("kanbanViewIssue")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO kanbanViewIssue(@QueryParam("issueId") Long issueId) {
        PlmIssue plmIssue = plmIssueManager.get(issueId);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("id", plmIssue.getId());
        data.put("name", plmIssue.getName());
        data.put("content", plmIssue.getContent());
        data.put("step", plmIssue.getStep());
        data.put("assigneeId", plmIssue.getAssigneeId());

        UserDTO userDto = userConnector.findById(plmIssue.getAssigneeId());
        data.put("assigneeName", userDto.getDisplayName());

        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);
        baseDto.setData(data);

        return baseDto;
    }

    /**
     * 创建任务.
     */
    @POST
    @Path("kanbanCreateIssue")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO kanbanCreateIssue(@FormParam("name") String name,
            @FormParam("content") String content,
            @FormParam("sprintId") Long sprintId,
            @FormParam("step") String step,
            @FormParam("assigneeId") String assigneeId) throws Exception {
        String userId = currentUserHolder.getUserId();
        PlmIssue plmIssue = new PlmIssue();
        plmIssue.setName(name);
        plmIssue.setContent(content);

        PlmSprint plmSprint = plmSprintManager.get(sprintId);
        plmIssue.setPlmSprint(plmSprint);
        plmIssue.setPlmProject(plmSprint.getPlmProject());
        plmIssue.setStep(step);
        plmIssue.setType("story");
        plmIssue.setStatus("active");
        plmIssue.setAssigneeId(assigneeId);
        plmIssue.setCreateTime(new Date());
        plmIssue.setReporterId(userId);
        plmIssueManager.save(plmIssue);
        plmLogService.issueCreated(plmIssue);

        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);

        return baseDto;
    }

    /**
     * 更新任务.
     */
    @POST
    @Path("kanbanUpdateIssue")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO kanbanUpdateIssue(@FormParam("id") Long id,
            @FormParam("name") String name,
            @FormParam("content") String content,
            @FormParam("step") String step,
            @FormParam("assigneeId") String assigneeId) throws Exception {
        PlmIssue plmIssue = plmIssueManager.get(id);
        plmIssue.setName(name);
        plmIssue.setContent(content);

        plmIssue.setStep(step);
        plmIssue.setAssigneeId(assigneeId);
        plmIssueManager.save(plmIssue);

        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);

        return baseDto;
    }

    /**
     * 修改步骤.
     */
    @POST
    @Path("kanbanChangeStep")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO kanbanChangeStep(@FormParam("issueId") Long issueId,
            @FormParam("step") String step) throws Exception {
        PlmIssue plmIssue = plmIssueManager.get(issueId);
        PlmIssue oldIssue = new PlmIssue();
        beanMapper.copy(plmIssue, oldIssue);

        PlmStep plmStep = plmStepManager.findUnique(
                "from PlmStep where plmConfig=? and code=?", plmIssue
                        .getPlmSprint().getPlmConfig(), step);
        plmIssue.setStep(step);

        String logType = "update";

        if ("complete".equals(plmStep.getAction())) {
            // complete
            plmIssue.setStatus("complete");
            logType = "complete";
        } else {
            if ("complete".equals(plmIssue.getStatus())) {
                // reopen
                plmIssue.setStatus("active");
                logType = "reopen";
            }
        }

        plmIssueManager.save(plmIssue);

        String userId = currentUserHolder.getUserId();

        if ("update".equals(logType)) {
            plmLogService.issueUpdated(oldIssue, plmIssue, userId);
        } else if ("complete".equals(logType)) {
            plmLogService.issueCompleted(plmIssue, userId);
        } else if ("reopen".equals(logType)) {
            plmLogService.issueReopened(plmIssue, userId);
        }

        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);

        return baseDto;
    }

    // ~ ======================================================================
    @Resource
    public void setPlmIssueManager(PlmIssueManager plmIssueManager) {
        this.plmIssueManager = plmIssueManager;
    }

    @Resource
    public void setPlmSprintManager(PlmSprintManager plmSprintManager) {
        this.plmSprintManager = plmSprintManager;
    }

    @Resource
    public void setPlmStepManager(PlmStepManager plmStepManager) {
        this.plmStepManager = plmStepManager;
    }

    @Resource
    public void setPlmLogService(PlmLogService plmLogService) {
        this.plmLogService = plmLogService;
    }

    @Resource
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }
}
