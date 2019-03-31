package com.mossle.vehicle.service;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.model.ModelInfoDTO;

import com.mossle.core.mapper.JsonMapper;

import com.mossle.vehicle.persistence.domain.VehicleRequest;
import com.mossle.vehicle.persistence.manager.VehicleRequestManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

@Service
public class VehicleBpmService {
    private static Logger logger = LoggerFactory
            .getLogger(VehicleBpmService.class);
    private VehicleRequestManager vehicleRequestManager;
    private JsonMapper jsonMapper = new JsonMapper();

    public void save(int eventCode, ModelInfoDTO modelInfo, String userId,
            String activityId, String activityName) throws Exception {
        logger.debug("{} {} {} {}", eventCode, userId, modelInfo.getCode(),
                activityId);

        // logger.info("{}", jsonMapper.toJson(modelInfo));
        String businessKey = modelInfo.getCode();
        VehicleRequest vehicleRequest = vehicleRequestManager.findUniqueBy(
                "code", businessKey);

        if (vehicleRequest != null) {
            this.update(vehicleRequest, modelInfo, eventCode, activityId,
                    activityName);
        } else {
            this.insert(userId, modelInfo, eventCode, activityId, activityName);
        }
    }

    public void update(VehicleRequest vehicleRequest, ModelInfoDTO modelInfo,
            int eventCode, String activityId, String activityName) {
        String vehicleName = this.processString(modelInfo, "vehicleName");
        Date startDate = this.processDate(modelInfo, "startDate");
        Date endDate = this.processDate(modelInfo, "endDate");
        String location = this.processString(modelInfo, "location");
        String entorage = this.processString(modelInfo, "entorage");

        vehicleRequest.setVehicleName(vehicleName);
        // TODO
        vehicleRequest.setStartDate(startDate);
        vehicleRequest.setEndDate(endDate);
        vehicleRequest.setLocation(location);
        vehicleRequest.setEntorage(entorage);

        vehicleRequestManager.save(vehicleRequest);

        // TODO: update status
        if (eventCode == 12) {
            vehicleRequest.setStatus("驳回");
        } else if (eventCode == 21) {
            if (!"驳回".equals(vehicleRequest.getStatus())) {
                vehicleRequest.setStatus("草稿");
            }
        } else if (eventCode == 23) {
            vehicleRequest.setStatus("作废");
        } else if (eventCode == 24) {
            vehicleRequest.setStatus("完成");
        } else {
            vehicleRequest.setStatus(activityName);
        }

        vehicleRequestManager.save(vehicleRequest);
    }

    public void insert(String userId, ModelInfoDTO modelInfo, int eventCode,
            String activityId, String activityName) {
        VehicleRequest vehicleRequest = new VehicleRequest();
        vehicleRequest.setCode(modelInfo.getCode());
        vehicleRequest.setUserId(userId);
        vehicleRequest.setDeptCode(modelInfo.getApplicantDept());
        vehicleRequest.setDeptName(modelInfo.getApplicantDept());
        vehicleRequest.setCreateTime(modelInfo.getCreateTime());
        vehicleRequest.setStatus(activityId);
        vehicleRequest.setDescription("");
        vehicleRequest.setTenantId("1");
        vehicleRequestManager.save(vehicleRequest);

        this.update(vehicleRequest, modelInfo, eventCode, activityId,
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
    public void setVehicleRequestManager(
            VehicleRequestManager vehicleRequestManager) {
        this.vehicleRequestManager = vehicleRequestManager;
    }
}
