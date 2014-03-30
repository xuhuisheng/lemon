package com.mossle.workcal.support;

import java.util.Date;

public class DayPartResult {
    private Date date;
    private DayPart dayPart;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public DayPart getDayPart() {
        return dayPart;
    }

    public void setDayPart(DayPart dayPart) {
        this.dayPart = dayPart;
    }
}
