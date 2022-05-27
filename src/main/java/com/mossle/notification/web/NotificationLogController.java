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

import com.mossle.notification.persistence.domain.NotificationLog;
import com.mossle.notification.persistence.manager.NotificationLogManager;

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
public class NotificationLogController {
    private static Logger logger = LoggerFactory
            .getLogger(NotificationLogController.class);
    private NotificationLogManager notificationLogManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private MessageHelper messageHelper;
    private CurrentUserHolder currentUserHolder;

    @RequestMapping("notification-log-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        logger.debug("list");

        String userId = currentUserHolder.getUserId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = notificationLogManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "notification/notification-log-list";
    }

    @RequestMapping("notification-log-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            NotificationLog notificationLog = notificationLogManager.get(id);

            model.addAttribute("model", notificationLog);
        }

        return "notification/notification-log-input";
    }

    @RequestMapping("notification-log-save")
    public String save(@ModelAttribute NotificationLog notificationLog,
            RedirectAttributes redirectAttributes) {
        NotificationLog dest = null;
        Long id = notificationLog.getId();

        if (id != null) {
            dest = notificationLogManager.get(id);
            beanMapper.copy(notificationLog, dest);
        } else {
            dest = notificationLog;
        }

        notificationLogManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/notification/notification-log-list.do";
    }

    @RequestMapping("notification-log-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<NotificationLog> notificationLogs = notificationLogManager
                .findByIds(selectedItem);

        notificationLogManager.removeAll(notificationLogs);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/notification/notification-log-list.do";
    }

    @RequestMapping("notification-log-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = notificationLogManager.pagedQuery(page, propertyFilters);

        List<NotificationLog> notificationLogs = (List<NotificationLog>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("notification message");
        tableModel.addHeaders("id", "name");
        tableModel.setData(notificationLogs);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setNotificationLogManager(
            NotificationLogManager notificationLogManager) {
        this.notificationLogManager = notificationLogManager;
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
