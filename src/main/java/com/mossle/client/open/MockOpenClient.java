package com.mossle.client.open;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockOpenClient implements OpenClient {
    private static Logger logger = LoggerFactory
            .getLogger(MockOpenClient.class);

    public OpenAppDTO getApp(String clientId) {
        OpenAppDTO openAppDto = new OpenAppDTO();
        openAppDto.setClientId(clientId);
        openAppDto.setClientSecret(clientId);

        return openAppDto;
    }

    public List<OpenAppDTO> getAll() {
        return Collections.emptyList();
    }

    public List<OpenAppDTO> findUserApps(String userId) {
        return Collections.emptyList();
    }

    public List<OpenAppDTO> findGroupApps(String groupCode) {
        return Collections.emptyList();
    }

    public SysDTO findSys(String code) {
        SysDTO sysDto = new SysDTO();
        sysDto.setCode("sys");
        sysDto.setName("name");

        return sysDto;
    }
}
