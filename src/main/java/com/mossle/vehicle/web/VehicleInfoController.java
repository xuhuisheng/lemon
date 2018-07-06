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

import com.mossle.vehicle.persistence.domain.VehicleInfo;
import com.mossle.vehicle.persistence.manager.VehicleInfoManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("vehicle")
public class VehicleInfoController {
    private VehicleInfoManager vehicleInfoManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;
    private TenantHolder tenantHolder;

    @RequestMapping("vehicle-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = vehicleInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "vehicle/vehicle-info-list";
    }

    @RequestMapping("vehicle-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            VehicleInfo vehicleInfo = vehicleInfoManager.get(id);
            model.addAttribute("model", vehicleInfo);
        }

        return "vehicle/vehicle-info-input";
    }

    @RequestMapping("vehicle-info-save")
    public String save(@ModelAttribute VehicleInfo vehicleInfo,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        VehicleInfo dest = null;

        Long id = vehicleInfo.getId();

        if (id != null) {
            dest = vehicleInfoManager.get(id);
            beanMapper.copy(vehicleInfo, dest);
        } else {
            dest = vehicleInfo;
            dest.setTenantId(tenantId);
        }

        vehicleInfoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/vehicle/vehicle-info-list.do";
    }

    @RequestMapping("vehicle-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<VehicleInfo> vehicleInfos = vehicleInfoManager
                .findByIds(selectedItem);

        vehicleInfoManager.removeAll(vehicleInfos);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/vehicle/vehicle-info-list.do";
    }

    @RequestMapping("vehicle-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = vehicleInfoManager.pagedQuery(page, propertyFilters);

        List<VehicleInfo> vehicleInfos = (List<VehicleInfo>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("vehicle info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(vehicleInfos);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setVehicleInfoManager(VehicleInfoManager vehicleInfoManager) {
        this.vehicleInfoManager = vehicleInfoManager;
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
