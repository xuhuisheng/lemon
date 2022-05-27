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

import com.mossle.notification.persistence.domain.NotificationProvider;
import com.mossle.notification.persistence.manager.NotificationProviderManager;

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
public class NotificationProviderController {
    private static Logger logger = LoggerFactory
            .getLogger(NotificationProviderController.class);
    private NotificationProviderManager notificationProviderManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private MessageHelper messageHelper;
    private CurrentUserHolder currentUserHolder;

    @RequestMapping("notification-provider-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        logger.debug("list");

        String userId = currentUserHolder.getUserId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = notificationProviderManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "notification/notification-provider-list";
    }

    @RequestMapping("notification-provider-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            NotificationProvider notificationProvider = notificationProviderManager
                    .get(id);

            model.addAttribute("model", notificationProvider);
        }

        return "notification/notification-provider-input";
    }

    @RequestMapping("notification-provider-save")
    public String save(
            @ModelAttribute NotificationProvider notificationProvider,
            RedirectAttributes redirectAttributes) {
        NotificationProvider dest = null;
        Long id = notificationProvider.getId();

        if (id != null) {
            dest = notificationProviderManager.get(id);
            beanMapper.copy(notificationProvider, dest);
        } else {
            dest = notificationProvider;
        }

        notificationProviderManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/notification/notification-provider-list.do";
    }

    @RequestMapping("notification-provider-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<NotificationProvider> notificationProviders = notificationProviderManager
                .findByIds(selectedItem);

        notificationProviderManager.removeAll(notificationProviders);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/notification/notification-provider-list.do";
    }

    @RequestMapping("notification-provider-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = notificationProviderManager.pagedQuery(page, propertyFilters);

        List<NotificationProvider> notificationProviders = (List<NotificationProvider>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("notification message");
        tableModel.addHeaders("id", "name");
        tableModel.setData(notificationProviders);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setNotificationProviderManager(
            NotificationProviderManager notificationProviderManager) {
        this.notificationProviderManager = notificationProviderManager;
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
