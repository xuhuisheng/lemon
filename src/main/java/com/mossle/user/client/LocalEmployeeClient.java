package com.mossle.user.client;

import javax.annotation.Resource;

import com.mossle.api.employee.EmployeeDTO;

import com.mossle.client.employee.EmployeeClient;

import com.mossle.user.service.UserEmployeeService;

public class LocalEmployeeClient implements EmployeeClient {
    private UserEmployeeService userEmployeeService;

    public EmployeeDTO findById(String userId, String userRepoRef) {
        return userEmployeeService.findById(userId);
    }

    // ~
    @Resource
    public void setUserEmployeeService(UserEmployeeService userEmployeeService) {
        this.userEmployeeService = userEmployeeService;
    }
}
