package com.mossle.bpm.calendar;

import java.util.Date;

import org.activiti.engine.ActivitiException;

/**
 * 截止日期.
 */
public class DueDateBusinessCalendar extends AdvancedBusinessCalendar {
    public Date resolveDuedate(String duedate, int maxIterations) {
        try {
            return new DurationUtil(duedate, this).getDateAfter();
        } catch (Exception e) {
            throw new ActivitiException("couldn't resolve duedate: "
                    + e.getMessage(), e);
        }
    }

    public String getName() {
        return "dueDate";
    }
}
