package com.mossle.workcal.service;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import javax.xml.datatype.Duration;

import com.mossle.api.tenant.TenantConnector;
import com.mossle.api.tenant.TenantDTO;
import com.mossle.api.workcal.WorkCalendarConnector;

import com.mossle.workcal.persistence.domain.WorkcalPart;
import com.mossle.workcal.persistence.domain.WorkcalRule;
import com.mossle.workcal.persistence.manager.WorkcalPartManager;
import com.mossle.workcal.persistence.manager.WorkcalRuleManager;
import com.mossle.workcal.support.DayPart;
import com.mossle.workcal.support.Holiday;
import com.mossle.workcal.support.WorkCalendar;
import com.mossle.workcal.support.WorkDay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkCalendarService implements WorkCalendarConnector {
    private static Logger logger = LoggerFactory
            .getLogger(WorkCalendarService.class);
    public static final int STATUS_WEEK = 0;
    public static final int STATUS_HOLIDAY = 1;
    public static final int STATUS_HOLIDAY_TO_WORKDAY = 2;
    public static final int STATUS_WORKDAY_TO_HOLIDAY = 3;
    private WorkcalRuleManager workcalRuleManager;
    private WorkcalPartManager workcalPartManager;
    private String hourFormatText = "HH:mm";
    private boolean enabled = true;
    private Map<String, WorkCalendar> map = new HashMap<String, WorkCalendar>();
    private TenantConnector tenantConnector;

    public Date processDate(Date date, String tenantId) {
        WorkCalendar workCalendar = map.get(tenantId);

        return workCalendar.findWorkDate(date);
    }

    public Date add(Date date, Duration duration, String tenantId) {
        WorkCalendar workCalendar = map.get(tenantId);

        return workCalendar.add(date, duration);
    }

    public void processWeek(WorkCalendar workCalendar, String tenantId)
            throws Exception {
        List<WorkDay> days = new ArrayList<WorkDay>(8);
        days.add(new Holiday(workCalendar));
        days.add(new Holiday(workCalendar));
        days.add(new Holiday(workCalendar));
        days.add(new Holiday(workCalendar));
        days.add(new Holiday(workCalendar));
        days.add(new Holiday(workCalendar));
        days.add(new Holiday(workCalendar));
        days.add(new Holiday(workCalendar));

        // 每周的工作规则
        List<WorkcalRule> workcalRules = workcalRuleManager.find(
                "from WorkcalRule where status=? and tenantId=?", STATUS_WEEK,
                tenantId);

        for (WorkcalRule workcalRule : workcalRules) {
            WorkDay day = new WorkDay(workCalendar);
            int dayPartIndex = 0;
            List<DayPart> dayParts = day.getDayParts();

            for (WorkcalPart workcalPart : workcalPartManager.findBy(
                    "workcalRule", workcalRule)) {
                DayPart dayPart = new DayPart();
                dayPart.setWorkDay(day);
                dayPart.setIndex(dayPartIndex);

                Date startDate = new SimpleDateFormat(hourFormatText)
                        .parse(workcalPart.getStartTime());
                Date endDate = new SimpleDateFormat(hourFormatText)
                        .parse(workcalPart.getEndTime());
                Calendar startCalendar = Calendar.getInstance();
                startCalendar.setTime(startDate);

                Calendar endCalendar = Calendar.getInstance();
                endCalendar.setTime(endDate);
                dayPart.setFromHour(startCalendar.get(Calendar.HOUR));
                dayPart.setFromMinute(startCalendar.get(Calendar.MINUTE));
                dayPart.setToHour(endCalendar.get(Calendar.HOUR));
                dayPart.setToMinute(endCalendar.get(Calendar.MINUTE));
                dayParts.add(dayPart);
            }

            days.set(workcalRule.getWeek(), day);
        }
    }

    public void processHoliday(WorkCalendar workCalendar,
            WorkcalRule workcalRule) throws Exception {
        Date date = workcalRule.getWorkDate();
        Holiday holiday = new Holiday(workCalendar);
        holiday.setDate(date);
        workCalendar.addHoliday(holiday);
    }

    public void processWorkDay(WorkCalendar workCalendar,
            WorkcalRule workcalRule) throws Exception {
        Date date = workcalRule.getWorkDate();
        WorkDay workDay = new WorkDay(workCalendar);
        workDay.setDate(date);

        int dayPartIndex = 0;
        List<DayPart> dayParts = workDay.getDayParts();

        for (WorkcalPart workcalPart : workcalPartManager.findBy("workcalRule",
                workcalRule)) {
            DayPart dayPart = new DayPart();
            dayPart.setWorkDay(workDay);
            dayPart.setIndex(dayPartIndex);

            Date startDate = new SimpleDateFormat(hourFormatText)
                    .parse(workcalPart.getStartTime());
            Date endDate = new SimpleDateFormat(hourFormatText)
                    .parse(workcalPart.getEndTime());
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(startDate);

            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(endDate);
            dayPart.setFromHour(startCalendar.get(Calendar.HOUR));
            dayPart.setFromMinute(startCalendar.get(Calendar.MINUTE));
            dayPart.setToHour(endCalendar.get(Calendar.HOUR));
            dayPart.setToMinute(endCalendar.get(Calendar.MINUTE));
            dayParts.add(dayPart);
        }

        workCalendar.addWorkDay(workDay);
    }

    @PostConstruct
    public void init() throws Exception {
        if (!enabled) {
            logger.info("skip work calendar");

            return;
        }

        for (TenantDTO tenantDto : tenantConnector.findAll()) {
            String tenantId = tenantDto.getId();
            WorkCalendar workCalendar = new WorkCalendar();
            map.put(tenantId, workCalendar);
            this.processWeek(workCalendar, tenantId);

            // 特殊日期
            List<WorkcalRule> extraWorkcalRules = workcalRuleManager.find(
                    "from WorkcalRule where status<>? and tenantId=?",
                    STATUS_WEEK, tenantId);

            for (WorkcalRule workcalRule : extraWorkcalRules) {
                if (workcalRule.getStatus() == STATUS_HOLIDAY) {
                    this.processHoliday(workCalendar, workcalRule);
                } else if (workcalRule.getStatus() == STATUS_HOLIDAY_TO_WORKDAY) {
                    this.processWorkDay(workCalendar, workcalRule);
                } else {
                    this.processHoliday(workCalendar, workcalRule);
                }
            }
        }
    }

    @Resource
    public void setWorkcalRuleManager(WorkcalRuleManager workcalRuleManager) {
        this.workcalRuleManager = workcalRuleManager;
    }

    @Resource
    public void setWorkcalPartManager(WorkcalPartManager workcalPartManager) {
        this.workcalPartManager = workcalPartManager;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Resource
    public void setTenantConnector(TenantConnector tenantConnector) {
        this.tenantConnector = tenantConnector;
    }
}
