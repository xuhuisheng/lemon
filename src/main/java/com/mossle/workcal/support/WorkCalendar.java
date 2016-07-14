package com.mossle.workcal.support;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

/**
 * 工作日历.
 */
public class WorkCalendar {
    /** 1分钟60秒. */
    public static final long MILLIS_OF_MINUTE = 1000L * 60;

    /** 1小时60分钟. */
    public static final long MILLIS_OF_HOUR = 60 * MILLIS_OF_MINUTE;

    /** 1天8小时. */
    public static final long HOUR_OF_DAY = 8;
    private List<WorkDay> days = new ArrayList<WorkDay>();
    private List<Holiday> holidays = new ArrayList<Holiday>();
    private List<WorkDay> workDays = new ArrayList<WorkDay>();
    private DatatypeFactory datatypeFactory;
    private boolean accurateToDay;

    /**
     * construtor.
     */
    public WorkCalendar() throws Exception {
        datatypeFactory = DatatypeFactory.newInstance();
    }

    /**
     * 计算结束时间.
     */
    public Date add(Date date, String period) throws Exception {
        return this.add(date, this.parsePeriod(period));
    }

    /**
     * 计算结束时间.
     */
    public Date add(Date startDate, Duration duration) {
        // 得到对应的时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        // 添加年数和月数，工作日方面年和月的概念不会改变
        calendar.add(Calendar.YEAR, duration.getYears());
        calendar.add(Calendar.MONTH, duration.getMonths());

        // 天数，小时，分钟可能因为工作日有概念，所以特殊处理
        int day = duration.getDays();
        int hour = duration.getHours();
        int minute = duration.getMinutes();

        if (accurateToDay) {
            // 有时需要自动把一天换算成8个小时，以实际计算工时
            hour += (day * HOUR_OF_DAY);
            day = 0;
        } else {
            Date workDate = this.findWorkDate(calendar.getTime());
            calendar.setTime(workDate);

            // 目前还没有更好的算法，所以对天数累加，再判断是否工作日
            for (int i = 0; i < day; i++) {
                calendar.add(Calendar.DATE, 1);

                int originHour = calendar.get(Calendar.HOUR_OF_DAY);
                int originMinute = calendar.get(Calendar.MINUTE);
                // 如果当前就是工作日，就返回当前时间
                // 如果当前的时间已经不是工作日了就返回最近的工作日
                workDate = this.findWorkDate(calendar.getTime());
                calendar.setTime(workDate);
                calendar.set(Calendar.HOUR_OF_DAY, originHour);
                calendar.set(Calendar.MINUTE, originMinute);
            }
        }

        Date targetDate = calendar.getTime();
        long millis = (hour * MILLIS_OF_HOUR) + (minute * MILLIS_OF_MINUTE);
        DayPart dayPart = this.findDayPart(targetDate);
        boolean isInbusinessHours = (dayPart != null);

        if (!isInbusinessHours) {
            DayPartResult dayPartResult = this.findTargetWorkDay(targetDate)
                    .findNextDayPartStart(0, targetDate);
            targetDate = dayPartResult.getDate();
            dayPart = dayPartResult.getDayPart();
        }

        Date end = dayPart.add(targetDate, millis);

        return end;
    }

    /**
     * 把开始时间转换成工作时间，比如当前时间是假期，就要从最近的工作日开始计算.
     */
    public Date findWorkDate(Date date) {
        // 先找当时所处的时间段，如果找到，可以直接返回当前时间了
        DayPart dayPart = this.findDayPart(date);

        if (dayPart != null) {
            return date;
        }

        // 如果找不到，从当天的第一个时间段开始搜索
        DayPartResult dayPartResult = this.findTargetWorkDay(date)
                .findNextDayPartStart(0, date);

        // Object[] result = new Object[2];
        // this.findDay(date).findNextDayPartStart(0, date, result);
        // date = (Date) result[0];
        // return date;
        return dayPartResult.getDate();
    }

    /**
     * 返回第二天.
     */
    public Date findStartOfNextDay(Date date) {
        Calendar calendar = this.cleanTime(date);

        return calendar.getTime();
    }

    /**
     * 找到当天的时间段设置.
     */
    public WorkDay findDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int weekDayIndex = calendar.get(Calendar.DAY_OF_WEEK);

        return this.days.get(weekDayIndex);
    }

    /**
     * 日期增加一天，清空时间属性.
     */
    public Calendar cleanTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar;
    }

    /**
     * 获取时间段配置.
     */
    public DayPart findDayPart(Date date) {
        if (this.isWorkDay(date)) {
            DayPart dayPart = this.findWorkDayPart(date);

            if (dayPart != null) {
                return dayPart;
            }
        }

        if (this.isHoliday(date)) {
            return null;
        }

        WorkDay day = this.findDay(date);
        List<DayPart> dayParts = day.getDayParts();

        if (dayParts == null) {
            return null;
        }

        for (int i = 0; i < dayParts.size(); i++) {
            DayPart dayPart = dayParts.get(i);

            if (dayPart.includes(date)) {
                return dayPart;
            }
        }

        return null;
    }

    // ~ ==================================================
    public boolean isHoliday(Calendar calendar) {
        return this.isHoliday(calendar.getTime());
    }

    public boolean isHoliday(Date date) {
        if (holidays != null) {
            for (WorkDay holiday : holidays) {
                if (holiday.isSameDay(date)) {
                    return true;
                }
            }
        }

        return false;
    }

    // ~ ==================================================
    public boolean isWorkDay(Calendar calendar) {
        return this.isWorkDay(calendar.getTime());
    }

    public boolean isWorkDay(Date date) {
        return findWorkDayPart(date) != null;
    }

    public WorkDay findWorkDay(Date date) {
        for (WorkDay workDay : workDays) {
            if (workDay.isSameDay(date)) {
                return workDay;
            }
        }

        return null;
    }

    public DayPart findWorkDayPart(Date date) {
        WorkDay workDay = findWorkDay(date);

        if (workDay == null) {
            return null;
        }

        for (DayPart dayPart : workDay.getDayParts()) {
            if (dayPart.includes(date)) {
                return dayPart;
            }
        }

        return null;
    }

    public WorkDay findTargetWorkDay(Date date) {
        WorkDay workDay = this.findWorkDay(date);

        if (workDay != null) {
            return workDay;
        }

        if (this.isHoliday(date)) {
            Holiday holiday = new Holiday(this);
            holiday.setDate(date);

            return holiday;
        }

        return this.findDay(date);
    }

    // ~ ==================================================
    private Duration parsePeriod(String period) throws Exception {
        return datatypeFactory.newDuration(period);
    }

    // ~ ==================================================
    public void setDays(List<WorkDay> days) {
        this.days = days;
    }

    public void setHolidays(List<Holiday> holidays) {
        this.holidays = holidays;
    }

    public void setWorkDays(List<WorkDay> workDays) {
        this.workDays = workDays;
    }

    public void addHoliday(Holiday holiday) {
        this.holidays.add(holiday);
    }

    public void addWorkDay(WorkDay workDay) {
        this.workDays.add(workDay);
    }
}
