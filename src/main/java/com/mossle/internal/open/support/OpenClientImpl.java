package com.mossle.internal.open.support;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.client.open.OpenAppDTO;
import com.mossle.client.open.OpenClient;
import com.mossle.client.open.SysDTO;

import com.mossle.internal.open.persistence.domain.OpenApp;
import com.mossle.internal.open.persistence.domain.SysInfo;
import com.mossle.internal.open.persistence.manager.OpenAppManager;
import com.mossle.internal.open.persistence.manager.SysInfoManager;
import com.mossle.internal.open.support.OpenAppConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenClientImpl implements OpenClient {
    private static Logger logger = LoggerFactory
            .getLogger(OpenClientImpl.class);
    private OpenAppManager openAppManager;
    private SysInfoManager sysInfoManager;
    private OpenAppConverter openAppConverter = new OpenAppConverter();

    public OpenAppDTO getApp(String clientId) {
        OpenApp openApp = openAppManager.findUniqueBy("clientId", clientId);

        OpenAppDTO openAppDto = this.openAppConverter
                .convertOpenAppDto(openApp);

        return openAppDto;
    }

    public List<OpenAppDTO> getAll() {
        List<OpenApp> openApps = openAppManager.getAll();
        List<OpenAppDTO> openAppDtos = this.openAppConverter
                .convertOpenAppDtos(openApps);

        return openAppDtos;
    }

    public List<OpenAppDTO> findUserApps(String userId) {
        String hql = "from OpenApp where userId=?";
        List<OpenApp> openApps = openAppManager.find(hql, userId);
        List<OpenAppDTO> openAppDtos = this.openAppConverter
                .convertOpenAppDtos(openApps);

        return openAppDtos;
    }

    public List<OpenAppDTO> findGroupApps(String groupCode) {
        String hql = "from OpenApp where groupCode=?";
        List<OpenApp> openApps = openAppManager.find(hql, groupCode);
        List<OpenAppDTO> openAppDtos = this.openAppConverter
                .convertOpenAppDtos(openApps);

        return openAppDtos;
    }

    public SysDTO findSys(String code) {
        String hql = "from SysInfo where code=?";
        SysInfo sysInfo = sysInfoManager.findUnique(hql, code);

        if (sysInfo == null) {
            return null;
        }

        SysDTO sysDto = new SysDTO();
        sysDto.setCode(code);
        sysDto.setName(sysInfo.getName());

        return sysDto;
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
