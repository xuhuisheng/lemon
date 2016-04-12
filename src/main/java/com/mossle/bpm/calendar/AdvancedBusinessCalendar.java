package com.mossle.bpm.calendar;

import java.util.Calendar;
import java.util.Date;

import javax.xml.datatype.Duration;

import com.mossle.api.workcal.WorkCalendarConnector;

import org.activiti.engine.impl.calendar.BusinessCalendar;
import org.activiti.engine.impl.util.DefaultClockImpl;
import org.activiti.engine.runtime.ClockReader;

import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 支持工作日的工作日历.
 */
public abstract class AdvancedBusinessCalendar implements BusinessCalendar {
    /** logger. */
    private static Logger logger = LoggerFactory
            .getLogger(AdvancedBusinessCalendar.class);
    private WorkCalendarConnector workCalendarConnector;
    protected ClockReader clockReader = new DefaultClockImpl();

    /**
     * 解析截止时间.
     */
    @Override
    public Date resolveDuedate(String duedateDescription) {
        return resolveDuedate(duedateDescription, -1);
    }

    /**
     * 解析截止时间.
     */
    public abstract Date resolveDuedate(String duedateDescription,
            int maxIterations);

    /**
     * 校验截止时间是否有效.
     */
    @Override
    public Boolean validateDuedate(String duedateDescription,
            int maxIterations, Date endDate, Date newTimer) {
        return (endDate == null) || endDate.after(newTimer)
                || endDate.equals(newTimer);
    }

    /**
     * 解析结束时间.
     */
    @Override
    public Date resolveEndDate(String endDateString) {
        return ISODateTimeFormat
                .dateTimeParser()
                .withZone(
                        DateTimeZone.forTimeZone(clockReader
                                .getCurrentTimeZone()))
                .parseDateTime(endDateString).toCalendar(null).getTime();
    }

    public Date processDate(Date date, boolean useBusinessTime) {
        if (!useBusinessTime) {
            return date;
        }

        // TODO: tenantId
        return workCalendarConnector.processDate(date, "1");
    }

    public Date add(Date date, Duration duration, boolean useBusinessTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        if (!useBusinessTime) {
            duration.addTo(calendar);

            return calendar.getTime();
        }

        // TODO: tenantId
        return workCalendarConnector.add(date, duration, "1");
    }

    public void setWorkCalendarConnector(
            WorkCalendarConnector workCalendarConnector) {
        this.workCalendarConnector = workCalendarConnector;
    }

    // ~ ======================================================================
    public abstract String getName();
}
