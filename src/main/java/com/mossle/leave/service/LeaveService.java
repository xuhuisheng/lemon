package com.mossle.leave.service;

import java.text.SimpleDateFormat;

import java.util.Date;

import javax.annotation.Resource;

import com.mossle.leave.persistence.domain.LeaveInfo;
import com.mossle.leave.persistence.manager.LeaveInfoManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

@Service
public class LeaveService {
    private static Logger logger = LoggerFactory.getLogger(LeaveService.class);
    private LeaveInfoManager leaveInfoManager;

    public void updateProcess(String code, String userId, String type,
            String description, String startTime, String endTime, String status)
            throws Exception {
        LeaveInfo leaveInfo = leaveInfoManager.findUniqueBy("code", code);

        if (leaveInfo != null) {
            logger.info("already create : {}", code);

            return;
        }

        leaveInfo = new LeaveInfo();
        leaveInfo.setCode(code);
        leaveInfo.setEmployeeId(userId);
        leaveInfo.setType(type);
        leaveInfo.setStartTime(new SimpleDateFormat("yyyy-MM-dd")
                .parse(startTime));
        leaveInfo.setEndTime(new SimpleDateFormat("yyyy-MM-dd").parse(endTime));
        leaveInfo.setCreateTime(new Date());
        leaveInfo.setStatus(status);
        leaveInfo.setName(description);
        leaveInfo.setTenantId("1");
        leaveInfoManager.save(leaveInfo);
    }

    @Resource
    public void setLeaveInfoManager(LeaveInfoManager leaveInfoManager) {
        this.leaveInfoManager = leaveInfoManager;
    }
}
