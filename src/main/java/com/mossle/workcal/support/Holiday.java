package com.mossle.workcal.support;

/** 节假日，就是一个没有dayPart的WorkDay. */
public class Holiday extends WorkDay {
    public Holiday(WorkCalendar workCalendar) {
        super(workCalendar);
    }

    public boolean isHoliday() {
        return true;
    }
}
