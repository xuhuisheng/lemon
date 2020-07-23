package com.mossle.internal.open.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.sys.SysCategoryDTO;
import com.mossle.api.sys.SysInfoDTO;

import com.mossle.client.mdm.SysClient;

import com.mossle.internal.open.persistence.domain.SysInfo;
import com.mossle.internal.open.persistence.manager.SysInfoManager;

public class LocalSysClient implements SysClient {
    private SysInfoManager sysInfoManager;

    public List<SysCategoryDTO> findAll() {
        return Collections.emptyList();
    }

    public List<SysInfoDTO> findFavorites() {
        List<SysInfoDTO> list = new ArrayList<SysInfoDTO>();
        List<SysInfo> sysInfos = sysInfoManager.getAll();

        for (SysInfo sysInfo : sysInfos) {
            SysInfoDTO sysInfoDto = new SysInfoDTO();
            sysInfoDto.setCode(sysInfo.getCode());
            sysInfoDto.setName(sysInfo.getName());
            sysInfoDto.setLogo(sysInfo.getLogo());
            sysInfoDto.setUrl(sysInfo.getUrl());
            list.add(sysInfoDto);
        }

        return list;
    }

    @Resource
    public void setSysInfoManager(SysInfoManager sysInfoManager) {
        this.sysInfoManager = sysInfoManager;
    }
}
