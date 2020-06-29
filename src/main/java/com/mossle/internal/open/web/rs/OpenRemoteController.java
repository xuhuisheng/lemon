package com.mossle.internal.open.web.rs;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.auth.CustomPasswordEncoder;
import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.AccountStatus;
import com.mossle.api.user.UserCache;
import com.mossle.api.user.UserDTO;

import com.mossle.client.open.OpenAppDTO;

import com.mossle.core.export.Exportor;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;
import com.mossle.core.util.BaseDTO;
import com.mossle.core.util.Select2Info;

import com.mossle.internal.open.persistence.domain.OpenApp;
import com.mossle.internal.open.persistence.manager.OpenAppManager;
import com.mossle.internal.open.support.OpenAppConverter;

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
@RequestMapping("open/rs/remote")
public class OpenRemoteController {
    private static Logger logger = LoggerFactory
            .getLogger(OpenRemoteController.class);
    private OpenAppManager openAppManager;
    private OpenAppConverter openAppConverter = new OpenAppConverter();

    @RequestMapping(value = "getOpenApp", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseDTO getOpenApp(@RequestParam("clientId") String clientId) {
        Assert.hasText(clientId, "clientId should not be null");

        OpenApp openApp = openAppManager.findUniqueBy("clientId", clientId);

        OpenAppDTO openAppDto = this.openAppConverter
                .convertOpenAppDto(openApp);
        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);
        baseDto.setData(openAppDto);

        return baseDto;
    }

    @RequestMapping(value = "getAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseDTO getAll() {
        List<OpenApp> openApps = openAppManager.getAll();
        List<OpenAppDTO> openAppDtos = this.openAppConverter
                .convertOpenAppDtos(openApps);

        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);
        baseDto.setData(openAppDtos);

        return baseDto;
    }

    @RequestMapping(value = "findUserApps", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseDTO findUserApps(@RequestParam("userId") String userId) {
        String hql = "from OpenApp where userId=?";
        List<OpenApp> openApps = openAppManager.find(hql, userId);
        List<OpenAppDTO> openAppDtos = this.openAppConverter
                .convertOpenAppDtos(openApps);

        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);
        baseDto.setData(openAppDtos);

        return baseDto;
    }

    @RequestMapping(value = "findGroupApps", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseDTO findGroupApps(@RequestParam("groupCode") String groupCode) {
        String hql = "from OpenApp where groupCode=?";
        List<OpenApp> openApps = openAppManager.find(hql, groupCode);
        List<OpenAppDTO> openAppDtos = this.openAppConverter
                .convertOpenAppDtos(openApps);

        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);
        baseDto.setData(openAppDtos);

        return baseDto;
    }

    // ~ ======================================================================
    @Resource
    public void setOpenAppManager(OpenAppManager openAppManager) {
        this.openAppManager = openAppManager;
    }
}
