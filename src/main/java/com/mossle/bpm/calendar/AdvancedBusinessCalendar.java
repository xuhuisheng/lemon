package com.mossle.bpm.calendar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.xml.datatype.Duration;

import com.mossle.api.workcal.WorkCalendarConnector;

import org.activiti.engine.impl.calendar.BusinessCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AdvancedBusinessCalendar implements BusinessCalendar {
    private static Logger logger = LoggerFactory
            .getLogger(AdvancedBusinessCalendar.class);
    private WorkCalendarConnector workCalendarConnector;

    public Date processDate(Date date, boolean useBusinessTime) {
        if (!useBusinessTime) {
            return date;
        }

        return workCalendarConnector.processDate(date);
    }

    public Date add(Date date, Duration duration, boolean useBusinessTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        if (!useBusinessTime) {
            duration.addTo(calendar);

            return calendar.getTime();
        }

        return workCalendarConnector.add(date, duration);
    }

    public void setWorkCalendarConnector(
            WorkCalendarConnector workCalendarConnector) {
        this.workCalendarConnector = workCalendarConnector;
    }

    // ~ ======================================================================
    public abstract String getName();
}
