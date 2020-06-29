package com.mossle.auth.data;

import java.util.List;

import com.mossle.client.open.OpenAppDTO;

import com.mossle.core.csv.CsvCallback;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenCallback implements CsvCallback {
    private static Logger logger = LoggerFactory.getLogger(OpenCallback.class);
    private List<OpenAppDTO> openAppDtos;

    public void process(List<String> list, int lineNo) throws Exception {
        String groupCode = list.get(0);
        String code = list.get(1);
        String name = list.get(2);
        String clientId = list.get(3);
        String clientSecret = list.get(4);
        String userId = list.get(5);

        if (StringUtils.isBlank(clientId)) {
            logger.warn("clientId cannot be blank {} {}", lineNo, list);

            return;
        }

        if (StringUtils.isBlank(clientSecret)) {
            logger.warn("clientSecret cannot be blank {} {}", lineNo, list);

            return;
        }

        OpenAppDTO openAppDto = this.findOpenAppDto(code);

        if (openAppDto == null) {
            openAppDto = new OpenAppDTO();
        }

        openAppDto.setGroupCode(groupCode);
        openAppDto.setCode(code);
        openAppDto.setName(name);
        openAppDto.setClientId(clientId);
        openAppDto.setClientSecret(clientSecret);
        openAppDtos.add(openAppDto);
    }

    public OpenAppDTO findOpenAppDto(String code) {
        for (OpenAppDTO openAppDto : openAppDtos) {
            if (code.equals(openAppDto.getCode())) {
                return openAppDto;
            }
        }

        return null;
    }

    public void setOpenAppDtos(List<OpenAppDTO> openAppDtos) {
        this.openAppDtos = openAppDtos;
    }
}
