package com.mossle.bridge.workcal;

import java.util.Date;

import javax.xml.datatype.Duration;

import com.mossle.api.workcal.WorkCalendarConnector;

public class MockWorkCalendarConnector implements WorkCalendarConnector {
    public Date processDate(Date date) {
        return date;
    }

    public Date add(Date date, Duration duration) {
        duration.addTo(date);

        return date;
    }
}
