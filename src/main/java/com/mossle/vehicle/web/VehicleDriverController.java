package com.mossle.vehicle.web;

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

import com.mossle.vehicle.persistence.domain.VehicleDriver;
import com.mossle.vehicle.persistence.manager.VehicleDriverManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("vehicle")
public class VehicleDriverController {
    private VehicleDriverManager vehicleDriverManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;
    private TenantHolder tenantHolder;

    @RequestMapping("vehicle-driver-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = vehicleDriverManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "vehicle/vehicle-driver-list";
    }

    @RequestMapping("vehicle-driver-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            VehicleDriver vehicleDriver = vehicleDriverManager.get(id);
            model.addAttribute("model", vehicleDriver);
        }

        return "vehicle/vehicle-driver-input";
    }

    @RequestMapping("vehicle-driver-save")
    public String save(@ModelAttribute VehicleDriver vehicleDriver,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        VehicleDriver dest = null;

        Long id = vehicleDriver.getId();

        if (id != null) {
            dest = vehicleDriverManager.get(id);
            beanMapper.copy(vehicleDriver, dest);
        } else {
            dest = vehicleDriver;
            dest.setTenantId(tenantId);
        }

        vehicleDriverManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/vehicle/vehicle-driver-list.do";
    }

    @RequestMapping("vehicle-driver-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<VehicleDriver> vehicleDrivers = vehicleDriverManager
                .findByIds(selectedItem);

        vehicleDriverManager.removeAll(vehicleDrivers);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/vehicle/vehicle-driver-list.do";
    }

    @RequestMapping("vehicle-driver-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = vehicleDriverManager.pagedQuery(page, propertyFilters);

        List<VehicleDriver> vehicleDrivers = (List<VehicleDriver>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("vehicle info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(vehicleDrivers);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setVehicleDriverManager(
            VehicleDriverManager vehicleDriverManager) {
        this.vehicleDriverManager = vehicleDriverManager;
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
