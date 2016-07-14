package com.mossle.api.delegate;

public class MockDelegateConnector implements DelegateConnector {
    public String findAttorney(String userId, String processDefinitionId,
            String taskDefinitionKey, String tenantId) {
        return null;
    }

    public void recordDelegate(String userId, String attorney, String taskId,
            String tenantId) {
    }

    public void cancel(String taskId, String userId, String tenantId) {
    }

    public void complete(String taskId, String complete, String tenantId) {
    }
}
