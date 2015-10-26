package com.mossle.user.rs;

import java.io.*;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.mossle.api.store.StoreConnector;
import com.mossle.api.store.StoreDTO;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.util.BaseDTO;
import com.mossle.core.util.IoUtils;
import com.mossle.core.util.StringUtils;

import com.mossle.user.persistence.domain.UserBase;
import com.mossle.user.persistence.manager.UserBaseManager;
import com.mossle.user.service.UserAvatarService;
import com.mossle.user.service.UserService;
import com.mossle.user.support.UserBaseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
@Path("avatar")
public class AvatarResource {
    private static Logger logger = LoggerFactory
            .getLogger(AvatarResource.class);
    private UserAvatarService userAvatarService;
    private TenantHolder tenantHolder;

    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public InputStream view(@QueryParam("id") String id,
            @QueryParam("width") @DefaultValue("0") int width) throws Exception {
        logger.debug("width : {}", width);

        String tenantId = tenantHolder.getTenantId();

        Long longId = null;

        try {
            longId = Long.parseLong(id);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return userAvatarService.viewAvatar(longId, width, tenantId)
                .getInputStream();
    }

    // ~ ======================================================================
    @Resource
    public void setUserAvatarService(UserAvatarService userAvatarService) {
        this.userAvatarService = userAvatarService;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
