package com.mossle.stamp.service;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.model.ModelInfoDTO;

import com.mossle.core.mapper.JsonMapper;

import com.mossle.stamp.persistence.domain.StampRequest;
import com.mossle.stamp.persistence.manager.StampRequestManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

@Service
public class StampBpmService {
    private static Logger logger = LoggerFactory
            .getLogger(StampBpmService.class);
    private StampRequestManager stampRequestManager;
    private JsonMapper jsonMapper = new JsonMapper();

    public void save(int eventCode, ModelInfoDTO modelInfo, String userId,
            String activityId, String activityName) throws Exception {
        logger.debug("{} {} {} {}", eventCode, userId, modelInfo.getCode(),
                activityId);

        // logger.info("{}", jsonMapper.toJson(modelInfo));
        String businessKey = modelInfo.getCode();
        StampRequest stampRequest = stampRequestManager.findUniqueBy("code",
                businessKey);

        if (stampRequest != null) {
            this.update(stampRequest, modelInfo, eventCode, activityId,
                    activityName);
        } else {
            this.insert(userId, modelInfo, eventCode, activityId, activityName);
        }
    }

    public void update(StampRequest stampRequest, ModelInfoDTO modelInfo,
            int eventCode, String activityId, String activityName) {
        String stampDeptCode = this.processString(modelInfo, "stampDeptCode");
        String stampDeptName = this.processString(modelInfo, "stampDeptName");
        Date stampDate = this.processDate(modelInfo, "stampDate");
        String fileName = this.processString(modelInfo, "fileName");
        String filePath = this.processString(modelInfo, "filePath");
        Integer fileCopy = this.processInt(modelInfo, "fileCopy");
        Integer filePage = this.processInt(modelInfo, "filePage");
        String fileComment = this.processString(modelInfo, "fileComment");
        String companyCode = this.processString(modelInfo, "companyCode");
        String companyName = this.processString(modelInfo, "companyName");
        String type = this.processString(modelInfo, "type");
        String acceptUnit = this.processString(modelInfo, "acceptUnit");

        stampRequest.setStampDeptCode(stampDeptCode);
        stampRequest.setStampDeptName(stampDeptName);
        stampRequest.setStampDate(stampDate);
        stampRequest.setFileName(fileName);
        // TODO
        stampRequest.setFilePath(fileName);
        stampRequest.setFileCopy(fileCopy);
        stampRequest.setFilePage(filePage);
        stampRequest.setFileComment(fileComment);
        stampRequest.setCompanyCode(companyCode);
        stampRequest.setCompanyName(companyName);
        stampRequest.setType(type);
        stampRequest.setAcceptUnit(acceptUnit);

        stampRequestManager.save(stampRequest);

        // TODO: update status
        if (eventCode == 12) {
            stampRequest.setStatus("驳回");
        } else if (eventCode == 21) {
            if (!"驳回".equals(stampRequest.getStatus())) {
                stampRequest.setStatus("草稿");
            }
        } else if (eventCode == 23) {
            stampRequest.setStatus("作废");
        } else if (eventCode == 24) {
            stampRequest.setStatus("完成");
        } else {
            stampRequest.setStatus(activityName);
        }

        stampRequestManager.save(stampRequest);
    }

    public void insert(String userId, ModelInfoDTO modelInfo, int eventCode,
            String activityId, String activityName) {
        StampRequest stampRequest = new StampRequest();
        stampRequest.setCode(modelInfo.getCode());
        stampRequest.setUserId(userId);
        stampRequest.setDeptCode(modelInfo.getApplicantDept());
        stampRequest.setDeptName(modelInfo.getApplicantDept());
        stampRequest.setCreateTime(modelInfo.getCreateTime());
        stampRequest.setStatus(activityId);
        stampRequest.setDescription("");
        stampRequest.setTenantId("1");
        stampRequestManager.save(stampRequest);

        this.update(stampRequest, modelInfo, eventCode, activityId,
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
    public void setStampRequestManager(StampRequestManager stampRequestManager) {
        this.stampRequestManager = stampRequestManager;
    }
}
