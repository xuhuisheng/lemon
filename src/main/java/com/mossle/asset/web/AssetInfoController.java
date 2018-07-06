package com.mossle.asset.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.user.UserConnector;

import com.mossle.asset.persistence.domain.AssetCategory;
import com.mossle.asset.persistence.domain.AssetInfo;
import com.mossle.asset.persistence.manager.AssetCategoryManager;
import com.mossle.asset.persistence.manager.AssetInfoManager;

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
@RequestMapping("asset")
public class AssetInfoController {
    private AssetInfoManager assetInfoManager;
    private AssetCategoryManager assetCategoryManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;

    @RequestMapping("asset-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = assetInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "asset/asset-info-list";
    }

    @RequestMapping("asset-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            AssetInfo assetInfo = assetInfoManager.get(id);
            model.addAttribute("model", assetInfo);
        }

        List<AssetCategory> assetCategories = this.assetCategoryManager
                .find("from AssetCategory where assetCategory=null");
        model.addAttribute("assetCategories", assetCategories);

        return "asset/asset-info-input";
    }

    @RequestMapping("asset-info-save")
    public String save(@ModelAttribute AssetInfo assetInfo,
            @RequestParam Map<String, Object> parameterMap,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("subCategoryId") Long subCategoryId,
            RedirectAttributes redirectAttributes) {
        AssetInfo dest = null;

        Long id = assetInfo.getId();

        if (id != null) {
            dest = assetInfoManager.get(id);
            beanMapper.copy(assetInfo, dest);
        } else {
            dest = assetInfo;
        }

        if (categoryId != null) {
            dest.setAssetCategoryByCategoryId(this.assetCategoryManager
                    .get(categoryId));
        } else {
            dest.setAssetCategoryByCategoryId(null);
        }

        if (subCategoryId != null) {
            dest.setAssetCategoryBySubCategoryId(this.assetCategoryManager
                    .get(subCategoryId));
        } else {
            dest.setAssetCategoryBySubCategoryId(null);
        }

        assetInfoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/asset/asset-info-list.do";
    }

    @RequestMapping("asset-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<AssetInfo> assetInfos = assetInfoManager.findByIds(selectedItem);

        assetInfoManager.removeAll(assetInfos);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/asset/asset-info-list.do";
    }

    @RequestMapping("asset-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = assetInfoManager.pagedQuery(page, propertyFilters);

        List<AssetInfo> assetInfos = (List<AssetInfo>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("asset info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(assetInfos);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setAssetInfoManager(AssetInfoManager assetInfoManager) {
        this.assetInfoManager = assetInfoManager;
    }

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
