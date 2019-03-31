package com.mossle.party.support;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.employee.EmployeeConnector;
import com.mossle.api.employee.EmployeeDTO;
import com.mossle.api.org.OrgConnector;
import com.mossle.api.org.OrgDTO;
import com.mossle.api.user.UserDTO;

import com.mossle.client.user.UserClient;

public class PartyEmployeeConnector implements EmployeeConnector {
    private UserClient userClient;
    private OrgConnector orgConnector;

    public EmployeeDTO findByCode(String code, String tenantId) {
        UserDTO userDto = userClient.findById(code, tenantId);

        if (userDto == null) {
            return null;
        }

        OrgDTO company = orgConnector.findCompany(code);
        List<OrgDTO> orgs = orgConnector.getOrgsByUserId(code);
        OrgDTO department = this.findDepartment(orgs);
        String superiourCode = orgConnector.getSuperiorId(code);
        UserDTO superiour = userClient.findById(superiourCode, tenantId);
        OrgDTO position = orgConnector.findPositionByUserId(code);

        EmployeeDTO employeeDto = new EmployeeDTO();

        employeeDto.setCode(code);
        employeeDto.setUsername(userDto.getUsername());
        employeeDto.setName(userDto.getDisplayName());

        if (company != null) {
            employeeDto.setCompanyCode(company.getRef());
            employeeDto.setCompanyName(company.getName());
        }

        if (department != null) {
            employeeDto.setDepartmentCode(department.getRef());
            employeeDto.setDepartmentName(department.getName());
            employeeDto.setCostCenterCode(department.getRef());
            employeeDto.setCostCenterName(department.getName());
        }

        if (superiour != null) {
            employeeDto.setSuperiourCode(superiour.getId());
            employeeDto.setSuperiourUsername(superiour.getUsername());
        }

        if (position != null) {
            employeeDto.setPositionCode(position.getRef());
            employeeDto.setPositionName(position.getName());
        }

        return employeeDto;
    }

    public OrgDTO findDepartment(List<OrgDTO> orgs) {
        for (OrgDTO org : orgs) {
            if ("部门".equals(org.getTypeName())) {
                return org;
            }
        }

        return null;
    }

    @Resource
    public void setUserClient(UserClient userClient) {
        this.userClient = userClient;
    }

    @Resource
    public void setOrgConnector(OrgConnector orgConnector) {
        this.orgConnector = orgConnector;
    }
}
