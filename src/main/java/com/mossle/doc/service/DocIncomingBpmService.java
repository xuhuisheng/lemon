package com.mossle.doc.service;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.model.ModelInfoDTO;

import com.mossle.core.mapper.JsonMapper;

import com.mossle.doc.persistence.domain.DocIncoming;
import com.mossle.doc.persistence.manager.DocIncomingManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

@Service
public class DocIncomingBpmService {
    private static Logger logger = LoggerFactory
            .getLogger(DocIncomingBpmService.class);
    private DocIncomingManager docIncomingManager;
    private JsonMapper jsonMapper = new JsonMapper();

    public void save(int eventCode, ModelInfoDTO modelInfo, String userId,
            String activityId, String activityName) throws Exception {
        logger.debug("{} {} {} {}", eventCode, userId, modelInfo.getCode(),
                activityId);

        // logger.info("{}", jsonMapper.toJson(modelInfo));
        String businessKey = modelInfo.getCode();
        DocIncoming docIncoming = docIncomingManager.findUniqueBy("code",
                businessKey);

        if (docIncoming != null) {
            this.update(docIncoming, modelInfo, eventCode, activityId,
                    activityName);
        } else {
            this.insert(userId, modelInfo, eventCode, activityId, activityName);
        }
    }

    public void update(DocIncoming docIncoming, ModelInfoDTO modelInfo,
            int eventCode, String activityId, String activityName) {
        String title = this.processString(modelInfo, "title");
        Date incomingDate = this.processDate(modelInfo, "incomingDate");
        String scope = this.processString(modelInfo, "scope");
        String content = this.processString(modelInfo, "content");
        String attachment = this.processString(modelInfo, "attachment");

        docIncoming.setTitle(title);
        // TODO
        docIncoming.setIncomingDate(incomingDate);
        docIncoming.setScope(scope);
        docIncoming.setContent(content);
        docIncoming.setAttachment(attachment);

        docIncomingManager.save(docIncoming);

        // TODO: update status
        if (eventCode == 12) {
            docIncoming.setStatus("驳回");
        } else if (eventCode == 21) {
            if (!"驳回".equals(docIncoming.getStatus())) {
                docIncoming.setStatus("草稿");
            }
        } else if (eventCode == 23) {
            docIncoming.setStatus("作废");
        } else if (eventCode == 24) {
            docIncoming.setStatus("完成");
        } else {
            docIncoming.setStatus(activityName);
        }

        docIncomingManager.save(docIncoming);
    }

    public void insert(String userId, ModelInfoDTO modelInfo, int eventCode,
            String activityId, String activityName) {
        DocIncoming docIncoming = new DocIncoming();
        docIncoming.setCode(modelInfo.getCode());
        docIncoming.setUserId(userId);
        docIncoming.setDeptCode(modelInfo.getApplicantDept());
        docIncoming.setDeptName(modelInfo.getApplicantDept());
        docIncoming.setCreateTime(modelInfo.getCreateTime());
        docIncoming.setStatus(activityId);
        docIncoming.setDescription("");
        docIncoming.setTenantId("1");
        docIncomingManager.save(docIncoming);

        this.update(docIncoming, modelInfo, eventCode, activityId, activityName);
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
    public void setDocIncomingManager(DocIncomingManager docIncomingManager) {
        this.docIncomingManager = docIncomingManager;
    }
}
