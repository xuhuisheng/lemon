package com.mossle.bpm.calendar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.xml.datatype.Duration;

import com.mossle.workcal.service.WorkCalendarService;

import org.activiti.engine.impl.calendar.BusinessCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AdvancedBusinessCalendar implements BusinessCalendar {
    private static Logger logger = LoggerFactory
            .getLogger(AdvancedBusinessCalendar.class);
    private WorkCalendarService workCalendarService;

    public Date processDate(Date date, boolean useBusinessTime) {
        if (!useBusinessTime) {
            return date;
        }

        return workCalendarService.processDate(date);
    }

    public Date add(Date date, Duration duration, boolean useBusinessTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        if (!useBusinessTime) {
            duration.addTo(calendar);

            return calendar.getTime();
        }

        return workCalendarService.add(date, duration);
    }

    public void setWorkCalendarService(WorkCalendarService workCalendarService) {
        this.workCalendarService = workCalendarService;
    }

    // ~ ======================================================================
    public abstract String getName();
}
