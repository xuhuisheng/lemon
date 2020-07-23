package com.mossle.travel.service;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.model.ModelInfoDTO;

import com.mossle.core.mapper.JsonMapper;

import com.mossle.travel.persistence.domain.TravelRequest;
import com.mossle.travel.persistence.manager.TravelRequestManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

@Service
public class TravelBpmService {
    private static Logger logger = LoggerFactory
            .getLogger(TravelBpmService.class);
    private TravelRequestManager travelRequestManager;
    private JsonMapper jsonMapper = new JsonMapper();

    /** 交通工具. */
    private String vehicle;
    private String type;
    private String startCity;
    private String endCity;
    private Date startDate;
    private Date endDate;
    private Integer day;
    private String peer;

    public void save(int eventCode, ModelInfoDTO modelInfo, String userId,
            String activityId, String activityName) throws Exception {
        logger.debug("{} {} {} {}", eventCode, userId, modelInfo.getCode(),
                activityId);

        // logger.info("{}", jsonMapper.toJson(modelInfo));
        String businessKey = modelInfo.getCode();
        TravelRequest travelRequest = travelRequestManager.findUniqueBy("code",
                businessKey);

        if (travelRequest != null) {
            this.update(travelRequest, modelInfo, eventCode, activityId,
                    activityName);
        } else {
            this.insert(userId, modelInfo, eventCode, activityId, activityName);
        }
    }

    public void update(TravelRequest travelRequest, ModelInfoDTO modelInfo,
            int eventCode, String activityId, String activityName) {
        String productCategory = this.processString(modelInfo,
                "productCategory");
        String vehicle = this.processString(modelInfo, "vehicle");
        String type = this.processString(modelInfo, "type");
        String startCity = this.processString(modelInfo, "startCity");
        String endCity = this.processString(modelInfo, "endCity");
        Date startDate = this.processDate(modelInfo, "startDate");
        Date endDate = this.processDate(modelInfo, "endDate");
        Integer day = this.processInt(modelInfo, "day");
        String peer = this.processString(modelInfo, "peer");

        travelRequest.setVehicle(vehicle);
        travelRequest.setType(type);
        // TODO
        travelRequest.setStartCity(startCity);
        travelRequest.setEndCity(endCity);
        travelRequest.setStartDate(startDate);
        travelRequest.setEndDate(endDate);
        travelRequest.setDay(day);
        travelRequest.setPeer(peer);

        travelRequestManager.save(travelRequest);

        // TODO: update status
        if (eventCode == 12) {
            travelRequest.setStatus("驳回");
        } else if (eventCode == 21) {
            if (!"驳回".equals(travelRequest.getStatus())) {
                travelRequest.setStatus("草稿");
            }
        } else if (eventCode == 23) {
            travelRequest.setStatus("作废");
        } else if (eventCode == 24) {
            travelRequest.setStatus("完成");
        } else {
            travelRequest.setStatus(activityName);
        }

        travelRequestManager.save(travelRequest);
    }

    public void insert(String userId, ModelInfoDTO modelInfo, int eventCode,
            String activityId, String activityName) {
        TravelRequest travelRequest = new TravelRequest();
        travelRequest.setCode(modelInfo.getCode());
        travelRequest.setUserId(userId);
        travelRequest.setDeptCode(modelInfo.getApplicantDept());
        travelRequest.setDeptName(modelInfo.getApplicantDept());
        travelRequest.setCreateTime(modelInfo.getCreateTime());
        travelRequest.setStatus(activityId);
        travelRequest.setDescription("");
        travelRequest.setTenantId("1");
        travelRequestManager.save(travelRequest);

        this.update(travelRequest, modelInfo, eventCode, activityId,
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
    public void setTravelRequestManager(
            TravelRequestManager travelRequestManager) {
        this.travelRequestManager = travelRequestManager;
    }
}
