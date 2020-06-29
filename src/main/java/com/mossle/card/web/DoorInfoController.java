package com.mossle.card.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserConnector;

import com.mossle.card.persistence.domain.DoorInfo;
import com.mossle.card.persistence.manager.DoorInfoManager;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("card")
public class DoorInfoController {
    private DoorInfoManager doorInfoManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;
    private TenantHolder tenantHolder;

    @RequestMapping("door-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        // propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = doorInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "card/door-info-list";
    }

    @RequestMapping("door-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            DoorInfo doorInfo = doorInfoManager.get(id);
            model.addAttribute("model", doorInfo);
        }

        return "card/door-info-input";
    }

    @RequestMapping("door-info-save")
    public String save(@ModelAttribute DoorInfo doorInfo,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        DoorInfo dest = null;

        Long id = doorInfo.getId();

        if (id != null) {
            dest = doorInfoManager.get(id);
            beanMapper.copy(doorInfo, dest);
        } else {
            dest = doorInfo;

            // dest.setTenantId(tenantId);
        }

        doorInfoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/card/door-info-list.do";
    }

    @RequestMapping("door-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<DoorInfo> doorInfos = doorInfoManager.findByIds(selectedItem);

        doorInfoManager.removeAll(doorInfos);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/card/door-info-list.do";
    }

    @RequestMapping("door-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = doorInfoManager.pagedQuery(page, propertyFilters);

        List<DoorInfo> doorInfos = (List<DoorInfo>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("card info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(doorInfos);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setDoorInfoManager(DoorInfoManager doorInfoManager) {
        this.doorInfoManager = doorInfoManager;
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
