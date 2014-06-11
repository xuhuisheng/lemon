package com.mossle.workcal.service;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import javax.xml.datatype.Duration;

import com.mossle.core.mapper.JsonMapper;

import com.mossle.workcal.domain.WorkcalPart;
import com.mossle.workcal.domain.WorkcalRule;
import com.mossle.workcal.manager.WorkcalPartManager;
import com.mossle.workcal.manager.WorkcalRuleManager;
import com.mossle.workcal.support.*;
import com.mossle.workcal.support.WorkCalendar;
import com.mossle.api.workcal.WorkCalendarConnector;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class WorkCalendarService implements WorkCalendarConnector {
    public static final int STATUS_WEEK = 0;
    public static final int STATUS_HOLIDAY = 1;
    public static final int STATUS_HOLIDAY_TO_WORKDAY = 2;
    public static final int STATUS_WORKDAY_TO_HOLIDAY = 3;
    private WorkCalendar workCalendar;
    private WorkcalRuleManager workcalRuleManager;
    private WorkcalPartManager workcalPartManager;
    private String hourFormatText = "HH:mm";

    public Date processDate(Date date) {
        return workCalendar.findWorkDate(date);
    }

    public Date add(Date date, Duration duration) {
        return workCalendar.add(date, duration);
    }

    public void processWeek() throws Exception {
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
        List<WorkcalRule> workcalRules = workcalRuleManager.findBy("status",
                STATUS_WEEK);

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

    public void processHoliday(WorkcalRule workcalRule) throws Exception {
        Date date = workcalRule.getWorkDate();
        Holiday holiday = new Holiday(workCalendar);
        holiday.setDate(date);
        workCalendar.addHoliday(holiday);
    }

    public void processWorkDay(WorkcalRule workcalRule) throws Exception {
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
        workCalendar = new WorkCalendar();
        this.processWeek();

        // 特殊日期
        List<WorkcalRule> extraWorkcalRules = workcalRuleManager.find(
                "from WorkcalRule where status<>?", STATUS_WEEK);

        for (WorkcalRule workcalRule : extraWorkcalRules) {
            if (workcalRule.getStatus() == STATUS_HOLIDAY) {
                this.processHoliday(workcalRule);
            } else if (workcalRule.getStatus() == STATUS_HOLIDAY_TO_WORKDAY) {
                this.processWorkDay(workcalRule);
            } else {
                this.processHoliday(workcalRule);
                
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
}
