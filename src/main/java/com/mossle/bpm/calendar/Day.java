package com.mossle.bpm.calendar;

import java.util.Date;

public class Day {
    private DayPart[] dayParts;
    private AdvancedBusinessCalendar businessCalendar;

    public Day(AdvancedBusinessCalendar businessCalendar) {
        this.businessCalendar = businessCalendar;
    }

    public void findNextDayPartStart(int dayPartIndex, Date date,
            Object[] result) {
        // 如果这一天有工作时间段，就从第一个慢慢查询
        if ((dayParts != null) && (dayPartIndex < dayParts.length)) {
            if (dayParts[dayPartIndex].isStartAfter(date)) {
                // 如果在时间段里，就返回
                result[0] = dayParts[dayPartIndex].getStartTime(date);
                result[1] = dayParts[dayPartIndex];
            } else {
                // 查询下一个时间段
                findNextDayPartStart(dayPartIndex + 1, date, result);
            }
        } else {
            // 如果这一天没有工作时间段，找下一个工作日
            date = businessCalendar.findStartOfNextDay(date);

            Day nextDay = businessCalendar.findDay(date);
            nextDay.findNextDayPartStart(0, date, result);
        }
    }

    public DayPart[] getDayParts() {
        return dayParts;
    }

    public void setDayParts(DayPart[] dayParts) {
        this.dayParts = dayParts;
    }
}
