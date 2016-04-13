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

import com.mossle.ticket.persistence.domain.TicketGroup;
import com.mossle.ticket.persistence.manager.TicketGroupManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("ticket")
public class TicketGroupController {
    private TicketGroupManager ticketGroupManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();

    @RequestMapping("ticket-group-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = ticketGroupManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "ticket/ticket-group-list";
    }

    @RequestMapping("ticket-group-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            TicketGroup ticketGroup = ticketGroupManager.get(id);
            model.addAttribute("model", ticketGroup);
        }

        return "ticket/ticket-group-input";
    }

    @RequestMapping("ticket-group-save")
    public String save(@ModelAttribute TicketGroup ticketGroup,
            RedirectAttributes redirectAttributes) {
        Long id = ticketGroup.getId();
        TicketGroup dest = null;

        if (id != null) {
            dest = ticketGroupManager.get(id);
            beanMapper.copy(ticketGroup, dest);
        } else {
            dest = ticketGroup;
        }

        ticketGroupManager.save(dest);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/ticket/ticket-group-list.do";
    }

    @RequestMapping("ticket-group-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<TicketGroup> ticketGroups = ticketGroupManager
                .findByIds(selectedItem);
        ticketGroupManager.removeAll(ticketGroups);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/ticket/ticket-group-list.do";
    }

    @RequestMapping("ticket-group-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = ticketGroupManager.pagedQuery(page, propertyFilters);

        List<TicketGroup> ticketGroups = (List<TicketGroup>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("ticketGroup");
        tableModel.addHeaders("id", "name");
        tableModel.setData(ticketGroups);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setTicketGroupManager(TicketGroupManager ticketGroupManager) {
        this.ticketGroupManager = ticketGroupManager;
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
