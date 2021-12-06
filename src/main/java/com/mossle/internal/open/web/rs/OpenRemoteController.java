package com.mossle.internal.open.web.rs;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.client.open.OpenAppDTO;
import com.mossle.client.open.SysDTO;

import com.mossle.core.util.BaseDTO;

import com.mossle.internal.open.persistence.domain.OpenApp;
import com.mossle.internal.open.persistence.domain.SysInfo;
import com.mossle.internal.open.persistence.manager.OpenAppManager;
import com.mossle.internal.open.persistence.manager.SysInfoManager;
import com.mossle.internal.open.support.OpenAppConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.MediaType;

import org.springframework.util.Assert;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("open/rs/remote")
public class OpenRemoteController {
    private static Logger logger = LoggerFactory
            .getLogger(OpenRemoteController.class);
    private OpenAppManager openAppManager;
    private SysInfoManager sysInfoManager;
    private OpenAppConverter openAppConverter = new OpenAppConverter();

    @RequestMapping(value = "getOpenApp", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseDTO getOpenApp(@RequestParam("clientId") String clientId) {
        logger.debug("get open app : {}", clientId);
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

    @RequestMapping(value = "findSys", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseDTO findSys(@RequestParam("code") String code) {
        String hql = "from SysInfo where code=?";
        SysInfo sysInfo = sysInfoManager.findUnique(hql, code);
        SysDTO sysDto = new SysDTO();

        if (sysInfo != null) {
            sysDto.setId(Long.toString(sysInfo.getId()));
            sysDto.setCode(code);
            sysDto.setName(sysInfo.getName());
        } else {
            sysDto = null;
        }

        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);
        baseDto.setData(sysDto);

        return baseDto;
    }

    // ~ ======================================================================
    @Resource
    public void setOpenAppManager(OpenAppManager openAppManager) {
        this.openAppManager = openAppManager;
    }

    @Resource
    public void setSysInfoManager(SysInfoManager sysInfoManager) {
        this.sysInfoManager = sysInfoManager;
    }
}
