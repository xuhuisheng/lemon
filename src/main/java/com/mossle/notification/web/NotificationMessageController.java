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

import com.mossle.notification.persistence.domain.NotificationMessage;
import com.mossle.notification.persistence.manager.NotificationMessageManager;

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
public class NotificationMessageController {
    private static Logger logger = LoggerFactory
            .getLogger(NotificationMessageController.class);
    private NotificationMessageManager notificationMessageManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private MessageHelper messageHelper;
    private CurrentUserHolder currentUserHolder;

    @RequestMapping("notification-message-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        logger.debug("list");

        String userId = currentUserHolder.getUserId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = notificationMessageManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "notification/notification-message-list";
    }

    @RequestMapping("notification-message-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            NotificationMessage notificationMessage = notificationMessageManager
                    .get(id);

            model.addAttribute("model", notificationMessage);
        }

        return "notification/notification-message-input";
    }

    @RequestMapping("notification-message-save")
    public String save(@ModelAttribute NotificationMessage notificationMessage,
            RedirectAttributes redirectAttributes) {
        NotificationMessage dest = null;
        Long id = notificationMessage.getId();

        if (id != null) {
            dest = notificationMessageManager.get(id);
            beanMapper.copy(notificationMessage, dest);
        } else {
            dest = notificationMessage;
        }

        notificationMessageManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/notification/notification-message-list.do";
    }

    @RequestMapping("notification-message-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<NotificationMessage> notificationMessages = notificationMessageManager
                .findByIds(selectedItem);

        notificationMessageManager.removeAll(notificationMessages);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/notification/notification-message-list.do";
    }

    @RequestMapping("notification-message-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = notificationMessageManager.pagedQuery(page, propertyFilters);

        List<NotificationMessage> notificationMessages = (List<NotificationMessage>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("notification message");
        tableModel.addHeaders("id", "name");
        tableModel.setData(notificationMessages);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setNotificationMessageManager(
            NotificationMessageManager notificationMessageManager) {
        this.notificationMessageManager = notificationMessageManager;
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
