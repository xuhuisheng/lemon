package com.mossle.android.rs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mossle.api.form.FormDTO;
import com.mossle.api.humantask.HumanTaskConnector;
import com.mossle.api.humantask.HumanTaskDTO;
import com.mossle.api.keyvalue.FormParameter;
import com.mossle.api.keyvalue.KeyValueConnector;
import com.mossle.api.keyvalue.Record;
import com.mossle.api.keyvalue.RecordBuilder;
import com.mossle.api.store.StoreConnector;
import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserConnector;

import com.mossle.bpm.persistence.manager.BpmProcessManager;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.page.Page;
import com.mossle.core.util.BaseDTO;

import com.mossle.operation.service.OperationService;

import com.mossle.pim.persistence.domain.PimDevice;
import com.mossle.pim.persistence.manager.PimDeviceManager;

import com.mossle.xform.Xform;
import com.mossle.xform.XformBuilder;

import org.activiti.engine.ProcessEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
@Path("android/task")
public class AndroidTaskResource {
    private static Logger logger = LoggerFactory
            .getLogger(AndroidTaskResource.class);
    public static final int STATUS_DRAFT_PROCESS = 0;
    public static final int STATUS_DRAFT_TASK = 1;
    public static final int STATUS_RUNNING = 2;
    private JsonMapper jsonMapper = new JsonMapper();
    private ProcessEngine processEngine;
    private BpmProcessManager bpmProcessManager;
    private TenantHolder tenantHolder;
    private HumanTaskConnector humanTaskConnector;
    private PimDeviceManager pimDeviceManager;
    private OperationService operationService;
    private KeyValueConnector keyValueConnector;
    private StoreConnector storeConnector;
    private UserConnector userConnector;

    @POST
    @Path("tasks")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO tasks(@HeaderParam("sessionId") String sessionId)
            throws Exception {
        logger.info("start");

        PimDevice pimDevice = pimDeviceManager.findUniqueBy("sessionId",
                sessionId);

        if (pimDevice == null) {
            BaseDTO result = new BaseDTO();
            result.setCode(401);
            result.setMessage("auth fail");

            return result;
        }

        String userId = pimDevice.getUserId();
        String tenantId = "1";
        Page page = humanTaskConnector.findPersonalTasks(userId, tenantId, 1,
                10);
        List<HumanTaskDTO> humanTaskDtos = (List<HumanTaskDTO>) page
                .getResult();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (HumanTaskDTO humanTaskDto : humanTaskDtos) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", humanTaskDto.getId());
            map.put("name", humanTaskDto.getName());
            map.put("presentationSubject",
                    humanTaskDto.getPresentationSubject());
            map.put("createTime",
                    dateFormat.format(humanTaskDto.getCreateTime()));
            map.put("assignee", humanTaskDto.getAssignee());
            map.put("assigneeDisplayName", userConnector.findById(userId)
                    .getDisplayName());
            list.add(map);
        }

        String json = jsonMapper.toJson(list);
        BaseDTO result = new BaseDTO();
        result.setCode(200);
        result.setData(json);
        logger.info("end");

