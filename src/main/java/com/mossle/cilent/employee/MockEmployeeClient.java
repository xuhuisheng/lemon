package com.mossle.client.employee;

import com.mossle.api.employee.EmployeeDTO;

public class MockEmployeeClient implements EmployeeClient {
    public EmployeeDTO findById(String userId, String tenantId) {
        return null;
    }
}
