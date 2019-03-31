package com.mossle.asset.service;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.model.ModelInfoDTO;

import com.mossle.asset.persistence.domain.AssetRequest;
import com.mossle.asset.persistence.manager.AssetRequestManager;

import com.mossle.core.mapper.JsonMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

@Service
public class AssetBpmService {
    private static Logger logger = LoggerFactory
            .getLogger(AssetBpmService.class);
    private AssetRequestManager assetRequestManager;
    private JsonMapper jsonMapper = new JsonMapper();

    public void save(int eventCode, ModelInfoDTO modelInfo, String userId,
            String activityId, String activityName) throws Exception {
        logger.debug("{} {} {} {}", eventCode, userId, modelInfo.getCode(),
                activityId);

        // logger.info("{}", jsonMapper.toJson(modelInfo));
        String businessKey = modelInfo.getCode();
        AssetRequest assetRequest = assetRequestManager.findUniqueBy("code",
                businessKey);

        if (assetRequest != null) {
            this.update(assetRequest, modelInfo, eventCode, activityId,
                    activityName);
        } else {
            this.insert(userId, modelInfo, eventCode, activityId, activityName);
        }
    }

    public void update(AssetRequest assetRequest, ModelInfoDTO modelInfo,
            int eventCode, String activityId, String activityName) {
        String productCategory = this.processString(modelInfo,
                "productCategory");
        String purpose = this.processString(modelInfo, "purpose");
        String productName = this.processString(modelInfo, "productName");
        String productModel = this.processString(modelInfo, "productModel");
        Integer productPrice = this.processInt(modelInfo, "productPrice");
        Integer productNum = this.processInt(modelInfo, "productNum");
        assetRequest.setProductCategory(productCategory);
        assetRequest.setPurpose(purpose);
        // TODO
        assetRequest.setProductName(productName);
        assetRequest.setProductModel(productModel);
        assetRequest.setProductPrice(productPrice);
        assetRequest.setProductNum(productNum);

        assetRequestManager.save(assetRequest);

        // TODO: update status
        if (eventCode == 12) {
            assetRequest.setStatus("驳回");
        } else if (eventCode == 21) {
            if (!"驳回".equals(assetRequest.getStatus())) {
                assetRequest.setStatus("草稿");
            }
        } else if (eventCode == 23) {
            assetRequest.setStatus("作废");
        } else if (eventCode == 24) {
            assetRequest.setStatus("完成");
        } else {
            assetRequest.setStatus(activityName);
        }

        assetRequestManager.save(assetRequest);
    }

    public void insert(String userId, ModelInfoDTO modelInfo, int eventCode,
            String activityId, String activityName) {
        AssetRequest assetRequest = new AssetRequest();
        assetRequest.setCode(modelInfo.getCode());
        assetRequest.setUserId(userId);
        assetRequest.setDeptCode(modelInfo.getApplicantDept());
        assetRequest.setDeptName(modelInfo.getApplicantDept());
        assetRequest.setCreateTime(modelInfo.getCreateTime());
        assetRequest.setStatus(activityId);
        assetRequest.setDescription("");
        assetRequest.setTenantId("1");
        assetRequestManager.save(assetRequest);

        this.update(assetRequest, modelInfo, eventCode, activityId,
                activityName);
    }

    public String processString(ModelInfoDTO modelInfo, String name) {
        Object value = modelInfo.findItemValue(name);

        if (value == null) {
            return "";
        }

        if (value instanceof String) {
            return (String) value;
        }

        return value.toString();
    }

    public Integer processInt(ModelInfoDTO modelInfo, String name) {
        Object value = modelInfo.findItemValue(name);

        if (value == null) {
            return null;
        }

        if (value instanceof Integer) {
            return (Integer) value;
        }

        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        if ("".equals(value)) {
            return null;
        }

        if (value instanceof String) {
            String text = (String) value;

            return Integer.parseInt(text);
        }

        return null;
    }

    public Date processDate(ModelInfoDTO modelInfo, String name) {
        try {
            Object value = modelInfo.findItemValue(name);

            if (value == null) {
                return null;
            }

            if (value instanceof Date) {
                return (Date) value;
            }

            if (value instanceof String) {
                String text = (String) value;

                return new SimpleDateFormat("yyyy-MM-dd").parse(text);
            }

            return new SimpleDateFormat("yyyy-MM-dd").parse(value.toString());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return null;
        }
    }

    @Resource
    public void setAssetRequestManager(AssetRequestManager assetRequestManager) {
        this.assetRequestManager = assetRequestManager;
    }
}
