package com.mossle.asset.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.user.UserConnector;

import com.mossle.asset.persistence.domain.AssetInfo;
import com.mossle.asset.persistence.domain.AssetLend;
import com.mossle.asset.persistence.manager.AssetInfoManager;
import com.mossle.asset.persistence.manager.AssetLendManager;

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
public class AssetLendController {
    private AssetLendManager assetLendManager;
    private AssetInfoManager assetInfoManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;

    @RequestMapping("asset-lend-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = assetLendManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "asset/asset-lend-list";
    }

    @RequestMapping("asset-lend-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            AssetLend assetLend = assetLendManager.get(id);
            model.addAttribute("model", assetLend);
        }

        List<AssetInfo> assetInfos = this.assetInfoManager.getAll();
        model.addAttribute("assetInfos", assetInfos);

        return "asset/asset-lend-input";
    }

    @RequestMapping("asset-lend-save")
    public String save(@ModelAttribute AssetLend assetLend,
            @RequestParam Map<String, Object> parameterMap,
            @RequestParam("infoId") Long infoId,
            RedirectAttributes redirectAttributes) {
        AssetLend dest = null;

        Long id = assetLend.getId();

        if (id != null) {
            dest = assetLendManager.get(id);
            beanMapper.copy(assetLend, dest);
        } else {
            dest = assetLend;
        }

        if (infoId != null) {
            dest.setAssetInfo(this.assetInfoManager.get(infoId));
        } else {
            dest.setAssetInfo(null);
        }

        assetLendManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/asset/asset-lend-list.do";
    }

    @RequestMapping("asset-lend-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<AssetLend> assetLends = assetLendManager.findByIds(selectedItem);

        assetLendManager.removeAll(assetLends);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/asset/asset-lend-list.do";
    }

    @RequestMapping("asset-lend-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = assetLendManager.pagedQuery(page, propertyFilters);

        List<AssetLend> assetLends = (List<AssetLend>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("asset info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(assetLends);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setAssetLendManager(AssetLendManager assetLendManager) {
        this.assetLendManager = assetLendManager;
    }

    @Resource
    public void setAssetInfoManager(AssetInfoManager assetInfoManager) {
        this.assetInfoManager = assetInfoManager;
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
