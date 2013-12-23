package com.mossle.bpm.calendar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.xml.datatype.Duration;

import org.activiti.engine.impl.calendar.BusinessCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AdvancedBusinessCalendar implements BusinessCalendar {
    private static Logger logger = LoggerFactory
            .getLogger(AdvancedBusinessCalendar.class);
    public static final long MILLIS_OF_HOUR = 8L * 60 * 60 * 1000;
    private TimeZone timeZone = TimeZone.getDefault();
    private String hourFormatText = "HH:mm";
    private String sunday;
    private String monday;
    private String tuesday;
    private String wednesday;
    private String thursday;
    private String friday;
    private String saturday;
    private Day[] days;
    private String dayFormatText = "dd/MM/yyyy";
    private Holiday[] holidays;
    private String holiday;
    private boolean accurateToDay;

    public void init() {
        logger.info("init");

        DateFormat hourFormat = new SimpleDateFormat(hourFormatText);
        days = new Day[8];
        days[Calendar.SUNDAY] = this.parseDay("sunday", sunday, hourFormat);
        days[Calendar.MONDAY] = this.parseDay("monday", monday, hourFormat);
        days[Calendar.TUESDAY] = this.parseDay("tuesday", tuesday, hourFormat);
        days[Calendar.WEDNESDAY] = this.parseDay("wednesday", wednesday,
                hourFormat);
        days[Calendar.THURSDAY] = this.parseDay("thursday", thursday,
                hourFormat);
        days[Calendar.FRIDAY] = this.parseDay("friday", friday, hourFormat);
        days[Calendar.SATURDAY] = this.parseDay("saturday", saturday,
                hourFormat);

        DateFormat dayFormat = new SimpleDateFormat(dayFormatText);

        if (holiday != null) {
            String[] holidayElements = holiday.split("\n");
            holidays = new Holiday[holidayElements.length];

            for (int i = 0; i < holidayElements.length; i++) {
                holidays[i] = parseHoliday(holidayElements[i].trim(), dayFormat);
            }
        }
    }

    public Day parseDay(String dayText, String hours, DateFormat hourFormat) {
        Day day = new Day(this);

        if (hours == null) {
            return day;
        }

        List<DayPart> dayParts = new ArrayList<DayPart>();
        int dayPartIndex = 0;

        for (String part : hours.split("and")) {
            try {
                int separatorIndex = part.indexOf('-');

                if (separatorIndex == -1) {
                    throw new IllegalArgumentException("no dash (-)");
                }

                String fromText = part.substring(0, separatorIndex).trim()
                        .toLowerCase();
                String toText = part.substring(separatorIndex + 1).trim()
                        .toLowerCase();

                Date from = hourFormat.parse(fromText);
                Date to = hourFormat.parse(toText);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(from);

                int fromHour = calendar.get(Calendar.HOUR_OF_DAY);
                int fromMinute = calendar.get(Calendar.MINUTE);

                calendar.setTime(to);

                int toHour = calendar.get(Calendar.HOUR_OF_DAY);

                if (toHour == 0) {
                    toHour = 24;
                }

                int toMinute = calendar.get(Calendar.MINUTE);

                DayPart dayPart = new DayPart();
                dayPart.setDay(day);
                dayPart.setIndex(dayPartIndex);
                dayPart.setFromHour(fromHour);
                dayPart.setFromMinute(fromMinute);
                dayPart.setToHour(toHour);
                dayPart.setToMinute(toMinute);
                dayParts.add(dayPart);
            } catch (Exception ex) {
                throw new IllegalArgumentException(dayText
                        + " has invalid hours part '" + part + "': "
                        + ex.getMessage(), ex);
            }

            dayPartIndex++;
        }

        DayPart[] dayPartArray = new DayPart[dayParts.size()];
        dayPartArray = dayParts.toArray(dayPartArray);
        day.setDayParts(dayPartArray);

        return day;
    }

    public Holiday parseHoliday(String holidayPeriodText, DateFormat dayFormat) {
        Holiday holiday = new Holiday();
        int dashIndex = holidayPeriodText.indexOf('-');

        String fromDateText = null;
        String toDateText = null;

        if (dashIndex != -1) {
            fromDateText = holidayPeriodText.substring(0, dashIndex).trim()
                    .toLowerCase();
            toDateText = holidayPeriodText.substring(dashIndex + 1).trim()
                    .toLowerCase();
        } else {
            fromDateText = holidayPeriodText.trim().toLowerCase();
            toDateText = fromDateText;
        }

        try {
            Date fromDate = dayFormat.parse(fromDateText);
            holiday.setFromDay(fromDate);

            Date toDate = dayFormat.parse(toDateText);
            holiday.setToDay(toDate);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(holiday.getToDay());
            calendar.add(Calendar.DATE, 1);

            Date toDay = calendar.getTime();
            holiday.setToDay(toDay);
        } catch (Exception ex) {
            throw new IllegalArgumentException("couldn't parse holiday: "
                    + holidayPeriodText, ex);
        }

        return holiday;
    }

    public Date processDate(Date date, boolean useBusinessTime) {
        if (!useBusinessTime) {
            return date;
        }

        // 先找当时所处的时间段，如果找到，可以直接返回当前时间了
        DayPart dayPart = this.findDayPart(date);

        if (dayPart != null) {
            return date;
        }

        Object[] result = new Object[2];
        // 如果找不到，从当天的第一个时间段开始搜索
        this.findDay(date).findNextDayPartStart(0, date, result);
        date = (Date) result[0];

        return date;
    }

    public Date add(Date date, Duration duration, boolean useBusinessTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        if (!useBusinessTime) {
            duration.addTo(calendar);

            return calendar.getTime();
        }

        calendar.add(Calendar.YEAR, duration.getYears());
        calendar.add(Calendar.MONTH, duration.getMonths());

        long millis = 0L;

        if (accurateToDay) {
            calendar.add(Calendar.DAY_OF_MONTH, duration.getDays());
            date = calendar.getTime();
            millis = duration.getTimeInMillis(date);
            calendar.set(Calendar.DAY_OF_MONTH, -1 * duration.getDays());
            date = calendar.getTime();
            millis += (duration.getDays() * MILLIS_OF_HOUR);
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, duration.getDays());

            date = calendar.getTime();
            millis = duration.getTimeInMillis(date);
        }

        DayPart dayPart = this.findDayPart(date);
        boolean isInbusinessHours = (dayPart != null);

        if (!isInbusinessHours) {
            Object[] result = new Object[2];
            this.findDay(date).findNextDayPartStart(0, date, result);
            date = (Date) result[0];
            dayPart = (DayPart) result[1];
        }

        Date end = dayPart.add(date, millis, useBusinessTime);

        return end;
    }

    protected DayPart findDayPart(Date date) {
        if (this.isHoliday(date)) {
            return null;
        }

        Day day = this.findDay(date);
        DayPart[] dayParts = day.getDayParts();

        if (dayParts == null) {
            return null;
        }

        for (int i = 0; i < dayParts.length; i++) {
            DayPart dayPart = dayParts[i];

            if (dayPart.includes(date)) {
                return dayPart;
            }
        }

        return null;
    }

    protected Day findDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int weekDayIndex = calendar.get(Calendar.DAY_OF_WEEK);

        return this.days[weekDayIndex];
    }

    protected DayPart findNextDayPart(Date date) {
        DayPart nextDayPart = null;

        while (nextDayPart == null) {
            nextDayPart = findDayPart(date);

            if (nextDayPart == null) {
                date = findStartOfNextDay(date);

                Object[] result = new Object[2];
                Day day = findDay(date);
                day.findNextDayPartStart(0, date, result);
                nextDayPart = (DayPart) result[1];
            }
        }

        return nextDayPart;
    }

    protected Date findStartOfNextDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        date = calendar.getTime();

        while (isHoliday(date)) {
            calendar.setTime(date);
            calendar.add(Calendar.DATE, 1);
            date = calendar.getTime();
        }

        return date;
    }

    public boolean isHoliday(Date date) {
        if (holidays != null) {
            for (Holiday holiday : holidays) {
                if (holiday.includes(date)) {
                    return true;
                }
            }
        }

        return false;
    }

    // ~ ======================================================================
    public abstract String getName();

    // ~ ======================================================================
    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public String getHourFormatText() {
        return hourFormatText;
    }

    public void setHourFormatText(String hourFormatText) {
        this.hourFormatText = hourFormatText;
    }

    public void setSunday(String sunday) {
        this.sunday = sunday;
    }

    public void setMonday(String monday) {
        this.monday = monday;
    }

    public void setTuesday(String tuesday) {
        this.tuesday = tuesday;
    }

    public void setWednesday(String wednesday) {
        this.wednesday = wednesday;
    }

    public void setThursday(String thursday) {
        this.thursday = thursday;
    }

    public void setFriday(String friday) {
        this.friday = friday;
    }

    public void setSaturday(String saturday) {
        this.saturday = saturday;
    }

    public void setHoliday(String holiday) {
        this.holiday = holiday;
    }
}
