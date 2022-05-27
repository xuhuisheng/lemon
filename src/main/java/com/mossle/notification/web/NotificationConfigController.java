package com.mossle.notification.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.auth.CurrentUserHolder;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.notification.persistence.domain.NotificationConfig;
import com.mossle.notification.persistence.manager.NotificationConfigManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("notification")
public class NotificationConfigController {
    private static Logger logger = LoggerFactory
            .getLogger(NotificationConfigController.class);
    private NotificationConfigManager notificationConfigManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private MessageHelper messageHelper;
    private CurrentUserHolder currentUserHolder;

    @RequestMapping("notification-config-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        logger.debug("list");

        String userId = currentUserHolder.getUserId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = notificationConfigManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "notification/notification-config-list";
    }

    @RequestMapping("notification-config-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            NotificationConfig notificationConfig = notificationConfigManager
                    .get(id);

            model.addAttribute("model", notificationConfig);
        }

        return "notification/notification-config-input";
    }

    @RequestMapping("notification-config-save")
    public String save(@ModelAttribute NotificationConfig notificationConfig,
            RedirectAttributes redirectAttributes) {
        NotificationConfig dest = null;
        Long id = notificationConfig.getId();

        if (id != null) {
            dest = notificationConfigManager.get(id);
            beanMapper.copy(notificationConfig, dest);
        } else {
            dest = notificationConfig;
        }

        notificationConfigManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/notification/notification-config-list.do";
    }

    @RequestMapping("notification-config-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<NotificationConfig> notificationConfigs = notificationConfigManager
                .findByIds(selectedItem);

        notificationConfigManager.removeAll(notificationConfigs);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/notification/notification-config-list.do";
    }

    @RequestMapping("notification-config-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = notificationConfigManager.pagedQuery(page, propertyFilters);

        List<NotificationConfig> notificationConfigs = (List<NotificationConfig>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("notification message");
        tableModel.addHeaders("id", "name");
        tableModel.setData(notificationConfigs);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setNotificationConfigManager(
            NotificationConfigManager notificationConfigManager) {
        this.notificationConfigManager = notificationConfigManager;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }
}
