package com.mossle.bpm.calendar;

import java.util.Date;

public class Holiday {
    private Date fromDay;
    private Date toDay;

    public boolean includes(Date date) {
        return ((fromDay.getTime() <= date.getTime()) && (date.getTime() < toDay
                .getTime()));
    }

    public Date getFromDay() {
        return fromDay;
    }

    public void setFromDay(Date fromDay) {
        this.fromDay = fromDay;
    }

    public Date getToDay() {
        return toDay;
    }

    public void setToDay(Date toDay) {
        this.toDay = toDay;
    }
}
