package com.mossle.travel.web;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserConnector;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.travel.persistence.domain.TravelInfo;
import com.mossle.travel.persistence.domain.TravelItem;
import com.mossle.travel.persistence.manager.TravelInfoManager;
import com.mossle.travel.persistence.manager.TravelItemManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("travel")
public class TravelController {
    private TravelInfoManager travelInfoManager;
    private TravelItemManager travelItemManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;
    private TenantHolder tenantHolder;
    private CurrentUserHolder currentUserHolder;

    @RequestMapping("index")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        String userId = currentUserHolder.getUserId();

        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_userId", userId));
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page.setDefaultOrder("id", Page.DESC);

        page = travelInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "travel/index";
    }

    @RequestMapping("input")
    public String input(Model model) {
        return "travel/input";
    }

    @RequestMapping("save")
    public String save(TravelInfo travelInfo,
            @RequestParam("type") List<String> types,
            @RequestParam("vehicle") List<String> vehicles,
            @RequestParam("startDate") List<String> startDates,
            @RequestParam("endDate") List<String> endDates,
            @RequestParam("startCity") List<String> startCities,
            @RequestParam("endCity") List<String> endCities) throws Exception {
        String tenantId = tenantHolder.getTenantId();
        String userId = currentUserHolder.getUserId();
        travelInfo.setCreateTime(new Date());
        travelInfo.setUserId(userId);
        travelInfo.setTenantId(tenantId);
        travelInfo.setStatus("active");
        travelInfoManager.save(travelInfo);
        travelInfo.setCode(Long.toString(travelInfo.getId()));
        travelInfoManager.save(travelInfo);

        for (int i = 0; i < types.size(); i++) {
            String type = types.get(i);
            String vehicle = vehicles.get(i);
            String startDate = startDates.get(i);
            String endDate = endDates.get(i);
            String startCity = startCities.get(i);
            String endCity = endCities.get(i);
            TravelItem travelItem = new TravelItem();
            travelItem.setType(type);
            travelItem.setVehicle(vehicle);
            travelItem.setStartDate(new SimpleDateFormat("yyyy-MM-dd")
                    .parse(startDate));
            travelItem.setEndDate(new SimpleDateFormat("yyyy-MM-dd")
                    .parse(endDate));
            travelItem.setStartCity(startCity);
            travelItem.setEndCity(endCity);
            travelItem.setPriority(i);
            travelItem.setTravelInfo(travelInfo);
            travelItemManager.save(travelItem);
        }

        return "redirect:/travel/index.do";
    }

    @RequestMapping("view")
    public String view(@RequestParam("id") Long id, Model model) {
        model.addAttribute("travelInfo", travelInfoManager.get(id));

        return "travel/view";
    }

    // ~ ======================================================================
    @Resource
    public void setTravelInfoManager(TravelInfoManager travelInfoManager) {
        this.travelInfoManager = travelInfoManager;
    }

    @Resource
    public void setTravelItemManager(TravelItemManager travelItemManager) {
        this.travelItemManager = travelItemManager;
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

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }
}
