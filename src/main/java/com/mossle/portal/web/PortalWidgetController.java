package com.mossle.portal.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.portal.persistence.domain.PortalWidget;
import com.mossle.portal.persistence.manager.PortalWidgetManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("portal")
public class PortalWidgetController {
    private PortalWidgetManager portalWidgetManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private MessageHelper messageHelper;

    @RequestMapping("portal-widget-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = portalWidgetManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "portal/portal-widget-list";
    }

    @RequestMapping("portal-widget-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            PortalWidget portalWidget = portalWidgetManager.get(id);
            model.addAttribute("model", portalWidget);
        }

        return "portal/portal-widget-input";
    }

    @RequestMapping("portal-widget-save")
    public String save(@ModelAttribute PortalWidget portalWidget,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        PortalWidget dest = null;

        Long id = portalWidget.getId();

        if (id != null) {
            dest = portalWidgetManager.get(id);
            beanMapper.copy(portalWidget, dest);
        } else {
            dest = portalWidget;
        }

        portalWidgetManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/portal/portal-widget-list.do";
    }

    @RequestMapping("portal-widget-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<PortalWidget> portalWidgets = portalWidgetManager
                .findByIds(selectedItem);

        portalWidgetManager.removeAll(portalWidgets);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/portal/portal-widget-list.do";
    }

    @RequestMapping("portal-widget-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = portalWidgetManager.pagedQuery(page, propertyFilters);

        List<PortalWidget> portalWidgets = (List<PortalWidget>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("portal base");
        tableModel.addHeaders("id", "client", "server", "resource");
        tableModel.setData(portalWidgets);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setPortalWidgetManager(PortalWidgetManager portalWidgetManager) {
        this.portalWidgetManager = portalWidgetManager;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }
}
