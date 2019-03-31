package com.mossle.api.employee;

public interface EmployeeConnector {
    EmployeeDTO findByCode(String code, String tenantId);
}
