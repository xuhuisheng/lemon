package com.mossle.notification.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.auth.CurrentUserHolder;

import com.mossle.client.open.OpenClient;
import com.mossle.client.open.SysDTO;

import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;

import com.mossle.notification.persistence.manager.NotificationConfigManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("notification/sys")
public class NotificationSysController {
    private static Logger logger = LoggerFactory
            .getLogger(NotificationSysController.class);
    private NotificationConfigManager notificationConfigManager;
    private CurrentUserHolder currentUserHolder;
    private OpenClient openClient;

    @RequestMapping("{sysCode}/index")
    public String list(@PathVariable("sysCode") String sysCode,
            @ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        logger.debug("list");

        SysDTO sysDto = openClient.findSys(sysCode);
        model.addAttribute("sysCode", sysCode);
        model.addAttribute("sysDto", sysDto);

        String userId = currentUserHolder.getUserId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = notificationConfigManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "notification/sys/index";
    }

    // ~ ======================================================================
    @Resource
    public void setNotificationConfigManager(
            NotificationConfigManager notificationConfigManager) {
        this.notificationConfigManager = notificationConfigManager;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setOpenClient(OpenClient openClient) {
        this.openClient = openClient;
    }
}
