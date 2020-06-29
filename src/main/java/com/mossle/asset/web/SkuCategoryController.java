package com.mossle.asset.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.user.UserConnector;

import com.mossle.asset.persistence.domain.SkuCategory;
import com.mossle.asset.persistence.manager.SkuCategoryManager;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;
import com.mossle.core.util.BaseDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("asset")
public class SkuCategoryController {
    private static Logger logger = LoggerFactory
            .getLogger(SkuCategoryController.class);
    private SkuCategoryManager skuCategoryManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;

    @RequestMapping("sku-category-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = skuCategoryManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "asset/sku-category-list";
    }

    @RequestMapping("sku-category-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            SkuCategory skuCategory = skuCategoryManager.get(id);
            model.addAttribute("model", skuCategory);

            String hql = "from SkuCategory where skuCategory is null and id!=?";
            List<SkuCategory> skuCategories = this.skuCategoryManager.find(hql,
                    id);
            model.addAttribute("skCatuegories", skuCategories);
        } else {
            String hql = "from SkuCategory where skuCategory is null";
            List<SkuCategory> skuCategories = this.skuCategoryManager.find(hql);
            model.addAttribute("skuCategories", skuCategories);
        }

        return "asset/sku-category-input";
    }

    @RequestMapping("sku-category-save")
    public String save(@ModelAttribute SkuCategory skuCategory,
            @RequestParam Map<String, Object> parameterMap,
            @RequestParam("parentId") Long parentId,
            RedirectAttributes redirectAttributes) {
        SkuCategory dest = null;

        Long id = skuCategory.getId();

        if (id != null) {
            dest = skuCategoryManager.get(id);
            beanMapper.copy(skuCategory, dest);
        } else {
            dest = skuCategory;
        }

        if (parentId != null) {
            dest.setSkuCategory(skuCategoryManager.get(parentId));
        } else {
            dest.setSkuCategory(null);
        }

        skuCategoryManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/asset/sku-category-list.do";
    }

    @RequestMapping("sku-category-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<SkuCategory> skuCategories = skuCategoryManager
                .findByIds(selectedItem);

        for (SkuCategory skuCategory : skuCategories) {
            if (!skuCategory.getSkuCategories().isEmpty()) {
                logger.info("children not empty : {}", skuCategory.getId());

                continue;
            }

            skuCategoryManager.remove(skuCategory);
        }

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/asset/sku-category-list.do";
    }

    @RequestMapping("sku-category-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = skuCategoryManager.pagedQuery(page, propertyFilters);

        List<SkuCategory> skuCategorys = (List<SkuCategory>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("sku info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(skuCategorys);
        exportor.export(request, response, tableModel);
    }

    @RequestMapping("sku-category-children")
    @ResponseBody
    public BaseDTO children(@RequestParam("parentId") Long parentId) {
        List<SkuCategory> skuCategories = this.skuCategoryManager.findBy(
                "skuCategory.id", parentId);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (SkuCategory skuCategory : skuCategories) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", skuCategory.getId());
            map.put("name", skuCategory.getName());
            list.add(map);
        }

        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);
        baseDto.setData(list);

        return baseDto;
    }

    // ~ ======================================================================
    @Resource
    public void setSkuCategoryManager(SkuCategoryManager skuCategoryManager) {
        this.skuCategoryManager = skuCategoryManager;
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
