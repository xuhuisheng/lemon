package com.mossle.vehicle.service;

import java.text.SimpleDateFormat;

import java.util.Date;

import javax.annotation.Resource;

import com.mossle.vehicle.persistence.domain.VehicleInfo;
import com.mossle.vehicle.persistence.domain.VehicleTask;
import com.mossle.vehicle.persistence.manager.VehicleInfoManager;
import com.mossle.vehicle.persistence.manager.VehicleTaskManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

@Service
public class VehicleService {
    private static Logger logger = LoggerFactory
            .getLogger(VehicleService.class);
    private VehicleTaskManager vehicleTaskManager;
    private VehicleInfoManager vehicleInfoManager;

    public boolean isValidTime(VehicleTask vehicleTask, VehicleInfo vehicleInfo) {
        Long id = vehicleTask.getId();
        boolean isNew = id == null;
        String hql = "select count(*) from VehicleTask where vehicleInfo=? and startTime<? and endTime>?";

        if (isNew) {
            return vehicleTaskManager.getCount(hql, vehicleInfo,
                    vehicleTask.getEndTime(), vehicleTask.getStartTime()) == 0;
        } else {
            hql += " and id!=?";

            return vehicleTaskManager.getCount(hql, vehicleInfo,
                    vehicleTask.getEndTime(), vehicleTask.getStartTime(), id) == 0;
        }
    }

    public void makeOrder(String userId, String code, String startTime,
            String endTime, String location) throws Exception {
        VehicleInfo vehicleInfo = this.vehicleInfoManager.get(Long
                .parseLong(code));

        if (vehicleInfo.getStatus() != 0) {
            logger.info("{} 已借出", vehicleInfo.getCode());

            return;
        }

        VehicleTask vehicleTask = this.vehicleTaskManager.findUnique(
                "from VehicleTask where userId=? and vehicleInfo=?", userId,
                vehicleInfo);

        if (vehicleTask == null) {
            vehicleTask = new VehicleTask();
            vehicleTask.setStatus(0);
            vehicleTask.setTenantId("1");
        }

        if (StringUtils.isNotBlank(userId)) {
            userId = userId.replace("[", "").replace("]", "");
        }

        vehicleTask.setUserId(userId);
        vehicleTask.setVehicleInfo(vehicleInfo);
        vehicleTask.setStartTime(new SimpleDateFormat("yyyy-MM-dd")
                .parse(startTime));
        vehicleTask.setEndTime(new SimpleDateFormat("yyyy-MM-dd")
                .parse(endTime));
        vehicleTask.setLocation(location);

        if (this.isValidTime(vehicleTask, vehicleTask.getVehicleInfo())) {
            vehicleTaskManager.save(vehicleTask);

            vehicleInfo.setStatus(1);
            vehicleInfoManager.save(vehicleInfo);
        } else {
            logger.info("车辆在当前时间段已被占用");
        }
    }

    public void cancelOrder(Long id) {
        vehicleTaskManager.removeById(id);
    }

    public void finishOrder(String userId, String code) {
        VehicleInfo vehicleInfo = this.vehicleInfoManager.get(Long
                .parseLong(code));

        VehicleTask vehicleTask = this.vehicleTaskManager.findUnique(
                "from VehicleTask where userId=? and vehicleInfo=?", userId,
                vehicleInfo);
        vehicleTask.setStatus(1);
        vehicleTaskManager.save(vehicleTask);

        vehicleInfo.setStatus(1);
        vehicleInfoManager.save(vehicleInfo);
    }

    @Resource
    public void setVehicleTaskManager(VehicleTaskManager vehicleTaskManager) {
        this.vehicleTaskManager = vehicleTaskManager;
    }

    @Resource
    public void setVehicleInfoManager(VehicleInfoManager vehicleInfoManager) {
        this.vehicleInfoManager = vehicleInfoManager;
    }
}
