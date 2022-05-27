package com.mossle.notification.web.rs;

import javax.annotation.Resource;

import com.mossle.core.util.BaseDTO;

import com.mossle.notification.service.NotificationService;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("notification/rs")
public class NotificationRestController {
    private static Logger logger = LoggerFactory
            .getLogger(NotificationRestController.class);
    private NotificationService notificationService;

    @RequestMapping("send")
    public BaseDTO send(@RequestParam("requestId") String requestId,
            @RequestParam("catalog") String catalog,
            @RequestParam("to") String to,
            @RequestParam("templateCode") String templateCode,
            @RequestParam("data") String data,
            @RequestParam("priority") int priority,
            @RequestParam("config") String config) {
        if (StringUtils.isBlank(catalog)) {
            logger.info("catalog cannot blank");

            BaseDTO baseDto = new BaseDTO();
            baseDto.setCode(400);
            baseDto.setMessage("catalog cannot blank");

            return baseDto;
        }

        if (StringUtils.isBlank(templateCode)) {
            logger.info("templateCode cannot blank");

            BaseDTO baseDto = new BaseDTO();
            baseDto.setCode(400);
            baseDto.setMessage("templateCode cannot blank");

            return baseDto;
        }

        notificationService.receive(requestId, catalog, to, templateCode, data,
                priority, config);

        return new BaseDTO();
    }

    @Resource
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
}
