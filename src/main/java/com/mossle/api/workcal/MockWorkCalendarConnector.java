package com.mossle.api.workcal;

import java.util.Date;

import javax.xml.datatype.Duration;

public class MockWorkCalendarConnector implements WorkCalendarConnector {
    public Date processDate(Date date) {
        return date;
    }

    public Date add(Date date, Duration duration) {
        duration.addTo(date);

        return date;
    }
}
