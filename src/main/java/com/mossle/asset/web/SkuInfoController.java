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
import com.mossle.asset.persistence.domain.SkuInfo;
import com.mossle.asset.persistence.manager.SkuCategoryManager;
import com.mossle.asset.persistence.manager.SkuInfoManager;

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
public class SkuInfoController {
    private static Logger logger = LoggerFactory
            .getLogger(SkuInfoController.class);
    private SkuInfoManager skuInfoManager;
    private SkuCategoryManager skuCategoryManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;

    @RequestMapping("sku-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = skuInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "asset/sku-info-list";
    }

    @RequestMapping("sku-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            SkuInfo skuInfo = skuInfoManager.get(id);
            model.addAttribute("model", skuInfo);
        }

        String hql = "from SkuCategory where skuCategory is null";
        List<SkuInfo> skuCategories = this.skuInfoManager.find(hql);
        model.addAttribute("skuCategories", skuCategories);

        return "asset/sku-info-input";
    }

    @RequestMapping("sku-info-save")
    public String save(@ModelAttribute SkuInfo skuInfo,
            @RequestParam Map<String, Object> parameterMap,
            @RequestParam("categoryId") Long categoryId,
            RedirectAttributes redirectAttributes) {
        SkuInfo dest = null;

        Long id = skuInfo.getId();

        if (id != null) {
            dest = skuInfoManager.get(id);
            beanMapper.copy(skuInfo, dest);
        } else {
            dest = skuInfo;
        }

        if (categoryId != null) {
            dest.setSkuCategory(skuCategoryManager.get(categoryId));
        } else {
            dest.setSkuCategory(null);
        }

        skuInfoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/asset/sku-info-list.do";
    }

    @RequestMapping("sku-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<SkuInfo> skuInfos = skuInfoManager.findByIds(selectedItem);

        for (SkuInfo skuInfo : skuInfos) {
            // if (!skuInfo.getSkuCategories().isEmpty()) {
            // logger.info("children not empty : {}", skuInfo.getId());
            // continue;
            // }
            skuInfoManager.remove(skuInfo);
        }

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/asset/sku-info-list.do";
    }

    @RequestMapping("sku-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = skuInfoManager.pagedQuery(page, propertyFilters);

        List<SkuInfo> skuInfos = (List<SkuInfo>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("asset info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(skuInfos);
        exportor.export(request, response, tableModel);
    }

    @RequestMapping("sku-info-children")
    @ResponseBody
    public BaseDTO children(@RequestParam("parentId") Long parentId) {
        List<SkuInfo> assetCategories = this.skuInfoManager.findBy(
                "skuInfo.id", parentId);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (SkuInfo skuInfo : assetCategories) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", skuInfo.getId());
            map.put("name", skuInfo.getName());
            list.add(map);
        }

        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);
        baseDto.setData(list);

        return baseDto;
    }

    // ~ ======================================================================
    @Resource
    public void setSkuInfoManager(SkuInfoManager skuInfoManager) {
        this.skuInfoManager = skuInfoManager;
    }

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
