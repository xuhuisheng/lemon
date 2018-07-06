package com.mossle.attendance.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.attendance.support.AttendanceDTO;
import com.mossle.attendance.persistence.domain.AttendanceInfo;
import com.mossle.attendance.persistence.domain.AttendanceRule;
import com.mossle.attendance.persistence.manager.AttendanceInfoManager;
import com.mossle.attendance.persistence.manager.AttendanceRuleManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class AttendanceService {
    private static Logger logger = LoggerFactory
            .getLogger(AttendanceService.class);
    private AttendanceInfoManager attendanceInfoManager;
    private AttendanceRuleManager attendanceRuleManager;

    public void saveRecord(String userId, String tenantId) {
        Date now = new Date();

        String hql = "from AttendanceInfo where userId=? and createTime between ? and ? order by createTime asc";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_YEAR);
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_YEAR, day);

        Date startTime = calendar.getTime();
        calendar.set(Calendar.HOUR, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        Date endTime = calendar.getTime();

        // 查询当天所有的打卡记录
        List<AttendanceInfo> attendanceInfos = this.attendanceInfoManager.find(
                hql, userId, startTime, endTime);

        if (attendanceInfos.isEmpty()) {
            // 还没有打卡，第一次打卡算作上班时间
            this.addRecordUp(userId, now, tenantId);
        } else if (attendanceInfos.size() == 1) {
            // 已打卡一次，第二次打卡算做下班时间
            this.addRecordDown(userId, now, tenantId);
        } else {
            // 超过两次打卡，认为是更新下班时间
            this.updateRecordDown(attendanceInfos.get(1), now);
        }
    }

    public void addRecordUp(String userId, Date now, String tenantId) {
        logger.info("add record up : {} {}", userId, now);

        AttendanceInfo attendanceInfo = new AttendanceInfo();
        attendanceInfo.setType("up");
        attendanceInfo.setUserId(userId);
        attendanceInfo.setCreateTime(now);
        attendanceInfo.setTenantId(tenantId);

        // 是否比上班时间早
        if (this.isBefore(now)) {
            attendanceInfo.setStatus("normal");
        } else {
            attendanceInfo.setStatus("late");
        }

        attendanceInfoManager.save(attendanceInfo);
    }

    public void addRecordDown(String userId, Date now, String tenantId) {
        AttendanceInfo attendanceInfo = new AttendanceInfo();
        attendanceInfo.setType("down");
        attendanceInfo.setUserId(userId);
        attendanceInfo.setCreateTime(now);
        attendanceInfo.setTenantId(tenantId);

        // 是否比上班时间早
        if (this.isAfter(now)) {
            attendanceInfo.setStatus("normal");
        } else {
            attendanceInfo.setStatus("early");
        }

        attendanceInfoManager.save(attendanceInfo);
    }

    public void updateRecordDown(AttendanceInfo attendanceInfo, Date now) {
        attendanceInfo.setCreateTime(now);

        // 是否比上班时间早
        if (this.isAfter(now)) {
            attendanceInfo.setStatus("normal");
        } else {
            attendanceInfo.setStatus("early");
        }

        attendanceInfoManager.save(attendanceInfo);
    }

    public AttendanceRule findAttendanceRule() {
        String hql = "from AttendanceRule";
        AttendanceRule attendanceRule = attendanceRuleManager.findUnique(hql);

        if (attendanceRule != null) {
            return attendanceRule;
        }

        attendanceRule = new AttendanceRule();
        attendanceRule.setStartHour(9);
        attendanceRule.setStartMinute(0);
        attendanceRule.setEndHour(18);
        attendanceRule.setEndMinute(0);
        attendanceRule.setStartOffset(0);
        attendanceRule.setEndOffset(0);

        return attendanceRule;
    }

    public boolean isBefore(Date now) {
        AttendanceRule attendanceRule = this.findAttendanceRule();

        int hourOffset = now.getHours() - attendanceRule.getStartHour();
        int minuteOffset = ((hourOffset * 60) + now.getMinutes())
                - attendanceRule.getStartMinute();

        return minuteOffset < attendanceRule.getStartOffset();
    }

    public boolean isAfter(Date now) {
        AttendanceRule attendanceRule = this.findAttendanceRule();

        int hourOffset = attendanceRule.getEndHour() - now.getHours();
        int minuteOffset = ((hourOffset * 60) + attendanceRule.getEndMinute())
                - now.getMinutes();

        return minuteOffset < attendanceRule.getEndOffset();
    }

	public AttendanceDTO findAttendanceStatus(String userId) {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int date = calendar.get(Calendar.DAY_OF_YEAR);
		calendar.clear();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.DAY_OF_YEAR, date);
		Date startTime = calendar.getTime();
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		Date endTime = calendar.getTime();
		String hql = "from AttendanceInfo where userId=? and createTime between ? and ? order by createTime";
		List<AttendanceInfo> attendanceInfos = this.attendanceInfoManager.find(hql, userId, startTime, endTime);
		AttendanceDTO attendanceDto = new AttendanceDTO();
		attendanceDto.setUserId(userId);
		if (attendanceInfos.isEmpty()) {
			return attendanceDto;
		} else if (attendanceInfos.size() == 1) {
			AttendanceInfo upAttendanceInfo = attendanceInfos.get(0);
			attendanceDto.setUpTime(upAttendanceInfo.getCreateTime());
			attendanceDto.setUpStatus(upAttendanceInfo.getStatus());
		} else {
			AttendanceInfo upAttendanceInfo = attendanceInfos.get(0);
			attendanceDto.setUpTime(upAttendanceInfo.getCreateTime());
			attendanceDto.setUpStatus(upAttendanceInfo.getStatus());
			AttendanceInfo downAttendanceInfo = attendanceInfos.get(attendanceInfos.size() - 1);
			attendanceDto.setDownTime(downAttendanceInfo.getCreateTime());
			attendanceDto.setDownStatus(downAttendanceInfo.getStatus());
		}
		return attendanceDto;
	}

	// ~
    @Resource
    public void setAttendanceInfoManager(
            AttendanceInfoManager attendanceInfoManager) {
        this.attendanceInfoManager = attendanceInfoManager;
    }

    @Resource
    public void setAttendanceRuleManger(
            AttendanceRuleManager attendanceRuleManager) {
        this.attendanceRuleManager = attendanceRuleManager;
    }
}
