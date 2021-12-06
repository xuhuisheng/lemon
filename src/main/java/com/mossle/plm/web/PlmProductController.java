package com.mossle.plm.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import com.mossle.plm.persistence.domain.PlmProduct;
import com.mossle.plm.persistence.manager.PlmProductManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("plm")
public class PlmProductController {
    private PlmProductManager plmProductManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private MessageHelper messageHelper;

    @RequestMapping("plm-product-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = plmProductManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "plm/plm-product-list";
    }

    @RequestMapping("plm-product-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            PlmProduct plmProduct = plmProductManager.get(id);
            model.addAttribute("model", plmProduct);
        }

        return "plm/plm-product-input";
    }

    @RequestMapping("plm-product-save")
    public String save(@ModelAttribute PlmProduct plmProduct,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        PlmProduct dest = null;

        Long id = plmProduct.getId();

        if (id != null) {
            dest = plmProductManager.get(id);
            beanMapper.copy(plmProduct, dest);
        } else {
            dest = plmProduct;
        }

        plmProductManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/plm/plm-product-list.do";
    }

    @RequestMapping("plm-product-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<PlmProduct> plmProducts = plmProductManager
                .findByIds(selectedItem);

        plmProductManager.removeAll(plmProducts);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/plm/plm-product-list.do";
    }

    @RequestMapping("plm-product-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = plmProductManager.pagedQuery(page, propertyFilters);

        List<PlmProduct> plmProducts = (List<PlmProduct>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("plm product");
        tableModel.addHeaders("id", "name");
        tableModel.setData(plmProducts);
        exportor.export(request, response, tableModel);
    }

    @RequestMapping("plm-product-view")
    public String view(@RequestParam("id") Long id, Model model) {
        PlmProduct plmProduct = plmProductManager.get(id);
        model.addAttribute("model", plmProduct);

        return "plm/plm-product-view";
    }

    // ~ ======================================================================
    @Resource
    public void setPlmProductManager(PlmProductManager plmProductManager) {
        this.plmProductManager = plmProductManager;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }
}
