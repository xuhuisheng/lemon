package com.mossle.api.org;

public interface EmployeeConnector {
    EmployeeDTO findByCode(String code);
}
