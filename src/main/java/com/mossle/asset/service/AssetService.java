package com.mossle.asset.service;

import java.text.SimpleDateFormat;

import java.util.Date;

import javax.annotation.Resource;

import com.mossle.asset.persistence.domain.AssetInfo;
import com.mossle.asset.persistence.domain.AssetLend;
import com.mossle.asset.persistence.manager.AssetInfoManager;
import com.mossle.asset.persistence.manager.AssetLendManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

@Service
public class AssetService {
    private static Logger logger = LoggerFactory.getLogger(AssetService.class);
    private AssetInfoManager assetInfoManager;
    private AssetLendManager assetLendManager;

    public void makeOrder(String userId, String code, String description)
            throws Exception {
        AssetInfo assetInfo = this.assetInfoManager.get(Long.parseLong(code));

        if (!("0".equals(assetInfo.getStatus()))) {
            logger.info("{} 已出借", assetInfo.getName());

            return;
        }

        AssetLend assetLend = this.assetLendManager.findUnique(
                "from AssetLend where userId=? and assetInfo=?", userId,
                assetInfo);

        // check
        if ((assetLend != null) && (!assetLend.getUserId().equals(userId))
                && assetLend.getStatus().equals("0")) {
            logger.info("资产已被{}领用", assetLend.getUserId());

            return;
        }

        if (assetLend == null) {
            assetLend = new AssetLend();
            assetLend.setStatus("0");

            // assetLend.setTenantId("1");
        }

        if (StringUtils.isNotBlank(userId)) {
            userId = userId.replace("[", "").replace("]", "");
        }

        assetLend.setUserId(userId);
        assetLend.setAssetInfo(assetInfoManager.findUniqueBy("code", code));
        assetLend.setDescription(description);
        assetLend.setLendDate(new Date());

        assetLendManager.save(assetLend);

        assetInfo.setStatus("1");
        assetInfoManager.save(assetInfo);
    }

    @Resource
    public void setAssetInfoManager(AssetInfoManager assetInfoManager) {
        this.assetInfoManager = assetInfoManager;
    }

    @Resource
    public void setAssetLendManager(AssetLendManager assetLendManager) {
        this.assetLendManager = assetLendManager;
    }
}
