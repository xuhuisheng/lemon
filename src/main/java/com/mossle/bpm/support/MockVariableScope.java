package com.mossle.bpm.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.mossle.api.model.ModelInfoDTO;

import org.activiti.engine.impl.persistence.entity.VariableInstanceEntity;
import org.activiti.engine.impl.persistence.entity.VariableScopeImpl;
import org.activiti.engine.impl.variable.IntegerType;
import org.activiti.engine.impl.variable.SerializableType;
import org.activiti.engine.impl.variable.StringType;
import org.activiti.engine.impl.variable.VariableType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockVariableScope extends VariableScopeImpl {
    private static Logger logger = LoggerFactory
            .getLogger(MockVariableScope.class);

    public MockVariableScope(Integer eventCode, String eventName,
            ModelInfoDTO modelInfo, String userId, String activityId,
            String activityName) {
        usedVariablesCache.put("eventCode", VariableInstanceEntity.create(
                "eventCode", new IntegerType(), eventCode));
        usedVariablesCache.put("eventName", VariableInstanceEntity.create(
                "eventName", new StringType(64), eventName));
        usedVariablesCache.put("modelInfo", VariableInstanceEntity.create(
                "modelInfo", new SerializableType(), modelInfo));
        usedVariablesCache.put("userId", VariableInstanceEntity.create(
                "userId", new StringType(64), userId));
        usedVariablesCache.put("activityId", VariableInstanceEntity.create(
                "activityId", new StringType(64), activityId));
        usedVariablesCache.put("activityName", VariableInstanceEntity.create(
                "activityName", new StringType(64), activityName));
    }

    protected List<VariableInstanceEntity> loadVariableInstances() {
        logger.debug("loadVariableInstances");

        return new ArrayList<VariableInstanceEntity>(
                usedVariablesCache.values());
    }

    protected VariableScopeImpl getParentVariableScope() {
        logger.debug("getParentVariableScope");

        return null;
    }

    protected void initializeVariableInstanceBackPointer(
            VariableInstanceEntity variableInstance) {
        logger.debug("initializeVariableInstanceBackPointer");
    }

    protected VariableInstanceEntity getSpecificVariable(String variableName) {
        logger.debug("getSpecificVariable");

        return null;
    }

    protected List<VariableInstanceEntity> getSpecificVariables(
            Collection<String> variableNames) {
        logger.debug("getSpecificVariables");

        return Collections.emptyList();
    }
}
