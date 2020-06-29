package com.mossle.asset.web;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.api.user.UserConnector;

import com.mossle.asset.persistence.domain.AssetInfo;
import com.mossle.asset.persistence.domain.AssetLend;
import com.mossle.asset.persistence.domain.AssetRequest;
import com.mossle.asset.persistence.domain.AssetRequestInfo;
import com.mossle.asset.persistence.domain.AssetRequestItem;
import com.mossle.asset.persistence.domain.SkuInfo;
import com.mossle.asset.persistence.domain.StockInfo;
import com.mossle.asset.persistence.domain.StockItem;
import com.mossle.asset.persistence.manager.AssetInfoManager;
import com.mossle.asset.persistence.manager.AssetLendManager;
import com.mossle.asset.persistence.manager.AssetRequestInfoManager;
import com.mossle.asset.persistence.manager.AssetRequestItemManager;
import com.mossle.asset.persistence.manager.AssetRequestManager;
import com.mossle.asset.persistence.manager.SkuInfoManager;
import com.mossle.asset.persistence.manager.StockInfoManager;
import com.mossle.asset.persistence.manager.StockItemManager;

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
@RequestMapping("asset/my")
public class AssetMyController {
    private AssetLendManager assetLendManager;
    private AssetInfoManager assetInfoManager;
    private AssetRequestManager assetRequestManager;
    private AssetRequestInfoManager assetRequestInfoManager;
    private AssetRequestItemManager assetRequestItemManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;
    private CurrentUserHolder currentUserHolder;
    private SkuInfoManager skuInfoManager;
    private StockInfoManager stockInfoManager;
    private StockItemManager stockItemManager;

    @RequestMapping("index")
    public String index(Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String userId = currentUserHolder.getUserId();

        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_userId", userId));
        page.setDefaultOrder("id", Page.DESC);

        page = stockItemManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "asset/my/index";
    }

    @RequestMapping("request")
    public String request(Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String userId = currentUserHolder.getUserId();

        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_userId", userId));
        page.setDefaultOrder("id", Page.DESC);

        page = assetRequestInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "asset/my/request";
    }

    @RequestMapping("request-input")
    public String request(Model model) {
        String userId = currentUserHolder.getUserId();

        return "asset/my/request-input";
    }

    @RequestMapping("request-save")
    public String requestSave(
            @RequestParam("category") List<String> categories,
            @RequestParam("name") List<String> names,
            @RequestParam("num") List<Integer> nums) {
        String userId = currentUserHolder.getUserId();
        Date now = new Date();
        AssetRequestInfo assetRequestInfo = new AssetRequestInfo();
        assetRequestInfo.setCode("code");
        assetRequestInfo.setUserId(userId);
        assetRequestInfo.setCreateTime(now);
        assetRequestInfo.setStatus("active");
        assetRequestInfoManager.save(assetRequestInfo);

        for (int i = 0; i < categories.size(); i++) {
            String category = categories.get(i);
            String name = names.get(i);
            Integer num = nums.get(i);
            AssetRequestItem assetRequestItem = new AssetRequestItem();
            assetRequestItem.setProductCategory(category);
            assetRequestItem.setProductName(name);
            assetRequestItem.setProductNum(num);
            assetRequestItem.setAssetRequestInfo(assetRequestInfo);
            assetRequestItemManager.save(assetRequestItem);
        }

        return "redirect:/asset/my/request.do";
    }

    @RequestMapping("request-complete")
    public String requestComplete(@RequestParam("id") Long id) {
        String userId = currentUserHolder.getUserId();
        AssetRequestInfo assetRequestInfo = assetRequestInfoManager.get(id);
        assetRequestInfo.setStatus("close");
        assetRequestInfoManager.save(assetRequestInfo);

        for (AssetRequestItem assetRequestItem : assetRequestInfo
                .getAssetRequestItems()) {
            String hql = "select skuInfo from SkuInfo skuInfo where skuInfo.skuCategory.name=? and skuInfo.name=?";
            SkuInfo skuInfo = skuInfoManager.findUnique(hql,
                    assetRequestItem.getProductCategory(),
                    assetRequestItem.getProductName());
            StockInfo stockInfo = skuInfo.getStockInfos().iterator().next();
            StockItem stockItem = stockItemManager
                    .findUnique(
                            "from StockItem where stockInfo=? and userId=''",
                            stockInfo);
            stockInfo.setStoreCount(stockInfo.getStoreCount() - 1);
            stockInfo.setUsingCount(stockInfo.getUsingCount() + 1);
            stockInfoManager.save(stockInfo);
            stockItem.setUserId(userId);
            stockItemManager.save(stockItem);
        }

        return "redirect:/asset/my/index.do";
    }

    @RequestMapping("request-return")
    public String requestReturn(@RequestParam("id") Long id) {
        String userId = currentUserHolder.getUserId();
        StockItem stockItem = stockItemManager.get(id);
        stockItem.setUserId("");
        stockItemManager.save(stockItem);
        StockInfo stockInfo = stockItem.getStockInfo();
        stockInfo.setStoreCount(stockInfo.getStoreCount() + 1);
        stockInfo.setUsingCount(stockInfo.getUsingCount() - 1);
        stockInfoManager.save(stockInfo);

        return "redirect:/asset/my/index.do";
    }

    @RequestMapping("check")
    public String check(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String userId = currentUserHolder.getUserId();

        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_userId", userId));
        page.setDefaultOrder("id", Page.DESC);

        page = assetRequestManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "asset/my/check";
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
    public void setAssetRequestManager(AssetRequestManager assetRequestManager) {
        this.assetRequestManager = assetRequestManager;
    }

    @Resource
    public void setAssetRequestInfoManager(
            AssetRequestInfoManager assetRequestInfoManager) {
        this.assetRequestInfoManager = assetRequestInfoManager;
    }

    @Resource
    public void setAssetRequestItemManager(
            AssetRequestItemManager assetRequestItemManager) {
        this.assetRequestItemManager = assetRequestItemManager;
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

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

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
}
