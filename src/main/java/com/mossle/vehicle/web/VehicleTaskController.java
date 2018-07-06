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
import com.mossle.vehicle.persistence.domain.VehicleTask;
import com.mossle.vehicle.persistence.manager.VehicleInfoManager;
import com.mossle.vehicle.persistence.manager.VehicleTaskManager;
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
public class VehicleTaskController {
    private static Logger logger = LoggerFactory
            .getLogger(VehicleTaskController.class);
    private VehicleTaskManager vehicleTaskManager;
    private VehicleInfoManager vehicleInfoManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;
    private TenantHolder tenantHolder;
    private VehicleService vehicleService;

    @RequestMapping("vehicle-task-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = vehicleTaskManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "vehicle/vehicle-task-list";
    }

    @RequestMapping("vehicle-task-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            VehicleTask vehicleTask = vehicleTaskManager.get(id);
            model.addAttribute("model", vehicleTask);
        }

        model.addAttribute("vehicleInfos", vehicleInfoManager.getAll());

        return "vehicle/vehicle-task-input";
    }

    @RequestMapping("vehicle-task-save")
    public String save(@ModelAttribute VehicleTask vehicleTask,
            @RequestParam Map<String, Object> parameterMap,
            @RequestParam("infoId") Long infoId,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        VehicleTask dest = null;

        Long id = vehicleTask.getId();

        if (id != null) {
            dest = vehicleTaskManager.get(id);
            beanMapper.copy(vehicleTask, dest);
        } else {
            dest = vehicleTask;
            dest.setTenantId(tenantId);
        }

        dest.setVehicleInfo(vehicleInfoManager.get(infoId));

        if (!vehicleService.isValidTime(dest, dest.getVehicleInfo())) {
            logger.info("车辆在当前时间段已被占用");

            return "vehicle/vehicle-task-input";
        }

        vehicleTaskManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/vehicle/vehicle-task-list.do";
    }

    @RequestMapping("vehicle-task-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<VehicleTask> vehicleTasks = vehicleTaskManager
                .findByIds(selectedItem);

        vehicleTaskManager.removeAll(vehicleTasks);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/vehicle/vehicle-task-list.do";
    }

    @RequestMapping("vehicle-task-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = vehicleTaskManager.pagedQuery(page, propertyFilters);

        List<VehicleTask> vehicleTasks = (List<VehicleTask>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("vehicle info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(vehicleTasks);
        exportor.export(request, response, tableModel);
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
}
