package com.mossle.bpm.calendar;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.calendar.DurationHelper;
import org.activiti.engine.impl.util.ClockUtil;

public class DurationBusinessCalendar extends AdvancedBusinessCalendar {
    public Date resolveDuedate(String duedate) {
        try {
            DurationUtil durationUtil = new DurationUtil(duedate, this);

            return durationUtil.getDateAfter();
        } catch (Exception e) {
            throw new ActivitiException("couldn't resolve duedate: "
                    + e.getMessage(), e);
        }
    }

    public String getName() {
        return "duration";
    }
}
