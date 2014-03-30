package com.mossle.workcal.support;

import java.util.Calendar;
import java.util.Date;

public class DayPart {
    private WorkDay workDay;
    private int index;
    private int fromHour;
    private int fromMinute;
    private int toHour;
    private int toMinute;

    public Date add(Date date, long millis) {
        Date end = null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // 计算当前时间的小时和分钟
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        long dateMilliseconds = ((hour * 60L) + minute) * 60 * 1000;
        long dayPartEndMilleseconds = ((toHour * 60L) + toMinute) * 60 * 1000;

        // 计算当前dayPart的结束时间与当前时间millis的差
        long millisecondsInThisDayPart = dayPartEndMilleseconds
                - dateMilliseconds;

        if (millis <= millisecondsInThisDayPart) {
            // 如果加上millis还在当前dayPart里，直接返回end
            end = new Date(date.getTime() + millis);
        } else {
            // 超出dayPart还剩多少millis没用
            long remainderMillis = millis - millisecondsInThisDayPart;
            Date dayPartEndDate = new Date((date.getTime() + millis)
                    - remainderMillis);

            // 找到下一个工作时间段
            DayPartResult dayPartResult = workDay.findNextDayPartStart(
                    index + 1, dayPartEndDate);

            Date nextDayPartStart = dayPartResult.getDate();
            DayPart nextDayPart = dayPartResult.getDayPart();
            // 继续从下一个时间段查找
            end = nextDayPart.add(nextDayPartStart, remainderMillis);
        }

        return end;
    }

    public boolean includes(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return (((fromHour < hour) || ((fromHour == hour) && (fromMinute <= minute))) && ((hour < toHour) || ((hour == toHour) && (minute <= toMinute))));
    }

    public boolean isStartAfter(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return ((hour < fromHour) || ((hour == fromHour) && (minute <= fromMinute)));
    }

    public Date getStartTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, fromHour);
        calendar.set(Calendar.MINUTE, fromMinute);

        return calendar.getTime();
    }

    public WorkDay getWorkDay() {
        return workDay;
    }

    public void setWorkDay(WorkDay workDay) {
        this.workDay = workDay;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getFromHour() {
        return fromHour;
    }

    public void setFromHour(int fromHour) {
        this.fromHour = fromHour;
    }

    public int getFromMinute() {
        return fromMinute;
    }

    public void setFromMinute(int fromMinute) {
        this.fromMinute = fromMinute;
    }

    public int getToHour() {
        return toHour;
    }

    public void setToHour(int toHour) {
        this.toHour = toHour;
    }

    public int getToMinute() {
        return toMinute;
    }

    public void setToMinute(int toMinute) {
        this.toMinute = toMinute;
    }
}
