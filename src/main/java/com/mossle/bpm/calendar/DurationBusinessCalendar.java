package com.mossle.bpm.calendar;

import java.util.Date;

import org.activiti.engine.ActivitiException;

/**
 * 时间段.
 */
public class DurationBusinessCalendar extends AdvancedBusinessCalendar {
    public Date resolveDuedate(String duedate, int maxIterations) {
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
