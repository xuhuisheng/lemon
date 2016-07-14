package com.mossle.bpm.calendar;

import java.util.Date;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.calendar.CronExpression;

/**
 * 循环.
 */
public class CycleBusinessCalendar extends AdvancedBusinessCalendar {
    public Date resolveDuedate(String duedate, int maxIterations) {
        String textWithoutBusiness = duedate;
        boolean isBusinessTime = textWithoutBusiness.startsWith("business");

        if (isBusinessTime) {
            textWithoutBusiness = textWithoutBusiness.substring(
                    "business".length()).trim();
        }

        try {
            if (textWithoutBusiness.startsWith("R")) {
                return new DurationUtil(duedate, this).getDateAfter();
            } else {
                CronExpression ce = new CronExpression(duedate, null);

                return ce.getTimeAfter(new Date());
            }
        } catch (Exception e) {
            throw new ActivitiException("Failed to parse cron expression: "
                    + duedate, e);
        }
    }

    public String getName() {
        return "cycle";
    }
}
