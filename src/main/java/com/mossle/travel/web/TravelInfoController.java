package com.mossle.travel.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserConnector;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.travel.persistence.domain.TravelInfo;
import com.mossle.travel.persistence.manager.TravelInfoManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("travel")
public class TravelInfoController {
    private TravelInfoManager travelInfoManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;
    private TenantHolder tenantHolder;

    @RequestMapping("travel-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = travelInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "travel/travel-info-list";
    }

    @RequestMapping("travel-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            TravelInfo travelInfo = travelInfoManager.get(id);
            model.addAttribute("model", travelInfo);
        }

        return "travel/travel-info-input";
    }

    @RequestMapping("travel-info-save")
    public String save(@ModelAttribute TravelInfo travelInfo,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        TravelInfo dest = null;

        Long id = travelInfo.getId();

        if (id != null) {
            dest = travelInfoManager.get(id);
            beanMapper.copy(travelInfo, dest);
        } else {
            dest = travelInfo;
            dest.setTenantId(tenantId);
        }

        travelInfoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/travel/travel-info-list.do";
    }

    @RequestMapping("travel-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<TravelInfo> travelInfos = travelInfoManager
                .findByIds(selectedItem);

        travelInfoManager.removeAll(travelInfos);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/travel/travel-info-list.do";
    }

    @RequestMapping("travel-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = travelInfoManager.pagedQuery(page, propertyFilters);

        List<TravelInfo> travelInfos = (List<TravelInfo>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("travel info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(travelInfos);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setTravelInfoManager(TravelInfoManager travelInfoManager) {
        this.travelInfoManager = travelInfoManager;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }

    @Resource
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
