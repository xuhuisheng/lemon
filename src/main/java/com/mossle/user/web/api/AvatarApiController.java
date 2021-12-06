package com.mossle.user.web.api;

import java.io.InputStream;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.user.service.UserAvatarService;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("avatar/api")
public class AvatarApiController {
    private static Logger logger = LoggerFactory
            .getLogger(AvatarApiController.class);
    private TenantHolder tenantHolder;
    private UserAvatarService userAvatarService;

    @RequestMapping(value = "{username}", produces = MediaType.IMAGE_PNG_VALUE)
    public void avatar(
            @PathVariable("username") String username,
            @RequestParam(value = "width", required = false, defaultValue = "16") int width,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        logger.debug("avatar : {} {}", username, width);

        String tenantId = tenantHolder.getTenantId();

        InputStream is = userAvatarService.viewAvatarByUsername(username,
                width, tenantId).getInputStream();

        response.setContentType("image/png");
        IOUtils.copy(is, response.getOutputStream());
    }

    // ~ ======================================================================
    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }

    @Resource
    public void setUserAvatarService(UserAvatarService userAvatarService) {
        this.userAvatarService = userAvatarService;
    }
}
