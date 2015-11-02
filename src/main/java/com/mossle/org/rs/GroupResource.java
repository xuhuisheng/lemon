package com.mossle.org.rs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mossle.core.mapper.JsonMapper;

import com.mossle.org.persistence.domain.OrgCompany;
import com.mossle.org.persistence.domain.OrgDepartment;
import com.mossle.org.persistence.manager.OrgCompanyManager;
import com.mossle.org.persistence.manager.OrgDepartmentManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
@Path("group")
public class GroupResource {
    private static Logger logger = LoggerFactory.getLogger(GroupResource.class);
    private OrgDepartmentManager orgDepartmentManager;
    private OrgCompanyManager orgCompanyManager;
    private JsonMapper jsonMapper = new JsonMapper();

    @GET
    @Path("search")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, List<Map<String, Object>>> search() {
        Map<String, List<Map<String, Object>>> resultMap = new HashMap<String, List<Map<String, Object>>>();

        List<Map<String, Object>> companies = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> departments = new ArrayList<Map<String, Object>>();
        resultMap.put("公司", companies);
        resultMap.put("部门", departments);

        for (OrgCompany orgCompany : orgCompanyManager.getAll()) {
            Map<String, Object> map = new HashMap<String, Object>();
            companies.add(map);
            map.put("id", orgCompany.getId());
            map.put("name", orgCompany.getName());
        }

        for (OrgDepartment orgDepartment : orgDepartmentManager.getAll()) {
            Map<String, Object> map = new HashMap<String, Object>();
            departments.add(map);
            map.put("id", orgDepartment.getId());
            map.put("name", orgDepartment.getName());
        }

        return resultMap;
    }

    // ~ ======================================================================
    @Resource
    public void setOrgDepartmentManager(
            OrgDepartmentManager orgDepartmentManager) {
        this.orgDepartmentManager = orgDepartmentManager;
    }

    @Resource
    public void setOrgCompanyManager(OrgCompanyManager orgCompanyManager) {
        this.orgCompanyManager = orgCompanyManager;
    }
}
