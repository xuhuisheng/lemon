package com.mossle.asset.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.user.UserConnector;

import com.mossle.asset.persistence.domain.AssetCategory;
import com.mossle.asset.persistence.domain.AssetHistory;
import com.mossle.asset.persistence.domain.AssetInfo;
import com.mossle.asset.persistence.manager.AssetCategoryManager;
import com.mossle.asset.persistence.manager.AssetHistoryManager;
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
public class AssetHistoryController {
    private AssetInfoManager assetInfoManager;
    private AssetCategoryManager assetCategoryManager;
    private AssetHistoryManager assetHistoryManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;

    @RequestMapping("asset-history-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = assetHistoryManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "asset/asset-history-list";
    }

    @RequestMapping("asset-history-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            AssetHistory assetHistory = assetHistoryManager.get(id);
            model.addAttribute("model", assetHistory);
        }

        return "asset/asset-history-input";
    }

    @RequestMapping("asset-history-save")
    public String save(@ModelAttribute AssetHistory assetHistory,
            @RequestParam Map<String, Object> parameterMap,
            @RequestParam("infoId") Long infoId,
            RedirectAttributes redirectAttributes) {
        AssetHistory dest = null;

        Long id = assetHistory.getId();

        if (id != null) {
            dest = assetHistoryManager.get(id);
            beanMapper.copy(assetHistory, dest);
        } else {
            dest = assetHistory;
        }

        if (infoId != null) {
            dest.setAssetInfo(this.assetInfoManager.get(infoId));
        } else {
            dest.setAssetInfo(null);
        }

        assetHistoryManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/asset/asset-history-list.do";
    }

    @RequestMapping("asset-history-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<AssetHistory> assetHistories = assetHistoryManager
                .findByIds(selectedItem);

        assetHistoryManager.removeAll(assetHistories);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/asset/asset-history-list.do";
    }

    @RequestMapping("asset-history-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = assetHistoryManager.pagedQuery(page, propertyFilters);

        List<AssetHistory> assetHistories = (List<AssetHistory>) page
                .getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("asset history");
        tableModel.addHeaders("id", "name");
        tableModel.setData(assetHistories);
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
    public void setAssetHistoryManager(AssetHistoryManager assetHistoryManager) {
        this.assetHistoryManager = assetHistoryManager;
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