        return result;
    }

    @POST
    @Path("tasksPersonal")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO tasksPersonal(@HeaderParam("sessionId") String sessionId)
            throws Exception {
        logger.info("start");

        PimDevice pimDevice = pimDeviceManager.findUniqueBy("sessionId",
                sessionId);

        if (pimDevice == null) {
            BaseDTO result = new BaseDTO();
            result.setCode(401);
            result.setMessage("auth fail");

            return result;
        }

        String userId = pimDevice.getUserId();
        String tenantId = "1";
        Page page = humanTaskConnector.findPersonalTasks(userId, tenantId, 1,
                10);
        List<HumanTaskDTO> humanTaskDtos = (List<HumanTaskDTO>) page
                .getResult();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (HumanTaskDTO humanTaskDto : humanTaskDtos) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", humanTaskDto.getId());
            map.put("name", humanTaskDto.getName());
            map.put("presentationSubject",
                    humanTaskDto.getPresentationSubject());
            map.put("createTime",
                    dateFormat.format(humanTaskDto.getCreateTime()));
            map.put("assignee", humanTaskDto.getAssignee());
            map.put("assigneeDisplayName", userConnector.findById(userId)
                    .getDisplayName());
            list.add(map);
        }

        String json = jsonMapper.toJson(list);
        BaseDTO result = new BaseDTO();
        result.setCode(200);
        result.setData(json);
        logger.info("end");

        return result;
    }

    @POST
    @Path("tasksGroup")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO tasksGroup(@HeaderParam("sessionId") String sessionId)
            throws Exception {
        logger.info("start");

        PimDevice pimDevice = pimDeviceManager.findUniqueBy("sessionId",
                sessionId);

        if (pimDevice == null) {
            BaseDTO result = new BaseDTO();
            result.setCode(401);
            result.setMessage("auth fail");

            return result;
        }

        String userId = pimDevice.getUserId();
        String tenantId = "1";
        Page page = humanTaskConnector.findGroupTasks(userId, tenantId, 1, 10);
        List<HumanTaskDTO> humanTaskDtos = (List<HumanTaskDTO>) page
                .getResult();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (HumanTaskDTO humanTaskDto : humanTaskDtos) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", humanTaskDto.getId());
            map.put("name", humanTaskDto.getName());
            map.put("presentationSubject",
                    humanTaskDto.getPresentationSubject());
            map.put("createTime",
                    dateFormat.format(humanTaskDto.getCreateTime()));
            map.put("assignee", humanTaskDto.getAssignee());
            map.put("assigneeDisplayName", userConnector.findById(userId)
                    .getDisplayName());
            list.add(map);
        }

        String json = jsonMapper.toJson(list);
        BaseDTO result = new BaseDTO();
        result.setCode(200);
        result.setData(json);
        logger.info("end");

        return result;
    }

    @POST
    @Path("tasksComplete")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO tasksComplete(@HeaderParam("sessionId") String sessionId)
            throws Exception {
        logger.info("start");

        PimDevice pimDevice = pimDeviceManager.findUniqueBy("sessionId",
                sessionId);

        if (pimDevice == null) {
            BaseDTO result = new BaseDTO();
            result.setCode(401);
            result.setMessage("auth fail");

            return result;
        }

        String userId = pimDevice.getUserId();
        String tenantId = "1";
        Page page = humanTaskConnector.findFinishedTasks(userId, tenantId, 1,
                10);
        List<HumanTaskDTO> humanTaskDtos = (List<HumanTaskDTO>) page
                .getResult();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (HumanTaskDTO humanTaskDto : humanTaskDtos) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", humanTaskDto.getId());
            map.put("name", humanTaskDto.getName());
            map.put("presentationSubject",
                    humanTaskDto.getPresentationSubject());
            map.put("createTime",
                    dateFormat.format(humanTaskDto.getCreateTime()));
            map.put("assignee", humanTaskDto.getAssignee());
            map.put("assigneeDisplayName", userConnector.findById(userId)
                    .getDisplayName());
            list.add(map);
        }

        String json = jsonMapper.toJson(list);
        BaseDTO result = new BaseDTO();
        result.setCode(200);
        result.setData(json);
        logger.info("end");

        return result;
    }

    @POST
    @Path("tasksDelegate")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO tasksDelegate(@HeaderParam("sessionId") String sessionId)
            throws Exception {
        logger.info("start");

        PimDevice pimDevice = pimDeviceManager.findUniqueBy("sessionId",
                sessionId);

        if (pimDevice == null) {
            BaseDTO result = new BaseDTO();
            result.setCode(401);
            result.setMessage("auth fail");

            return result;
        }

        String userId = pimDevice.getUserId();
        String tenantId = "1";
        Page page = humanTaskConnector.findDelegateTasks(userId, tenantId, 1,
                10);
        List<HumanTaskDTO> humanTaskDtos = (List<HumanTaskDTO>) page
                .getResult();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (HumanTaskDTO humanTaskDto : humanTaskDtos) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", humanTaskDto.getId());
            map.put("name", humanTaskDto.getName());
            map.put("presentationSubject",
                    humanTaskDto.getPresentationSubject());
            map.put("createTime",
                    dateFormat.format(humanTaskDto.getCreateTime()));
            map.put("assignee", humanTaskDto.getAssignee());
            map.put("assigneeDisplayName", userConnector.findById(userId)
                    .getDisplayName());
            list.add(map);
        }

        String json = jsonMapper.toJson(list);
        BaseDTO result = new BaseDTO();
        result.setCode(200);
        result.setData(json);
        logger.info("end");

        return result;
    }

    @POST
    @Path("completeTask")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO completeTask(@HeaderParam("sessionId") String sessionId,
            @FormParam("taskId") String taskId, @FormParam("data") String data)
            throws Exception {
        logger.info("start : {} {}", taskId, data);

        String humanTaskId = taskId;

        PimDevice pimDevice = pimDeviceManager.findUniqueBy("sessionId",
                sessionId);

        if (pimDevice == null) {
            BaseDTO result = new BaseDTO();
            result.setCode(401);
            result.setMessage("auth fail");

            return result;
        }

        String userId = pimDevice.getUserId();
        String tenantId = "1";

        Map<String, Object> map = jsonMapper.fromJson(data, Map.class);
        map.put("taskId", humanTaskId);

        Record record = null;
        FormParameter formParameter = null;
        HumanTaskDTO humanTaskDto = null;
        FormDTO formDto = null;

        formParameter = doSaveRecord(map, userId, tenantId);

        humanTaskId = formParameter.getHumanTaskId();
        operationService.saveDraft(userId, tenantId, formParameter);

        formDto = humanTaskConnector.findTaskForm(humanTaskId);

        humanTaskDto = humanTaskConnector.findHumanTask(humanTaskId);

        String processInstanceId = humanTaskDto.getProcessInstanceId();
        record = keyValueConnector.findByRef(processInstanceId);

        record = new RecordBuilder().build(record,
                formParameter.getMultiValueMap(), tenantId);

        keyValueConnector.save(record);

        Xform xform = new XformBuilder().setStoreConnector(storeConnector)
                .setContent(formDto.getContent()).setRecord(record).build();
        Map<String, Object> taskParameters = xform.getMapData();
        logger.info("taskParameters : {}", taskParameters);

        String comment = "";
        String action = "";

        try {
            humanTaskConnector.completeTask(humanTaskId, userId, action,
                    comment, taskParameters);
        } catch (IllegalStateException ex) {
            logger.error(ex.getMessage(), ex);

            return null;
        }

        if (record == null) {
            record = new Record();
        }

        record = new RecordBuilder().build(record, STATUS_RUNNING,
                humanTaskDto.getProcessInstanceId());
        keyValueConnector.save(record);

        BaseDTO result = new BaseDTO();
        result.setCode(200);
        result.setData(data);
        logger.info("end");

        return result;
    }

    /**
     * 把数据先保存到keyvalue里.
     */
    public FormParameter doSaveRecord(Map<String, Object> map, String userId,
            String tenantId) throws Exception {
        FormParameter formParameter = new FormParameter();
        MultiValueMap multiValueMap = new LinkedMultiValueMap();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            multiValueMap.add(entry.getKey(), entry.getValue());
        }

        formParameter.setMultiValueMap(multiValueMap);
        formParameter.setHumanTaskId((String) map.get("taskId"));

        String businessKey = operationService.saveDraft(userId, tenantId,
                formParameter);

        if ((formParameter.getBusinessKey() == null)
                || "".equals(formParameter.getBusinessKey().trim())) {
            formParameter.setBusinessKey(businessKey);
        }

        Record record = keyValueConnector.findByCode(businessKey);

        record = new RecordBuilder().build(record, multiValueMap, tenantId);

        keyValueConnector.save(record);

        return formParameter;
    }

    // ~ ======================================================================
    @Resource
    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }

    @Resource
    public void setHumanTaskConnector(HumanTaskConnector humanTaskConnector) {
        this.humanTaskConnector = humanTaskConnector;
    }

    @Resource
    public void setPimDeviceManager(PimDeviceManager pimDeviceManager) {
        this.pimDeviceManager = pimDeviceManager;
    }

    @Resource
    public void setOperationService(OperationService operationService) {
        this.operationService = operationService;
    }

    @Resource
    public void setKeyValueConnector(KeyValueConnector keyValueConnector) {
        this.keyValueConnector = keyValueConnector;
    }

    @Resource
    public void setStoreConnector(StoreConnector storeConnector) {
        this.storeConnector = storeConnector;
    }

    @Resource
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }
}
