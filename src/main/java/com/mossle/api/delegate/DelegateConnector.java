package com.mossle.api.delegate;

public interface DelegateConnector {
    String findAttorney(String userId, String processDefinitionId,
            String taskDefinitionKey, String tenantId);

    void recordDelegate(String userId, String attorney, String taskId,
            String tenantId);

    void cancel(String taskId, String userId, String tenantId);

    void complete(String taskId, String userId, String tenantId);
}
