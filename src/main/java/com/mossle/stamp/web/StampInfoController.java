package com.mossle.stamp.web;

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

import com.mossle.stamp.persistence.domain.StampInfo;
import com.mossle.stamp.persistence.manager.StampInfoManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("stamp")
public class StampInfoController {
    private StampInfoManager stampInfoManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;
    private TenantHolder tenantHolder;

    @RequestMapping("stamp-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = stampInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "stamp/stamp-info-list";
    }

    @RequestMapping("stamp-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            StampInfo stampInfo = stampInfoManager.get(id);
            model.addAttribute("model", stampInfo);
        }

        return "stamp/stamp-info-input";
    }

    @RequestMapping("stamp-info-save")
    public String save(@ModelAttribute StampInfo stampInfo,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        StampInfo dest = null;

        Long id = stampInfo.getId();

        if (id != null) {
            dest = stampInfoManager.get(id);
            beanMapper.copy(stampInfo, dest);
        } else {
            dest = stampInfo;
            dest.setTenantId(tenantId);
        }

        stampInfoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/stamp/stamp-info-list.do";
    }

    @RequestMapping("stamp-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<StampInfo> stampInfos = stampInfoManager.findByIds(selectedItem);

        stampInfoManager.removeAll(stampInfos);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/stamp/stamp-info-list.do";
    }

    @RequestMapping("stamp-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = stampInfoManager.pagedQuery(page, propertyFilters);

        List<StampInfo> stampInfos = (List<StampInfo>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("stamp info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(stampInfos);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setStampInfoManager(StampInfoManager stampInfoManager) {
        this.stampInfoManager = stampInfoManager;
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
