package com.mossle.asset.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.Date;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.asset.persistence.domain.AssetCategory;
import com.mossle.asset.persistence.domain.AssetInfo;
import com.mossle.asset.persistence.manager.AssetCategoryManager;
import com.mossle.asset.persistence.manager.AssetInfoManager;
import com.mossle.asset.persistence.manager.SkuCategoryManager;
import com.mossle.asset.persistence.manager.SkuInfoManager;
import com.mossle.asset.persistence.manager.StockInfoManager;
import com.mossle.asset.persistence.manager.StockItemManager;

import com.mossle.core.csv.CsvProcessor;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssetDeployer {
    private static Logger logger = LoggerFactory.getLogger(AssetDeployer.class);
    private AssetInfoManager assetInfoManager;
    private AssetCategoryManager assetCategoryManager;
    private SkuInfoManager skuInfoManager;
    private SkuCategoryManager skuCategoryManager;
    private StockInfoManager stockInfoManager;
    private StockItemManager stockItemManager;
    private String dataFilePath = "data/asset.csv";
    private String dataFileEncoding = "GB2312";
    private String skuDataFilePath = "data/sku.csv";
    private String skuDataFileEncoding = "GB2312";
    private String stockDataFilePath = "data/stock.csv";
    private String stockDataFileEncoding = "GB2312";
    private String defaultTenantId = "1";
    private boolean enable = true;

    @PostConstruct
    public void process() throws Exception {
        if (!enable) {
            logger.info("skip init asset data");

            return;
        }

        AssetInfoCallback assetInfoCallback = new AssetInfoCallback();
        assetInfoCallback.setAssetInfoManager(assetInfoManager);
        assetInfoCallback.setAssetCategoryManager(assetCategoryManager);
        new CsvProcessor().process(dataFilePath, dataFileEncoding,
                assetInfoCallback);

        SkuCallback skuCallback = new SkuCallback();
        skuCallback.setSkuInfoManager(skuInfoManager);
        skuCallback.setSkuCategoryManager(skuCategoryManager);
        new CsvProcessor().process(skuDataFilePath, skuDataFileEncoding,
                skuCallback);

        StockCallback stockCallback = new StockCallback();
        stockCallback.setStockInfoManager(stockInfoManager);
        stockCallback.setStockItemManager(stockItemManager);
        stockCallback.setSkuInfoManager(skuInfoManager);
        new CsvProcessor().process(stockDataFilePath, stockDataFileEncoding,
                stockCallback);
    }

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
    public void setSkuInfoManager(SkuInfoManager skuInfoManager) {
        this.skuInfoManager = skuInfoManager;
    }

    @Resource
    public void setSkuCategoryManager(SkuCategoryManager skuCategoryManager) {
        this.skuCategoryManager = skuCategoryManager;
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
