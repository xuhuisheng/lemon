package com.mossle.client.employee;

import com.mossle.api.employee.EmployeeDTO;

public interface EmployeeClient {
    EmployeeDTO findById(String userId, String tenantId);
}
