package com.mossle.workcal.support;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkDay {
    private static Logger logger = LoggerFactory.getLogger(WorkDay.class);
    private Date date;
    private List<DayPart> dayParts = new ArrayList<DayPart>();
    private WorkCalendar workCalendar;

    public WorkDay(WorkCalendar workCalendar) {
        this.workCalendar = workCalendar;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<DayPart> getDayParts() {
        return dayParts;
    }

    public void setDayParts(List<DayPart> dayParts) {
        this.dayParts = dayParts;
    }

    public boolean isSameDay(Date workDate) {
        Calendar workCalendar = Calendar.getInstance();
        workCalendar.setTime(date);

        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTime(workDate);

        return (workCalendar.get(Calendar.YEAR) == targetCalendar
                .get(Calendar.YEAR))
                && (workCalendar.get(Calendar.MONTH) == targetCalendar
                        .get(Calendar.MONTH))
                && (workCalendar.get(Calendar.DATE) == targetCalendar
                        .get(Calendar.DATE));
    }

    public DayPartResult findNextDayPartStart(int dayPartIndex, Date date) {
        logger.info("date : {}, dayPartIndex : {}", date, dayPartIndex);
        logger.info("isHoliday : {}", this.isHoliday());
        logger.info("dayPartIndex >= dayParts.size() : {}",
                dayPartIndex >= dayParts.size());

        if (this.isHoliday() || (dayPartIndex >= dayParts.size())) {
            logger.info("workCalendar : {}", workCalendar);
            // 如果这一天没有工作时间段，找下一个工作日
            date = workCalendar.findStartOfNextDay(date);

            logger.info("findStartOfNextDay : {}", date);

            WorkDay nextDay = workCalendar.findTargetWorkDay(date);

            return nextDay.findNextDayPartStart(0, date);
        }

        // 检查是否在当前的工作时间段里
        if (dayParts.get(dayPartIndex).isStartAfter(date)) {
            // 如果在，就返回
            date = dayParts.get(dayPartIndex).getStartTime(date);

            DayPartResult dayPartResult = new DayPartResult();
            dayPartResult.setDate(date);
            dayPartResult.setDayPart(dayParts.get(dayPartIndex));

            return dayPartResult;
        } else {
            // 如果不在，查询下一个时间段
            return findNextDayPartStart(dayPartIndex + 1, date);
        }
    }

    public boolean isHoliday() {
        return (dayParts == null) || dayParts.isEmpty();
    }
}
