package com.mossle.api.workcal;

import java.util.Date;

import javax.xml.datatype.Duration;

public class MockWorkCalendarConnector implements WorkCalendarConnector {
    public Date processDate(Date date, String tenantId) {
        return date;
    }

    public Date add(Date date, Duration duration, String tenantId) {
        duration.addTo(date);

        return date;
    }
}
