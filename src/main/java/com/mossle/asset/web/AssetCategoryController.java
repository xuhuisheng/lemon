package com.mossle.asset.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.user.UserConnector;

import com.mossle.asset.persistence.domain.AssetCategory;
import com.mossle.asset.persistence.manager.AssetCategoryManager;

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
public class AssetCategoryController {
    private static Logger logger = LoggerFactory
            .getLogger(AssetCategoryController.class);
    private AssetCategoryManager assetCategoryManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;

    @RequestMapping("asset-category-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = assetCategoryManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "asset/asset-category-list";
    }

    @RequestMapping("asset-category-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            AssetCategory assetCategory = assetCategoryManager.get(id);
            model.addAttribute("model", assetCategory);

            String hql = "from AssetCategory where assetCategory=null and id!=?";
            List<AssetCategory> assetCategories = this.assetCategoryManager
                    .find(hql, id);
            model.addAttribute("assetCategories", assetCategories);
        } else {
            String hql = "from AssetCategory where assetCategory=null";
            List<AssetCategory> assetCategories = this.assetCategoryManager
                    .find(hql);
            model.addAttribute("assetCategories", assetCategories);
        }

        return "asset/asset-category-input";
    }

    @RequestMapping("asset-category-save")
    public String save(@ModelAttribute AssetCategory assetCategory,
            @RequestParam Map<String, Object> parameterMap,
            @RequestParam("parentId") Long parentId,
            RedirectAttributes redirectAttributes) {
        AssetCategory dest = null;

        Long id = assetCategory.getId();

        if (id != null) {
            dest = assetCategoryManager.get(id);
            beanMapper.copy(assetCategory, dest);
        } else {
            dest = assetCategory;
        }

        if (parentId != null) {
            dest.setAssetCategory(assetCategoryManager.get(parentId));
        } else {
            dest.setAssetCategory(null);
        }

        assetCategoryManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/asset/asset-category-list.do";
    }

    @RequestMapping("asset-category-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<AssetCategory> assetCategories = assetCategoryManager
                .findByIds(selectedItem);

        for (AssetCategory assetCategory : assetCategories) {
            if (!assetCategory.getAssetCategories().isEmpty()) {
                logger.info("children not empty : {}", assetCategory.getId());

                continue;
            }

            assetCategoryManager.remove(assetCategory);
        }

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/asset/asset-category-list.do";
    }

    @RequestMapping("asset-category-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = assetCategoryManager.pagedQuery(page, propertyFilters);

        List<AssetCategory> assetCategorys = (List<AssetCategory>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("asset info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(assetCategorys);
        exportor.export(request, response, tableModel);
    }

    @RequestMapping("asset-category-children")
    @ResponseBody
    public BaseDTO children(@RequestParam("parentId") Long parentId) {
        List<AssetCategory> assetCategories = this.assetCategoryManager.findBy(
                "assetCategory.id", parentId);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (AssetCategory assetCategory : assetCategories) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", assetCategory.getId());
            map.put("name", assetCategory.getName());
            list.add(map);
        }

        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);
        baseDto.setData(list);

        return baseDto;
    }

    // ~ ======================================================================
    @Resource
    public void setAssetCategoryManager(
            AssetCategoryManager assetCategoryManager) {
        this.assetCategoryManager = assetCategoryManager;
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
