package com.mossle.vehicle.web;

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

import com.mossle.vehicle.persistence.domain.VehicleInfo;
import com.mossle.vehicle.persistence.domain.VehicleTask;
import com.mossle.vehicle.persistence.manager.VehicleInfoManager;
import com.mossle.vehicle.persistence.manager.VehicleTaskManager;
import com.mossle.vehicle.persistence.domain.VehicleRequest;
import com.mossle.vehicle.persistence.manager.VehicleRequestManager;
import com.mossle.vehicle.service.VehicleService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("vehicle")
public class VehicleController {
    private static Logger logger = LoggerFactory
            .getLogger(VehicleController.class);
    private VehicleTaskManager vehicleTaskManager;
    private VehicleInfoManager vehicleInfoManager;
    private VehicleRequestManager vehicleRequestManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;
    private TenantHolder tenantHolder;
    private VehicleService vehicleService;
    private CurrentUserHolder currentUserHolder;

    @RequestMapping("index")
    public String index(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        String userId = currentUserHolder.getUserId();

        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        propertyFilters.add(new PropertyFilter("EQS_userId", userId));
        page.setDefaultOrder("id", Page.DESC);

        page = vehicleRequestManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "vehicle/index";
    }

    // ~ ======================================================================
    @Resource
    public void setVehicleTaskManager(VehicleTaskManager vehicleTaskManager) {
        this.vehicleTaskManager = vehicleTaskManager;
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
    public void setVehicleInfoManager(VehicleInfoManager vehicleInfoManager) {
        this.vehicleInfoManager = vehicleInfoManager;
    }

    @Resource
    public void setVehicleService(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setVehicleRequestManager(VehicleRequestManager vehicleRequestManager) {
        this.vehicleRequestManager=vehicleRequestManager;
    }
}
