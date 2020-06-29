package com.mossle.asset.data;

import java.util.List;

import com.mossle.asset.persistence.domain.SkuInfo;
import com.mossle.asset.persistence.domain.StockInfo;
import com.mossle.asset.persistence.domain.StockItem;
import com.mossle.asset.persistence.manager.SkuInfoManager;
import com.mossle.asset.persistence.manager.StockInfoManager;
import com.mossle.asset.persistence.manager.StockItemManager;

import com.mossle.core.csv.CsvCallback;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StockCallback implements CsvCallback {
    private static Logger logger = LoggerFactory.getLogger(StockCallback.class);
    private StockInfoManager stockInfoManager;
    private StockItemManager stockItemManager;
    private SkuInfoManager skuInfoManager;
    private String defaultTenantId = "1";

    public void process(List<String> list, int lineNo) throws Exception {
        String code = list.get(0);
        String sn = list.get(1);
        int usingCount = Integer.parseInt(list.get(2), 10);
        String userId = list.get(3);
        String skuCode = list.get(4);

        if (StringUtils.isBlank(code)) {
            logger.warn("code cannot be blank {} {}", lineNo, list);

            return;
        }

        code = code.trim().toLowerCase();

        this.createOrUpdateStockInfo(code, sn, usingCount, userId, skuCode,
                lineNo);
    }

    public void createOrUpdateStockInfo(String code, String sn, int usingCount,
            String userId, String skuCode, int lineNo) {
        SkuInfo skuInfo = skuInfoManager.findUniqueBy("code", skuCode);
        List<StockInfo> stockInfos = stockInfoManager
                .findBy("skuInfo", skuInfo);
        StockInfo stockInfo = null;

        if (stockInfos.isEmpty()) {
            stockInfo = new StockInfo();
            stockInfo.setSkuInfo(skuInfo);
            stockInfoManager.save(stockInfo);
        } else {
            stockInfo = stockInfos.iterator().next();
        }

        // item
        StockItem stockItem = new StockItem();
        stockItem.setCode(code);
        stockItem.setSn(sn);
        stockItem.setUsingCount(usingCount);
        stockItem.setUserId(userId);
        stockItem.setStockInfo(stockInfo);
        stockItemManager.save(stockItem);

        // count
        calculateCount(stockInfo);
    }

    public void calculateCount(StockInfo stockInfo) {
        List<StockItem> stockItems = stockItemManager.findBy("stockInfo",
                stockInfo);
        int storeCount = 0;
        int usingCount = 0;

        for (StockItem stockItem : stockItems) {
            if (StringUtils.isBlank(stockItem.getUserId())) {
                storeCount += stockItem.getUsingCount();
            } else {
                usingCount += stockItem.getUsingCount();
            }
        }

        stockInfo.setStoreCount(storeCount);
        stockInfo.setUsingCount(usingCount);
        stockInfoManager.save(stockInfo);
    }

    // ~
    public void setStockInfoManager(StockInfoManager stocknfoManager) {
        this.stockInfoManager = stocknfoManager;
    }

    public void setStockItemManager(StockItemManager stockItemManager) {
        this.stockItemManager = stockItemManager;
    }

    public void setSkuInfoManager(SkuInfoManager skuInfoManager) {
        this.skuInfoManager = skuInfoManager;
    }
}
