package com.mossle.party.support;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.employee.EmployeeDTO;
import com.mossle.api.org.OrgConnector;
import com.mossle.api.org.OrgDTO;
import com.mossle.api.user.UserDTO;

import com.mossle.client.employee.EmployeeClient;
import com.mossle.client.user.UserClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PartyEmployeeClient implements EmployeeClient {
    private static Logger logger = LoggerFactory
            .getLogger(PartyEmployeeClient.class);
    private UserClient userClient;
    private OrgConnector orgConnector;

    public EmployeeDTO findById(String userId, String tenantId) {
        UserDTO userDto = userClient.findById(userId, tenantId);

        if (userDto == null) {
            logger.info("cannot find user : {} {}", userId, tenantId);

            return null;
        }

        OrgDTO company = orgConnector.findCompany(userId);
        List<OrgDTO> orgs = orgConnector.getOrgsByUserId(userId);
        OrgDTO department = this.findDepartment(orgs);
        String departmentPath = this.findDepartmentPath(orgs);
        String superiourCode = orgConnector.getSuperiorId(userId);
        UserDTO superiour = userClient.findById(superiourCode, tenantId);
        OrgDTO position = orgConnector.findPositionByUserId(userId);

        EmployeeDTO employeeDto = new EmployeeDTO();

        employeeDto.setCode(userId);
        employeeDto.setUsername(userDto.getUsername());
        employeeDto.setName(userDto.getDisplayName());

        if (company != null) {
            // TODO: company
            employeeDto.setCompanyCode(company.getRef());
            employeeDto.setCompanyName(company.getName());
        }

        if (department != null) {
            employeeDto.setDepartmentCode(department.getRef());
            employeeDto.setDepartmentName(department.getName());
            employeeDto.setDepartmentPath(departmentPath);
            // TODO: costcenter
            employeeDto.setCostCenterCode(department.getRef());
            employeeDto.setCostCenterName(department.getName());
        }

        if (superiour != null) {
            employeeDto.setSuperiourCode(superiour.getId());
            employeeDto.setSuperiourUsername(superiour.getUsername());
        }

        if (position != null) {
            // TODO: position
            employeeDto.setPositionCode(position.getRef());
            employeeDto.setPositionName(position.getName());
        }

        return employeeDto;
    }

    public OrgDTO findDepartment(List<OrgDTO> orgs) {
        for (OrgDTO org : orgs) {
            logger.debug("type : {}", org.getTypeName());
            // if ("部门".equals(org.getTypeName()) || "群组".equals(org.getTypeName())) {
            return org;
            // }
        }

        return null;
    }

    public String findDepartmentPath(List<OrgDTO> orgs) {
        StringBuilder buff = new StringBuilder();

        for (OrgDTO org : orgs) {
            // if ("部门".equals(org.getTypeName()) || "群组".equals(org.getTypeName())) {
            buff.append(org.getName()).append(" ");
            // }
        }

        return buff.toString();
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
