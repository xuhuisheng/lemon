package com.mossle.internal.mail.rs;

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

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.util.BaseDTO;
import com.mossle.core.util.StringUtils;

import com.mossle.internal.mail.service.MailDataService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
@Path("mail")
public class MailResource {
    private static Logger logger = LoggerFactory.getLogger(MailResource.class);
    private MailDataService mailDataService;
    private JsonMapper jsonMapper = new JsonMapper();

    @Path("send")
    @POST
    public BaseDTO send(@FormParam("to") String to,
            @FormParam("subject") String subject,
            @FormParam("content") String content,
            @FormParam("configCode") String configCode) {
        BaseDTO baseDto = new BaseDTO();

        if (StringUtils.isBlank(to)) {
            logger.debug("to should not be empty");
            baseDto.setCode(400);
            baseDto.setMessage("to should not be empty");

            return baseDto;
        }

        if (StringUtils.isBlank(subject)) {
            logger.debug("subject should not be empty");
            baseDto.setCode(400);
            baseDto.setMessage("subject should not be empty");

            return baseDto;
        }

        if (StringUtils.isBlank(content)) {
            logger.debug("content should not be empty");
            baseDto.setCode(400);
            baseDto.setMessage("content should not be empty");

            return baseDto;
        }

        if (StringUtils.isBlank(configCode)) {
            logger.debug("configCode should not be empty");
            baseDto.setCode(400);
            baseDto.setMessage("configCode should not be empty");

            return baseDto;
        }

        try {
            mailDataService.send(to, subject, content, configCode);
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

    @Path("sendTemplate")
    @POST
    public BaseDTO sendTemplate(@FormParam("to") String to,
            @FormParam("data") String data,
            @FormParam("templateCode") String templateCode,
            @FormParam("configCode") String configCode) {
        logger.debug("to : {}", to);
        logger.debug("data : {}", data);
        logger.debug("templateCode : {}", templateCode);
        logger.debug("configCode : {}", configCode);

        BaseDTO baseDto = new BaseDTO();

        if (StringUtils.isBlank(templateCode)) {
            logger.debug("templateCode should not be empty");
            baseDto.setCode(400);
            baseDto.setMessage("templateCode should not be empty");

            return baseDto;
        }

        if (StringUtils.isBlank(configCode)) {
            logger.debug("configCode should not be empty");
            baseDto.setCode(400);
            baseDto.setMessage("configCode should not be empty");

            return baseDto;
        }

        if (StringUtils.isNotBlank(data)) {
            try {
                jsonMapper.fromJson(data, Map.class);
            } catch (Exception ex) {
                logger.warn(ex.getMessage(), ex);
                baseDto.setCode(400);
                baseDto.setMessage(ex.getMessage());

                return baseDto;
            }
        }

        boolean templateExists = mailDataService
                .checkTemplateCodeExists(templateCode);

        if (!templateExists) {
            logger.debug("templateCode doesnot exists : {}", templateCode);
            baseDto.setCode(400);
            baseDto.setMessage("templateCode doesnot exists : " + templateCode);

            return baseDto;
        }

        boolean configExists = mailDataService
                .checkConfigCodeExists(configCode);

        if (!configExists) {
            logger.debug("configCode doesnot exists : {}", configCode);
            baseDto.setCode(400);
            baseDto.setMessage("configCode doesnot exists : " + configCode);

            return baseDto;
        }

        try {
            mailDataService.sendTemplate(to, data, templateCode, configCode);
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
    public void setMailDataService(MailDataService mailDataService) {
        this.mailDataService = mailDataService;
    }
}
