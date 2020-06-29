package com.mossle.internal.open.support;

import java.util.ArrayList;
import java.util.List;

import com.mossle.client.open.OpenAppDTO;

import com.mossle.internal.open.persistence.domain.OpenApp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenAppConverter {
    private static Logger logger = LoggerFactory
            .getLogger(OpenAppConverter.class);

    public List<OpenAppDTO> convertOpenAppDtos(List<OpenApp> openApps) {
        List<OpenAppDTO> openAppDos = new ArrayList<OpenAppDTO>();

        for (OpenApp openApp : openApps) {
            openAppDos.add(this.convertOpenAppDto(openApp));
        }

        return openAppDos;
    }

    public OpenAppDTO convertOpenAppDto(OpenApp openApp) {
        if (openApp == null) {
            logger.info("openApp cannot be null");

            return null;
        }

        OpenAppDTO openAppDto = new OpenAppDTO();
        openAppDto.setGroupCode(openApp.getGroupCode());
        openAppDto.setCode(openApp.getCode());
        openAppDto.setName(openApp.getName());
        openAppDto.setClientId(openApp.getClientId());
        openAppDto.setClientSecret(openApp.getClientSecret());

        return openAppDto;
    }
}
