package com.mossle.auth.web.rs;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.auth.CustomPasswordEncoder;
import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserCache;
import com.mossle.api.user.UserDTO;
import com.mossle.api.userauth.UserAuthConnector;
import com.mossle.api.userauth.UserAuthDTO;

import com.mossle.client.user.UserClient;

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
@RequestMapping("auth/rs/remote")
public class AuthRemoteController {
    private static Logger logger = LoggerFactory
            .getLogger(AuthRemoteController.class);
    private UserAuthConnector userAuthConnector;
    private UserClient userClient;

    @RequestMapping(value = "findByUsername", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseDTO findByUsername(@RequestParam("username") String username,
            @RequestParam("tenantId") String tenantId,
            @RequestParam("sysCode") String sysCode) {
        logger.debug("findByUsername {} {} {}", username, tenantId, sysCode);
        Assert.hasText(username, "username should not be null");

        UserDTO userDto = userClient.findByUsername(username, tenantId);

        return this.findById(userDto.getId(), sysCode);
    }

    @RequestMapping(value = "findById", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseDTO findById(@RequestParam("id") String id,
            @RequestParam("sysCode") String sysCode) {
        logger.debug("findById {} {}", id, sysCode);
        Assert.hasText(id, "id should not be null");

        UserAuthDTO userAuthDto = userAuthConnector.findById(id, sysCode);
        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);
        baseDto.setData(userAuthDto);

        return baseDto;
    }

    @Resource
    public void setUserAuthConnector(UserAuthConnector userAuthConnector) {
        this.userAuthConnector = userAuthConnector;
    }

    @Resource
    public void setUserClient(UserClient userClient) {
        this.userClient = userClient;
    }
}
