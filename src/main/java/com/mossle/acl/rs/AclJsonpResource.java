package com.mossle.acl.rs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.util.JSONPObject;

import com.mossle.acl.domain.AclEntry;
import com.mossle.acl.domain.AclObjectIdentity;
import com.mossle.acl.service.AclService;

import com.mossle.core.util.BaseDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
@Path("acljsonp")
public class AclJsonpResource {
    private static Logger logger = LoggerFactory
            .getLogger(AclJsonpResource.class);
    private AclService aclService;

    /**
     * 获得对某个资源拥有权限的sid列表
     */
    @Path("getAccess")
    @GET
    @Produces({ MediaType.APPLICATION_JSON, "application/x-javascript" })
    public JSONPObject getAccess(@QueryParam("callback") String callback,
            @QueryParam("resourceId") String resourceId,
            @QueryParam("resourceType") String resourceType,
            @QueryParam("mask") int mask) {
        AclObjectIdentity aclObjectIdentity = aclService.findAclObjectIdentity(
                resourceId, resourceType);

        if (aclObjectIdentity == null) {
            logger.info("object identity [{},{}] is null", resourceId,
                    resourceType);

            List departments = new ArrayList();
            List emailgroups = new ArrayList();
            List users = new ArrayList();
            BaseDTO baseDto = new BaseDTO();
            baseDto.setCode(200);

            Map data = new HashMap();
            baseDto.setData(data);
            data.put("department", departments);
            data.put("emailgroup", emailgroups);
            data.put("user", users);

            return new JSONPObject(callback, baseDto);
        }

        List departments = new ArrayList();
        List emailgroups = new ArrayList();
        List users = new ArrayList();

        for (AclEntry aclEntry : aclObjectIdentity.getAclEntries()) {
            Map sidInfo = aclService.getSidInfo(aclEntry.getSidId());

            if ("1".equals(sidInfo.get("typeId"))) {
                sidInfo.remove("typeId");
                users.add(sidInfo);
            } else if ("3".equals(sidInfo.get("typeId"))) {
                sidInfo.remove("typeId");
                departments.add(sidInfo);
            } else if ("5".equals(sidInfo.get("typeId"))) {
                sidInfo.remove("typeId");
                emailgroups.add(sidInfo);
            }
        }

        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);

        Map data = new HashMap();
        baseDto.setData(data);
        data.put("department", departments);
        data.put("emailgroup", emailgroups);
        data.put("user", users);

        return new JSONPObject(callback, baseDto);
    }

    @Resource
    public void setAclService(AclService aclService) {
        this.aclService = aclService;
    }
}
