package com.mossle.doc.service;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.model.ModelInfoDTO;

import com.mossle.core.mapper.JsonMapper;

import com.mossle.doc.persistence.domain.DocDispatch;
import com.mossle.doc.persistence.manager.DocDispatchManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

@Service
public class DocDispatchBpmService {
    private static Logger logger = LoggerFactory
            .getLogger(DocDispatchBpmService.class);
    private DocDispatchManager docDispatchManager;
    private JsonMapper jsonMapper = new JsonMapper();

    public void save(int eventCode, ModelInfoDTO modelInfo, String userId,
            String activityId, String activityName) throws Exception {
        logger.debug("{} {} {} {}", eventCode, userId, modelInfo.getCode(),
                activityId);

        // logger.info("{}", jsonMapper.toJson(modelInfo));
        String businessKey = modelInfo.getCode();
        DocDispatch docDispatch = docDispatchManager.findUniqueBy("code",
                businessKey);

        if (docDispatch != null) {
            this.update(docDispatch, modelInfo, eventCode, activityId,
                    activityName);
        } else {
            this.insert(userId, modelInfo, eventCode, activityId, activityName);
        }
    }

    public void update(DocDispatch docDispatch, ModelInfoDTO modelInfo,
            int eventCode, String activityId, String activityName) {
        String title = this.processString(modelInfo, "title");
        Date dispatchDate = this.processDate(modelInfo, "dispatchDate");
        String scope = this.processString(modelInfo, "scope");
        String content = this.processString(modelInfo, "content");
        String attachment = this.processString(modelInfo, "attachment");

        docDispatch.setTitle(title);
        // TODO
        docDispatch.setDispatchDate(dispatchDate);
        docDispatch.setScope(scope);
        docDispatch.setContent(content);
        docDispatch.setAttachment(attachment);

        docDispatchManager.save(docDispatch);

        // TODO: update status
        if (eventCode == 12) {
            docDispatch.setStatus("驳回");
        } else if (eventCode == 21) {
            if (!"驳回".equals(docDispatch.getStatus())) {
                docDispatch.setStatus("草稿");
            }
        } else if (eventCode == 23) {
            docDispatch.setStatus("作废");
        } else if (eventCode == 24) {
            docDispatch.setStatus("完成");
        } else {
            docDispatch.setStatus(activityName);
        }

        docDispatchManager.save(docDispatch);
    }

    public void insert(String userId, ModelInfoDTO modelInfo, int eventCode,
            String activityId, String activityName) {
        DocDispatch docDispatch = new DocDispatch();
        docDispatch.setCode(modelInfo.getCode());
        docDispatch.setUserId(userId);
        docDispatch.setDeptCode(modelInfo.getApplicantDept());
        docDispatch.setDeptName(modelInfo.getApplicantDept());
        docDispatch.setCreateTime(modelInfo.getCreateTime());
        docDispatch.setStatus(activityId);
        docDispatch.setDescription("");
        docDispatch.setTenantId("1");
        docDispatchManager.save(docDispatch);

        this.update(docDispatch, modelInfo, eventCode, activityId, activityName);
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
    public void setDocDispatchManager(DocDispatchManager docDispatchManager) {
        this.docDispatchManager = docDispatchManager;
    }
}
