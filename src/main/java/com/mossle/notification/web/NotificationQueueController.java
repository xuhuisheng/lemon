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

import com.mossle.notification.persistence.domain.NotificationQueue;
import com.mossle.notification.persistence.manager.NotificationQueueManager;

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
public class NotificationQueueController {
    private static Logger logger = LoggerFactory
            .getLogger(NotificationQueueController.class);
    private NotificationQueueManager notificationQueueManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private MessageHelper messageHelper;
    private CurrentUserHolder currentUserHolder;

    @RequestMapping("notification-queue-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        logger.debug("list");

        String userId = currentUserHolder.getUserId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = notificationQueueManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "notification/notification-queue-list";
    }

    @RequestMapping("notification-queue-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            NotificationQueue notificationQueue = notificationQueueManager
                    .get(id);

            model.addAttribute("model", notificationQueue);
        }

        return "notification/notification-queue-input";
    }

    @RequestMapping("notification-queue-save")
    public String save(@ModelAttribute NotificationQueue notificationQueue,
            RedirectAttributes redirectAttributes) {
        NotificationQueue dest = null;
        Long id = notificationQueue.getId();

        if (id != null) {
            dest = notificationQueueManager.get(id);
            beanMapper.copy(notificationQueue, dest);
        } else {
            dest = notificationQueue;
        }

        notificationQueueManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/notification/notification-queue-list.do";
    }

    @RequestMapping("notification-queue-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<NotificationQueue> notificationQueues = notificationQueueManager
                .findByIds(selectedItem);

        notificationQueueManager.removeAll(notificationQueues);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/notification/notification-queue-list.do";
    }

    @RequestMapping("notification-queue-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = notificationQueueManager.pagedQuery(page, propertyFilters);

        List<NotificationQueue> notificationQueues = (List<NotificationQueue>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("notification message");
        tableModel.addHeaders("id", "name");
        tableModel.setData(notificationQueues);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setNotificationQueueManager(
            NotificationQueueManager notificationQueueManager) {
        this.notificationQueueManager = notificationQueueManager;
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
