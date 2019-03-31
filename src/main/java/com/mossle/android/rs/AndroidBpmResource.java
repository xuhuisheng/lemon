package com.mossle.android.rs;

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
import com.mossle.api.keyvalue.FormParameter;
import com.mossle.api.keyvalue.Record;
import com.mossle.api.keyvalue.RecordBuilder;
import com.mossle.api.model.ModelBuilder;
import com.mossle.api.model.ModelConnector;
import com.mossle.api.model.ModelInfoDTO;
import com.mossle.api.process.ProcessConnector;
import com.mossle.api.process.ProcessDTO;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.bpm.persistence.domain.BpmProcess;
import com.mossle.bpm.persistence.manager.BpmProcessManager;

import com.mossle.client.store.StoreClient;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.page.Page;
import com.mossle.core.util.BaseDTO;

import com.mossle.operation.service.OperationService;

import com.mossle.pim.persistence.domain.PimDevice;
import com.mossle.pim.persistence.manager.PimDeviceManager;

import com.mossle.xform.Xform;
import com.mossle.xform.XformBuilder;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.history.HistoricProcessInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
@Path("android/bpm")
public class AndroidBpmResource {
    private static Logger logger = LoggerFactory
            .getLogger(AndroidBpmResource.class);
    public static final int STATUS_DRAFT_PROCESS = 0;
    public static final int STATUS_DRAFT_TASK = 1;
    public static final int STATUS_RUNNING = 2;
    private JsonMapper jsonMapper = new JsonMapper();
    private ProcessEngine processEngine;
    private BpmProcessManager bpmProcessManager;
    private TenantHolder tenantHolder;
    private PimDeviceManager pimDeviceManager;
    private ProcessConnector processConnector;
    private OperationService operationService;
    private StoreClient storeClient;
    private ModelConnector modelConnector;

