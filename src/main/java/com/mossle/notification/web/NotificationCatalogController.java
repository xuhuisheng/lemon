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

import com.mossle.notification.persistence.domain.NotificationCatalog;
import com.mossle.notification.persistence.manager.NotificationCatalogManager;

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
public class NotificationCatalogController {
    private static Logger logger = LoggerFactory
            .getLogger(NotificationCatalogController.class);
    private NotificationCatalogManager notificationCatalogManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private MessageHelper messageHelper;
    private CurrentUserHolder currentUserHolder;

    @RequestMapping("notification-catalog-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        logger.debug("list");

        String userId = currentUserHolder.getUserId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = notificationCatalogManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "notification/notification-catalog-list";
    }

    @RequestMapping("notification-catalog-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            NotificationCatalog notificationCatalog = notificationCatalogManager
                    .get(id);

            model.addAttribute("model", notificationCatalog);
        }

        return "notification/notification-catalog-input";
    }

    @RequestMapping("notification-catalog-save")
    public String save(@ModelAttribute NotificationCatalog notificationCatalog,
            RedirectAttributes redirectAttributes) {
        NotificationCatalog dest = null;
        Long id = notificationCatalog.getId();

        if (id != null) {
            dest = notificationCatalogManager.get(id);
            beanMapper.copy(notificationCatalog, dest);
        } else {
            dest = notificationCatalog;
        }

        notificationCatalogManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/notification/notification-catalog-list.do";
    }

    @RequestMapping("notification-catalog-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<NotificationCatalog> notificationCatalogs = notificationCatalogManager
                .findByIds(selectedItem);

        notificationCatalogManager.removeAll(notificationCatalogs);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/notification/notification-catalog-list.do";
    }

    @RequestMapping("notification-catalog-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = notificationCatalogManager.pagedQuery(page, propertyFilters);

        List<NotificationCatalog> notificationCatalogs = (List<NotificationCatalog>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("notification message");
        tableModel.addHeaders("id", "name");
        tableModel.setData(notificationCatalogs);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setNotificationCatalogManager(
            NotificationCatalogManager notificationCatalogManager) {
        this.notificationCatalogManager = notificationCatalogManager;
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
