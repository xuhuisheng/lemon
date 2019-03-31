package com.mossle.bpm.rs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import com.mossle.api.humantask.HumanTaskConnector;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.page.Page;
import com.mossle.core.util.BaseDTO;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
@Path("bpm")
public class ProcessResource {
    private static Logger logger = LoggerFactory
            .getLogger(ProcessResource.class);
    private ProcessEngine processEngine;
    private HumanTaskConnector humanTaskConnector;
    private JsonMapper jsonMapper = new JsonMapper();

    /**
     * 发起流程.
     * 
     * @param processKey
     *            流程定义key
     * @param processVersion
     *            流程定义version，默认为0，自动使用最大版本
     * @param businessKey
     *            业务主键
     * @param initiator
     *            发起人
     * @param parameters
     *            json格式流程变量
     * @return 流程实例ID
     */
    @POST
    @Path("startProcess")
    public BaseDTO startProcess(@FormParam("processKey") String processKey,
            @FormParam("processVersion") @DefaultValue("0") int processVersion,
            @FormParam("businessKey") String businessKey,
            @FormParam("initiator") String initiator,
            @FormParam("parameters") String parameters) throws Exception {
        ProcessInstance processInstance = null;

        Map<String, Object> parameterMap = jsonMapper.fromJson(parameters,
                Map.class);

        processEngine.getIdentityService().setAuthenticatedUserId(initiator);

        try {
            if (processVersion <= 0) {
                processInstance = processEngine.getRuntimeService()
                        .startProcessInstanceByKey(processKey, businessKey,
                                parameterMap);
            } else {
                ProcessDefinition processDefinition = processEngine
                        .getRepositoryService().createProcessDefinitionQuery()
                        .processDefinitionKey(processKey)
                        .processDefinitionVersion(processVersion)
                        .singleResult();
                processInstance = processEngine.getRuntimeService()
                        .startProcessInstanceById(processDefinition.getId(),
                                businessKey, parameterMap);
            }
        } finally {
            processEngine.getIdentityService().setAuthenticatedUserId(null);
        }

        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);
        baseDto.setData(processInstance.getId());

        return baseDto;
    }

    /**
     * 完成任务.
     * 
     * @param humanTaskId
     *            任务ID
     * @param assignee
     *            任务操作人
     * @param action
     *            任务操作
     * @param comment
     *            任务操作意见
     * @param parameters
     *            json格式流程变量
     */
    @POST
    @Path("completeTask")
    public BaseDTO completeTask(@FormParam("humanTaskId") String humanTaskId,
            @FormParam("assignee") String assignee,
            @FormParam("action") String action,
            @FormParam("comment") String comment,
            @FormParam("parameters") String parameters) throws Exception {
        Map<String, Object> parameterMap = jsonMapper.fromJson(parameters,
                Map.class);
        humanTaskConnector.completeTask(humanTaskId, assignee, action, comment,
                parameterMap);

        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);

        return baseDto;
    }

    /**
     * 待办任务.
     */
    public BaseDTO findPersonalTasks(@FormParam("userId") String userId,
            @FormParam("pageNo") @DefaultValue("1") int pageNo,
            @FormParam("pageSize") @DefaultValue("10") int pageSize)
            throws Exception {
        Page page = this.humanTaskConnector.findPersonalTasks(userId, "1",
                pageNo, pageSize);

        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);
        baseDto.setData(page);

        return baseDto;
    }

    @Resource
    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    @Resource
    public void setHumanTaskConnector(HumanTaskConnector humanTaskConnector) {
        this.humanTaskConnector = humanTaskConnector;
    }
}
