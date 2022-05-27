package com.mossle.user.service;

import javax.annotation.Resource;

import com.mossle.api.employee.EmployeeDTO;

import com.mossle.user.persistence.domain.AccountInfo;
import com.mossle.user.persistence.domain.PersonInfo;
import com.mossle.user.persistence.manager.AccountInfoManager;
import com.mossle.user.persistence.manager.PersonInfoManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class UserEmployeeService {
    private static Logger logger = LoggerFactory
            .getLogger(UserEmployeeService.class);
    private AccountInfoManager accountInfoManager;
    private PersonInfoManager personInfoManager;

    public EmployeeDTO findById(String userId) {
        AccountInfo accountInfo = accountInfoManager.findUnique(
                "from AccountInfo where code=?", userId);
        PersonInfo personInfo = personInfoManager.findUnique(
                "from PersonInfo where code=?", userId);

        EmployeeDTO employeeDto = new EmployeeDTO();
        employeeDto.setCode(userId);
        employeeDto.setUsername(personInfo.getUsername());
        employeeDto.setName(personInfo.getDisplayName());

        // TODO: company
        employeeDto.setCompanyCode(personInfo.getCompanyCode());
        employeeDto.setCompanyName(personInfo.getCompanyName());

        employeeDto.setDepartmentCode(personInfo.getDepartmentCode());
        employeeDto.setDepartmentName(personInfo.getDepartmentName());
        // employeeDto.setDepartmentPath(personInfo.getDepartmentPath());
        // TODO: costcenter
        employeeDto.setCostCenterCode(personInfo.getCostCenterCode());
        employeeDto.setCostCenterName(personInfo.getCostCenterName());

        employeeDto.setSuperiourCode(personInfo.getSuperiourCode());
        employeeDto.setSuperiourUsername(personInfo.getSuperiourName());

        // TODO: position
        employeeDto.setPositionCode(personInfo.getPositionCode());
        employeeDto.setPositionName(personInfo.getPositionName());

        return employeeDto;
    }

    // ~
    @Resource
    public void setAccountInfoManager(AccountInfoManager accountInfoManager) {
        this.accountInfoManager = accountInfoManager;
    }

    @Resource
    public void setPersonInfoManager(PersonInfoManager personInfoManager) {
        this.personInfoManager = personInfoManager;
    }
}
