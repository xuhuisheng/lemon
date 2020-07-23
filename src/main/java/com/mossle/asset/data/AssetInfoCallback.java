package com.mossle.asset.data;

import java.util.List;

import com.mossle.asset.persistence.domain.AssetCategory;
import com.mossle.asset.persistence.domain.AssetInfo;
import com.mossle.asset.persistence.manager.AssetCategoryManager;
import com.mossle.asset.persistence.manager.AssetInfoManager;

import com.mossle.core.csv.CsvCallback;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssetInfoCallback implements CsvCallback {
    private static Logger logger = LoggerFactory
            .getLogger(AssetInfoCallback.class);
    private AssetInfoManager assetInfoManager;
    private AssetCategoryManager assetCategoryManager;
    private String defaultTenantId = "1";

    public void process(List<String> list, int lineNo) throws Exception {
        String code = list.get(0);
        String name = list.get(1);
        String sn = list.get(2);
        String model = list.get(3);
        String category = list.get(4);

        if (StringUtils.isBlank(code)) {
            logger.warn("code cannot be blank {} {}", lineNo, list);

            return;
        }

        code = code.trim().toLowerCase();

        this.createOrUpdateAssetInfo(code, name, sn, model, category, lineNo);
    }

    public void createOrUpdateAssetInfo(String code, String name, String sn,
            String model, String category, int lineNo) {
        AssetCategory topCategory = this.createOrGetTopCategory(category);
        AssetCategory subCategory = this.createOrGetSubCategory(category);
        AssetInfo assetInfo = assetInfoManager.findUniqueBy("code", code);

        if (assetInfo == null) {
            // insert
            assetInfo = new AssetInfo();
            assetInfo.setCode(code);
            assetInfo.setName(name);
            assetInfo.setSn(sn);
            assetInfo.setModel(model);
            assetInfo.setStatus("active");
            // assetInfo.setTenantId(defaultTenantId);
            assetInfo.setAssetCategoryByCategoryId(topCategory);
            assetInfo.setAssetCategoryBySubCategoryId(subCategory);
            assetInfoManager.save(assetInfo);

            return;
        }
    }

    public AssetCategory createOrGetTopCategory(String name) {
        if (name.indexOf("/") == -1) {
            return createOrGetAssetCategory(name, null);
        }

        String categoryName = name.split("/")[0];

        return createOrGetAssetCategory(categoryName, null);
    }

    public AssetCategory createOrGetSubCategory(String name) {
        if (name.indexOf("/") == -1) {
            return null;
        }

        String parentCategoryName = name.split("/")[0];
        AssetCategory parent = createOrGetAssetCategory(parentCategoryName,
                null);
        String categoryName = name.split("/")[1];

        return createOrGetAssetCategory(categoryName, parent);
    }

    public AssetCategory createOrGetAssetCategory(String name,
            AssetCategory parent) {
        AssetCategory assetCategory = null;

        if (parent == null) {
            String hql = "from AssetCategory where name=? and assetCategory.id is null";
            assetCategory = assetCategoryManager.findUnique(hql, name);
        } else {
            String hql = "from AssetCategory where name=? and assetCategory=?";
            assetCategory = assetCategoryManager.findUnique(hql, name, parent);
        }

        if (assetCategory != null) {
            return assetCategory;
        }

        assetCategory = new AssetCategory();
        assetCategory.setName(name);
        assetCategory.setAssetCategory(parent);
        assetCategoryManager.save(assetCategory);

        return assetCategory;
    }

    // ~
    public void setAssetInfoManager(AssetInfoManager assetInfoManager) {
        this.assetInfoManager = assetInfoManager;
    }

    public void setAssetCategoryManager(
            AssetCategoryManager assetCategoryManager) {
        this.assetCategoryManager = assetCategoryManager;
    }
}
