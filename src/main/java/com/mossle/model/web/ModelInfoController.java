package com.mossle.model.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.export.Exportor;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.model.persistence.domain.ModelInfo;
import com.mossle.model.persistence.manager.ModelInfoManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("model")
public class ModelInfoController {
    private ModelInfoManager modelInfoManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private TenantHolder tenantHolder;

    @RequestMapping("model-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = modelInfoManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "model/model-info-list";
    }

    @RequestMapping("model-info-input")
    public String input(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "type", required = false) String whitelistTypeCode,
            Model model) {
        ModelInfo modelInfo = null;

        if (id != null) {
            modelInfo = modelInfoManager.get(id);
            model.addAttribute("model", modelInfo);
        }

        return "model/model-info-input";
    }

    @RequestMapping("model-info-save")
    public String save(@ModelAttribute ModelInfo modelInfo,
            RedirectAttributes redirectAttributes) {
        String tenantId = tenantHolder.getTenantId();
        Long id = modelInfo.getId();
        ModelInfo dest = null;

        if (id != null) {
            dest = modelInfoManager.get(id);
            beanMapper.copy(modelInfo, dest);
        } else {
            dest = modelInfo;
            dest.setTenantId(tenantId);
        }

        modelInfoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/model/model-info-list.do";
    }

    @RequestMapping("model-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<ModelInfo> modelInfos = modelInfoManager.findByIds(selectedItem);

        for (ModelInfo modelInfo : modelInfos) {
            modelInfoManager.remove(modelInfo);
        }

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/model/model-info-list.do";
    }

    // ~ ======================================================================
    @Resource
    public void setModelInfoManager(ModelInfoManager modelInfoManager) {
        this.modelInfoManager = modelInfoManager;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
