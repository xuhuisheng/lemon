package com.mossle.leave.service;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.model.ModelInfoDTO;

import com.mossle.core.mapper.JsonMapper;

import com.mossle.leave.persistence.domain.LeaveRequest;
import com.mossle.leave.persistence.manager.LeaveRequestManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

@Service
public class LeaveBpmService {
    private static Logger logger = LoggerFactory
            .getLogger(LeaveBpmService.class);
    private LeaveRequestManager leaveRequestManager;
    private JsonMapper jsonMapper = new JsonMapper();

    public void save(int eventCode, ModelInfoDTO modelInfo, String userId,
            String activityId, String activityName) throws Exception {
        logger.debug("{} {} {} {}", eventCode, userId, modelInfo.getCode(),
                activityId);

        // logger.info("{}", jsonMapper.toJson(modelInfo));
        String businessKey = modelInfo.getCode();
        LeaveRequest leaveRequest = leaveRequestManager.findUniqueBy("code",
                businessKey);

        if (leaveRequest != null) {
            this.update(leaveRequest, modelInfo, eventCode, activityId,
                    activityName);
        } else {
            this.insert(userId, modelInfo, eventCode, activityId, activityName);
        }
    }

    public void update(LeaveRequest leaveRequest, ModelInfoDTO modelInfo,
            int eventCode, String activityId, String activityName) {
        String type = this.processString(modelInfo, "type");
        String startDateText = this.processString(modelInfo, "startDate");
        String startTimeText = this.processString(modelInfo, "startDateTime");
        String endDateText = this.processString(modelInfo, "endDate");
        String endTimeText = this.processString(modelInfo, "endDateTime");
        Integer day = this.processInt(modelInfo, "day");
        String handOverPerson = this.processString(modelInfo, "handOverPerson");

        leaveRequest.setType(type);
        // TODO
        leaveRequest.setStartTime(this.processTime(startDateText,
                startTimeText, true));
        leaveRequest.setEndTime(this.processTime(endDateText, endTimeText,
                false));
        leaveRequest.setDay(day);
        leaveRequest.setHandOverPerson(handOverPerson);

        leaveRequestManager.save(leaveRequest);

        // TODO: update status
        if (eventCode == 12) {
            leaveRequest.setStatus("驳回");
        } else if (eventCode == 21) {
            if (!"驳回".equals(leaveRequest.getStatus())) {
                leaveRequest.setStatus("草稿");
            }
        } else if (eventCode == 23) {
            leaveRequest.setStatus("作废");
        } else if (eventCode == 24) {
            leaveRequest.setStatus("完成");
        } else {
            leaveRequest.setStatus(activityName);
        }

        leaveRequestManager.save(leaveRequest);
    }

    public void insert(String userId, ModelInfoDTO modelInfo, int eventCode,
            String activityId, String activityName) {
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setCode(modelInfo.getCode());
        leaveRequest.setUserId(userId);
        leaveRequest.setDeptCode(modelInfo.getApplicantDept());
        leaveRequest.setDeptName(modelInfo.getApplicantDept());
        leaveRequest.setCreateTime(modelInfo.getCreateTime());
        leaveRequest.setStatus(activityId);
        leaveRequest.setDescription("");
        leaveRequest.setTenantId("1");
        leaveRequestManager.save(leaveRequest);

        this.update(leaveRequest, modelInfo, eventCode, activityId,
                activityName);
    }

    public Date processTime(String dateText, String timeText,
            boolean isBeforenoon) {
        try {
            String text = dateText;

            if (isBeforenoon) {
                if ("上午".equals(timeText)) {
                    text += " 8:00";
                } else {
                    text += " 12:00";
                }
            } else {
                if ("上午".equals(timeText)) {
                    text += " 12:00";
                } else {
                    text += " 18:00";
                }
            }

            return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(text);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return null;
        }
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
    public void setLeaveRequestManager(LeaveRequestManager leaveRequestManager) {
        this.leaveRequestManager = leaveRequestManager;
    }
}