    @POST
    @Path("processDefinitions")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO processDefinitions() throws Exception {
        logger.info("start");

        String hql = "from BpmProcess where tenantId=? order by priority";
        List<BpmProcess> bpmProcesses = bpmProcessManager.find(hql, "1");
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (BpmProcess bpmProcess : bpmProcesses) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name", bpmProcess.getName());
            map.put("processDefinitionId", bpmProcess.getBpmConfBase()
                    .getProcessDefinitionId());
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
    @Path("processInstances")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO processInstances(@HeaderParam("sessionId") String sessionId)
            throws Exception {
        return this.processInstancesRunning(sessionId);
    }

    @POST
    @Path("processInstancesRunning")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO processInstancesRunning(
            @HeaderParam("sessionId") String sessionId) throws Exception {
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
        List<HistoricProcessInstance> historicProcessInstances = processEngine
                .getHistoryService().createHistoricProcessInstanceQuery()
                .processInstanceTenantId(tenantId).startedBy(userId)
                .unfinished().list();

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (HistoricProcessInstance historicProcessInstance : historicProcessInstances) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name", historicProcessInstance.getName());
            map.put("processInstanceId", historicProcessInstance.getId());
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
    @Path("processInstancesComplete")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO processInstancesComplete(
            @HeaderParam("sessionId") String sessionId) throws Exception {
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
        List<HistoricProcessInstance> historicProcessInstances = processEngine
                .getHistoryService().createHistoricProcessInstanceQuery()
                .processInstanceTenantId(tenantId).startedBy(userId).finished()
                .list();

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (HistoricProcessInstance historicProcessInstance : historicProcessInstances) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name", historicProcessInstance.getName());
            map.put("processInstanceId", historicProcessInstance.getId());
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
    @Path("processInstancesDraft")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO processInstancesDraft(
            @HeaderParam("sessionId") String sessionId) throws Exception {
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

        // List<Record> records = keyValueConnector.findByStatus(
        // STATUS_DRAFT_PROCESS, userId, tenantId);
        Page page = modelConnector.findDraft(1, 100, userId);
        List<ModelInfoDTO> modelInfoDtos = (List<ModelInfoDTO>) page
                .getResult();

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (ModelInfoDTO modelInfoDto : modelInfoDtos) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name", modelInfoDto.getName());
            // map.put("formTemplateCode", modelInfoDto.getFormTemplateCode());
            map.put("code", modelInfoDto.getCode());
            map.put("category", modelInfoDto.getCategory());
            map.put("status", modelInfoDto.getStatus());
            // map.put("ref", modelInfoDto.getRef());
            map.put("createTime", modelInfoDto.getCreateTime());
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
    @Path("startProcess")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseDTO startProcess(@HeaderParam("sessionId") String sessionId,
            @FormParam("processDefinitionId") String processDefinitionId,
            @FormParam("data") String data) throws Exception {
        logger.info("start : {} {}", processDefinitionId, data);

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
        String hql = "from BpmProcess where bpmConfBase.processDefinitionId=? order by priority";
        BpmProcess bpmProcess = bpmProcessManager.findUnique(hql,
                processDefinitionId);

        Map<String, Object> map = jsonMapper.fromJson(data, Map.class);
        map.put("bpmProcessId", Long.toString(bpmProcess.getId()));

        FormParameter formParameter = this.doSaveRecord(map, userId, tenantId);

        // doConfirmStartProcess(formParameter, model);
        // Record record = keyValueConnector.findByCode(formParameter
        // .getBusinessKey());
        ModelInfoDTO modelInfoDto = modelConnector.findByCode(formParameter
                .getBusinessKey());
        ProcessDTO processDto = processConnector.findProcess(formParameter
                .getBpmProcessId());

        // String processDefinitionId = processDto.getProcessDefinitionId();

        // 获得form的信息
        FormDTO formDto = processConnector.findStartForm(processDefinitionId);

        Xform xform = new XformBuilder().setStoreClient(storeClient)
                .setContent(formDto.getContent()).setModelInfoDto(modelInfoDto)
                .build();
        Map<String, Object> processParameters = xform.getMapData();
        logger.info("processParameters : {}", processParameters);

        String processInstanceId = processConnector.startProcess(userId,
                formParameter.getBusinessKey(), processDefinitionId,
                processParameters);

        // record = new RecordBuilder().build(record, STATUS_RUNNING,
        // processInstanceId);
        // keyValueConnector.save(record);
        modelInfoDto.setStatus("active");
        modelConnector.save(modelInfoDto);

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
        formParameter.setBpmProcessId((String) map.get("bpmProcessId"));

        String businessKey = operationService.saveDraft(userId, tenantId,
                formParameter);

        if ((formParameter.getBusinessKey() == null)
                || "".equals(formParameter.getBusinessKey().trim())) {
            formParameter.setBusinessKey(businessKey);
        }

        // Record record = keyValueConnector.findByCode(businessKey);
        // record = new RecordBuilder().build(record, multiValueMap, tenantId);
        // keyValueConnector.save(record);
        ModelInfoDTO modelInfoDto = modelConnector.findByCode(businessKey);
        modelInfoDto = new ModelBuilder().build(modelInfoDto, multiValueMap,
                tenantId);
        modelConnector.save(modelInfoDto);

        return formParameter;
    }

    // ~ ======================================================================
    @Resource
    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    @Resource
    public void setBpmProcessManager(BpmProcessManager bpmProcessManager) {
        this.bpmProcessManager = bpmProcessManager;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }

    @Resource
    public void setPimDeviceManager(PimDeviceManager pimDeviceManager) {
        this.pimDeviceManager = pimDeviceManager;
    }

    @Resource
    public void setProcessConnector(ProcessConnector processConnector) {
        this.processConnector = processConnector;
    }

    @Resource
    public void setOperationService(OperationService operationService) {
        this.operationService = operationService;
    }

    @Resource
    public void setStoreClient(StoreClient storeClient) {
        this.storeClient = storeClient;
    }

    @Resource
    public void setModelConnector(ModelConnector modelConnector) {
        this.modelConnector = modelConnector;
    }
}
