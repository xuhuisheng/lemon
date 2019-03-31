package com.mossle.party.web.rs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import com.mossle.api.employee.EmployeeConnector;
import com.mossle.api.employee.EmployeeDTO;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.party.persistence.domain.PartyEntity;
import com.mossle.party.persistence.domain.PartyStruct;
import com.mossle.party.persistence.domain.PartyType;
import com.mossle.party.persistence.manager.PartyEntityManager;
import com.mossle.party.persistence.manager.PartyStructManager;
import com.mossle.party.persistence.manager.PartyTypeManager;
import com.mossle.party.service.PartyService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequestMapping("party/rs/employee")
public class EmployeeRestController {
    private static Logger logger = LoggerFactory
            .getLogger(EmployeeRestController.class);
    private EmployeeConnector employeeConnector;
    private TenantHolder tenantHolder;

    @RequestMapping("view")
    public EmployeeDTO view(@RequestParam("code") String code) {
        String tenantId = tenantHolder.getTenantId();
        EmployeeDTO employeeDto = employeeConnector.findByCode(code, tenantId);

        return employeeDto;
    }

    // ~ ======================================================================
    @Resource
    public void setEmployeeConnector(EmployeeConnector employeeConnector) {
        this.employeeConnector = employeeConnector;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
