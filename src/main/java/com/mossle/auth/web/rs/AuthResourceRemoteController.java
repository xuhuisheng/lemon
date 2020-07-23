package com.mossle.auth.web.rs;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.type.TypeReference;

import com.mossle.api.auth.CustomPasswordEncoder;
import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserCache;
import com.mossle.api.user.UserDTO;
import com.mossle.api.userauth.ResourceDTO;
import com.mossle.api.userauth.UserAuthConnector;
import com.mossle.api.userauth.UserAuthDTO;

import com.mossle.auth.persistence.domain.Access;
import com.mossle.auth.persistence.manager.AccessManager;

import com.mossle.core.export.Exportor;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;
import com.mossle.core.util.BaseDTO;
import com.mossle.core.util.Select2Info;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.MediaType;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.util.Assert;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequestMapping("auth/rs/remote/resource")
public class AuthResourceRemoteController {
    private static Logger logger = LoggerFactory
            .getLogger(AuthResourceRemoteController.class);
    public static final String HQL_ACCESS = "from Access where tenantId=? order by priority";
    private AccessManager accessManager;

    @RequestMapping(value = "findResources", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseDTO findResources(@RequestParam("sysCode") String sysCode) {
        Assert.hasText(sysCode, "sysCode should not be null");

        List<Access> accesses = accessManager.find(HQL_ACCESS, sysCode);
        List<ResourceDTO> resourceDtos = new ArrayList<ResourceDTO>();

        for (Access access : accesses) {
            ResourceDTO dto = new ResourceDTO();
            dto.setResource(access.getValue());
            dto.setPermission(access.getPerm().getCode());
            resourceDtos.add(dto);
        }

        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);
        baseDto.setData(resourceDtos);

        return baseDto;
    }

    @Resource
    public void setAccessManager(AccessManager accessManager) {
        this.accessManager = accessManager;
    }
}
