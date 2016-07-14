package com.mossle.internal.sendsms.rs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mossle.core.util.BaseDTO;
import com.mossle.core.util.StringUtils;

import com.mossle.internal.sendsms.service.SendsmsDataService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
@Path("sendsms")
public class SendsmsResource {
    private static Logger logger = LoggerFactory
            .getLogger(SendsmsResource.class);
    private SendsmsDataService sendsmsDataService;

    @Path("send")
    @POST
    public BaseDTO send(@FormParam("mobile") String mobile,
            @FormParam("message") String message,
            @FormParam("configCode") String configCode) {
        logger.debug("mobile : {}", mobile);
        logger.debug("message : {}", message);
        logger.debug("configCode : {}", configCode);

        BaseDTO baseDto = new BaseDTO();

        if (StringUtils.isBlank(mobile)) {
            logger.debug("mobile should not be empty");
            baseDto.setCode(400);
            baseDto.setMessage("mobile should not be empty");

            return baseDto;
        }

        if (StringUtils.isBlank(message)) {
            logger.debug("message should not be empty");
            baseDto.setCode(400);
            baseDto.setMessage("message should not be empty");

            return baseDto;
        }

        if (StringUtils.isBlank(configCode)) {
            logger.debug("configCode should not be empty");
            baseDto.setCode(400);
            baseDto.setMessage("configCode should not be empty");

            return baseDto;
        }

        if (mobile.length() != 11) {
            logger.debug("mobile({}) length should be 11", mobile);
            baseDto.setCode(400);
            baseDto.setMessage("mobile(" + mobile + ") length should be 11");

            return baseDto;
        }

        boolean configExists = sendsmsDataService
                .checkConfigCodeExists(configCode);

        if (!configExists) {
            logger.debug("configCode doesnot exists : {}", configCode);
            baseDto.setCode(400);
            baseDto.setMessage("configCode doesnot exists : " + configCode);

            return baseDto;
        }

        try {
            sendsmsDataService.saveSendsmsQueue(mobile, message, configCode);
            baseDto.setCode(200);
            logger.debug("success");
        } catch (Exception ex) {
            logger.debug("error");
            logger.error(ex.getMessage(), ex);
            baseDto.setCode(500);
            baseDto.setMessage(ex.getMessage());
        }

        return baseDto;
    }

    @Resource
    public void setSendsmsDataService(SendsmsDataService sendsmsDataService) {
        this.sendsmsDataService = sendsmsDataService;
    }
}
