package com.mossle.asset.data;

import java.util.List;

import com.mossle.asset.persistence.domain.SkuCategory;
import com.mossle.asset.persistence.domain.SkuInfo;
import com.mossle.asset.persistence.manager.SkuCategoryManager;
import com.mossle.asset.persistence.manager.SkuInfoManager;

import com.mossle.core.csv.CsvCallback;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkuCallback implements CsvCallback {
    private static Logger logger = LoggerFactory.getLogger(SkuCallback.class);
    private SkuInfoManager skuInfoManager;
    private SkuCategoryManager skuCategoryManager;
    private String defaultTenantId = "1";

    public void process(List<String> list, int lineNo) throws Exception {
        String code = list.get(0);
        String name = list.get(1);
        String model = list.get(2);
        String category = list.get(3);

        if (StringUtils.isBlank(code)) {
            logger.warn("code cannot be blank {} {}", lineNo, list);

            return;
        }

        code = code.trim().toLowerCase();

        this.createOrUpdateSkuInfo(code, name, model, category, lineNo);
    }

    public void createOrUpdateSkuInfo(String code, String name, String model,
            String category, int lineNo) {
        SkuCategory skuCategory = this.createOrGetSkuCategory(category, null);
        SkuInfo skuInfo = skuInfoManager.findUniqueBy("code", code);

        if (skuInfo == null) {
            // insert
            skuInfo = new SkuInfo();
            skuInfo.setCode(code);
            skuInfo.setName(name);
            // skuInfo.setSn(sn);
            skuInfo.setModel(model);
            // skuInfo.setStatus("active");
            // skuInfo.setTenantId(defaultTenantId);
            // skuInfo.setSkuCategoryByCategoryId(topCategory);
            // skuInfo.setSkuCategoryBySubCategoryId(subCategory);
            skuInfo.setSkuCategory(skuCategory);
            skuInfoManager.save(skuInfo);

            return;
        }
    }

    public SkuCategory createOrGetSkuCategory(String name, SkuCategory parent) {
        SkuCategory skuCategory = null;

        if (parent == null) {
            String hql = "from SkuCategory where code=? and skuCategory.id is null";
            skuCategory = skuCategoryManager.findUnique(hql, name);
        } else {
            String hql = "from SkuCategory where code=? and skuCategory=?";
            skuCategory = skuCategoryManager.findUnique(hql, name, parent);
        }

        if (skuCategory != null) {
            return skuCategory;
        }

        skuCategory = new SkuCategory();
        skuCategory.setCode(name);
        skuCategory.setName(name);
        skuCategory.setSkuCategory(parent);
        skuCategoryManager.save(skuCategory);

        return skuCategory;
    }

    // ~
    public void setSkuInfoManager(SkuInfoManager skuInfoManager) {
        this.skuInfoManager = skuInfoManager;
    }

    public void setSkuCategoryManager(SkuCategoryManager skuCategoryManager) {
        this.skuCategoryManager = skuCategoryManager;
    }
}
