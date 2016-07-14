package com.mossle.bpm.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.bpm.persistence.domain.BpmCategory;
import com.mossle.bpm.persistence.manager.BpmCategoryManager;

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
@RequestMapping("bpm")
public class BpmCategoryController {
    private BpmCategoryManager bpmCategoryManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private TenantHolder tenantHolder;
    private BeanMapper beanMapper = new BeanMapper();

    @RequestMapping("bpm-category-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        page = bpmCategoryManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "bpm/bpm-category-list";
    }

    @RequestMapping("bpm-category-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            BpmCategory bpmCategory = bpmCategoryManager.get(id);
            model.addAttribute("model", bpmCategory);
        }

        return "bpm/bpm-category-input";
    }

    @RequestMapping("bpm-category-save")
    public String save(@ModelAttribute BpmCategory bpmCategory,
            RedirectAttributes redirectAttributes) {
        BpmCategory dest = null;
        Long id = bpmCategory.getId();

        if (id != null) {
            dest = bpmCategoryManager.get(id);
            beanMapper.copy(bpmCategory, dest);
        } else {
            dest = bpmCategory;

            String tenantId = tenantHolder.getTenantId();
            dest.setTenantId(tenantId);
        }

        bpmCategoryManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/bpm/bpm-category-list.do";
    }

    @RequestMapping("bpm-category-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<BpmCategory> bpmCategories = bpmCategoryManager
                .findByIds(selectedItem);
        bpmCategoryManager.removeAll(bpmCategories);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/bpm/bpm-category-list.do";
    }

    @RequestMapping("bpm-category-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = bpmCategoryManager.pagedQuery(page, propertyFilters);

        List<BpmCategory> bpmCategories = (List<BpmCategory>) page.getResult();
        TableModel tableModel = new TableModel();
        tableModel.setName("bpm-category");
        tableModel.addHeaders("id", "name");
        tableModel.setData(bpmCategories);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setBpmCategoryManager(BpmCategoryManager bpmCategoryManager) {
        this.bpmCategoryManager = bpmCategoryManager;
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
