package com.mossle.form.service;

import java.util.Map;

import javax.annotation.Resource;

import com.mossle.form.domain.DynamicModel;
import com.mossle.form.domain.DynamicModelData;
import com.mossle.form.manager.DynamicModelDataManager;
import com.mossle.form.manager.DynamicModelManager;

import org.springframework.stereotype.Service;

@Service
public class ModelService {
    public static final int STATUS_DRAFT = 0;
    public static final int STATUS_COMPLETE = 1;
    private DynamicModelManager dynamicModelManager;
    private DynamicModelDataManager dynamicModelDataManager;

    public void saveProcessInstanceDraft(Long businessKey,
            String processDefinitionId, Map<String, String> parameters) {
        DynamicModel dynamicModel = saveDynamicModel(businessKey, parameters);
        dynamicModel.setDefinitionId(processDefinitionId);
        dynamicModel.setStatus(STATUS_DRAFT);
        dynamicModelManager.save(dynamicModel);
    }

    public void saveProcessInstance(Long businessKey,
            String processDefinitionId, String processInstanceId,
            Map<String, String> parameters) {
        DynamicModel dynamicModel = saveDynamicModel(businessKey, parameters);
        dynamicModel.setDefinitionId(processDefinitionId);
        dynamicModel.setInstanceId(processInstanceId);
        dynamicModel.setExecutionId(processInstanceId);
        dynamicModel.setStatus(STATUS_COMPLETE);
        dynamicModelManager.save(dynamicModel);
    }

    public void saveTaskDraft(Long businessKey, String executionId,
            Map<String, String> parameters) {
        DynamicModel dynamicModel = saveDynamicModel(businessKey, parameters);
        dynamicModel.setExecutionId(executionId);
        dynamicModel.setStatus(STATUS_DRAFT);
        dynamicModelManager.save(dynamicModel);
    }

    public void saveTask(Long businessKey, String executionId,
            Map<String, String> parameters) {
        DynamicModel dynamicModel = saveDynamicModel(businessKey, parameters);
        dynamicModel.setExecutionId(executionId);
        dynamicModel.setStatus(STATUS_COMPLETE);
        dynamicModelManager.save(dynamicModel);
    }

    private DynamicModel saveDynamicModel(Long businessKey,
            Map<String, String> parameters) {
        DynamicModel dynamicModel = createOrGetDynamicModel(businessKey);

        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            DynamicModelData dynamicModelData = this
                    .createOrGetDynamicModelData(entry.getKey(), dynamicModel);

            dynamicModelData.setValue(entry.getValue());
            dynamicModel.getDynamicModelDatas().add(dynamicModelData);
        }

        dynamicModelManager.save(dynamicModel);

        return dynamicModel;
    }

    public DynamicModel createOrGetDynamicModel(Long businessKey) {
        DynamicModel dynamicModel = null;

        if (businessKey == null) {
            dynamicModel = new DynamicModel();
        } else {
            dynamicModel = dynamicModelManager.get(businessKey);
        }

        dynamicModelManager.save(dynamicModel);

        return dynamicModel;
    }

    public DynamicModelData createOrGetDynamicModelData(String name,
            DynamicModel dynamicModel) {
        DynamicModelData dynamicModelData = dynamicModelDataManager.findUnique(
                "from DynamicModelData where name=? and dynamicModel=?", name,
                dynamicModel);

        if (dynamicModelData == null) {
            dynamicModelData = new DynamicModelData();
            dynamicModelData.setDynamicModel(dynamicModel);
            dynamicModelData.setName(name);
            dynamicModelDataManager.save(dynamicModelData);
        }

        return dynamicModelData;
    }

    @Resource
    public void setDynamicModelManager(DynamicModelManager dynamicModelManager) {
        this.dynamicModelManager = dynamicModelManager;
    }

    @Resource
    public void setDynamicModelDataManager(
            DynamicModelDataManager dynamicModelDataManager) {
        this.dynamicModelDataManager = dynamicModelDataManager;
    }
}
