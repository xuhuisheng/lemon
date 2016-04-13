package com.mossle.ticket.web;

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

import com.mossle.ticket.persistence.domain.TicketCatalog;
import com.mossle.ticket.persistence.manager.TicketCatalogManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("ticket")
public class TicketCatalogController {
    private TicketCatalogManager ticketCatalogManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();

    @RequestMapping("ticket-catalog-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = ticketCatalogManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "ticket/ticket-catalog-list";
    }

    @RequestMapping("ticket-catalog-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            TicketCatalog ticketCatalog = ticketCatalogManager.get(id);
            model.addAttribute("model", ticketCatalog);
        }

        return "ticket/ticket-catalog-input";
    }

    @RequestMapping("ticket-catalog-save")
    public String save(@ModelAttribute TicketCatalog ticketCatalog,
            RedirectAttributes redirectAttributes) {
        Long id = ticketCatalog.getId();
        TicketCatalog dest = null;

        if (id != null) {
            dest = ticketCatalogManager.get(id);
            beanMapper.copy(ticketCatalog, dest);
        } else {
            dest = ticketCatalog;
        }

        ticketCatalogManager.save(dest);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/ticket/ticket-catalog-list.do";
    }

    @RequestMapping("ticket-catalog-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<TicketCatalog> ticketCatalogs = ticketCatalogManager
                .findByIds(selectedItem);
        ticketCatalogManager.removeAll(ticketCatalogs);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/ticket/ticket-catalog-list.do";
    }

    @RequestMapping("ticket-catalog-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = ticketCatalogManager.pagedQuery(page, propertyFilters);

        List<TicketCatalog> ticketCatalogs = (List<TicketCatalog>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("ticketCatalog");
        tableModel.addHeaders("id", "name");
        tableModel.setData(ticketCatalogs);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setTicketCatalogManager(
            TicketCatalogManager ticketCatalogManager) {
        this.ticketCatalogManager = ticketCatalogManager;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }
}
