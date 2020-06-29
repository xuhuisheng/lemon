package com.mossle.asset.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.user.UserConnector;

import com.mossle.asset.persistence.domain.SkuInfo;
import com.mossle.asset.persistence.domain.StockInfo;
import com.mossle.asset.persistence.domain.StockItem;
import com.mossle.asset.persistence.manager.SkuInfoManager;
import com.mossle.asset.persistence.manager.StockInfoManager;
import com.mossle.asset.persistence.manager.StockItemManager;

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
public class StockItemController {
    private static Logger logger = LoggerFactory
            .getLogger(StockItemController.class);
    private SkuInfoManager skuInfoManager;
    private StockInfoManager stockInfoManager;
    private StockItemManager stockItemManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;

    @RequestMapping("stock-item-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = stockItemManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "asset/stock-item-list";
    }

    @RequestMapping("stock-item-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            StockItem stockItem = stockItemManager.get(id);
            model.addAttribute("model", stockItem);
        }

        return "asset/stock-item-input";
    }

    @RequestMapping("stock-item-save")
    public String save(StockItem stockItem,
            RedirectAttributes redirectAttributes) {
        StockItem dest = null;

        Long id = stockItem.getId();

        if (id != null) {
            dest = stockItemManager.get(id);
            beanMapper.copy(stockItem, dest);
        } else {
            dest = stockItem;
        }

        stockItemManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/asset/stock-item-list.do";
    }

    @RequestMapping("stock-item-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<StockItem> stockItems = stockItemManager.findByIds(selectedItem);

        for (StockItem stockItem : stockItems) {
            // if (!skuInfo.getSkuCategories().isEmpty()) {
            // logger.info("children not empty : {}", skuInfo.getId());
            // continue;
            // }
            stockItemManager.remove(stockItem);
        }

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/asset/stock-item-list.do";
    }

    @RequestMapping("stock-item-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = stockItemManager.pagedQuery(page, propertyFilters);

        List<StockItem> stockItems = (List<StockItem>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("asset info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(stockItems);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setSkuInfoManager(SkuInfoManager skuInfoManager) {
        this.skuInfoManager = skuInfoManager;
    }

    @Resource
    public void setStockInfoManager(StockInfoManager stockInfoManager) {
        this.stockInfoManager = stockInfoManager;
    }

    @Resource
    public void setStockItemManager(StockItemManager stockItemManager) {
        this.stockItemManager = stockItemManager;
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
