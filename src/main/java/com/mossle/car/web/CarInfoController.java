package com.mossle.car.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.api.user.UserConnector;

import com.mossle.car.domain.CarInfo;
import com.mossle.car.manager.CarInfoManager;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;

import com.mossle.ext.export.Exportor;
import com.mossle.ext.export.TableModel;

import com.mossle.security.util.SpringSecurityUtils;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("car")
public class CarInfoController {
    private CarInfoManager carInfoManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;

    @RequestMapping("car-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = carInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "car/car-info-list";
    }

    @RequestMapping("car-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            CarInfo carInfo = carInfoManager.get(id);
            model.addAttribute("model", carInfo);
        }

        return "car/car-info-input";
    }

    @RequestMapping("car-info-save")
    public String save(@ModelAttribute CarInfo carInfo,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        CarInfo dest = null;

        Long id = carInfo.getId();

        if (id != null) {
            dest = carInfoManager.get(id);
            beanMapper.copy(carInfo, dest);
        } else {
            dest = carInfo;
        }

        carInfoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/car/car-info-list.do";
    }

    @RequestMapping("car-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<CarInfo> carInfos = carInfoManager.findByIds(selectedItem);

        carInfoManager.removeAll(carInfos);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/car/car-info-list.do";
    }

    @RequestMapping("car-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletResponse response) throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = carInfoManager.pagedQuery(page, propertyFilters);

        List<CarInfo> carInfos = (List<CarInfo>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("car info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(carInfos);
        exportor.export(response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setCarInfoManager(CarInfoManager carInfoManager) {
        this.carInfoManager = carInfoManager;
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
}
