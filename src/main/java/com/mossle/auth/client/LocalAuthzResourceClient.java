package com.mossle.auth.client;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.fasterxml.jackson.core.type.TypeReference;

import com.mossle.api.userauth.ResourceDTO;

import com.mossle.auth.persistence.domain.Access;
import com.mossle.auth.persistence.manager.AccessManager;

import com.mossle.client.authz.AuthzResourceClient;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.util.BaseDTO;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.util.Assert;

public class LocalAuthzResourceClient implements AuthzResourceClient {
    private static Logger logger = LoggerFactory
            .getLogger(LocalAuthzResourceClient.class);
    public static final String HQL_ACCESS = "from Access where tenantId=? order by priority";
    public static final String HQL_ACCESS_TYPE = "from Access where tenantId=? and type=? order by priority";
    private AccessManager accessManager;

    @Transactional
    public List<ResourceDTO> findResource(String sysCode) {
        Assert.hasText(sysCode, "sysCode should not be null");

        List<Access> accesses = accessManager.find(HQL_ACCESS, sysCode);
        List<ResourceDTO> resourceDtos = new ArrayList<ResourceDTO>();

        for (Access access : accesses) {
            ResourceDTO dto = new ResourceDTO();
            dto.setType(access.getType());
            dto.setResource(access.getValue());
            dto.setPermission(access.getPerm().getCode());
            resourceDtos.add(dto);
        }

        return resourceDtos;
    }

    @Transactional
    public List<ResourceDTO> findResourceByType(String type, String sysCode) {
        Assert.hasText(sysCode, "sysCode should not be null");

        List<Access> accesses = accessManager.find(HQL_ACCESS, sysCode, type);
        List<ResourceDTO> resourceDtos = new ArrayList<ResourceDTO>();

        for (Access access : accesses) {
            ResourceDTO dto = new ResourceDTO();
            dto.setType(access.getType());
            dto.setResource(access.getValue());
            dto.setPermission(access.getPerm().getCode());
            resourceDtos.add(dto);
        }

        return resourceDtos;
    }

    @Resource
    public void setAccessManager(AccessManager accessManager) {
        this.accessManager = accessManager;
    }
}
