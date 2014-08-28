package com.mossle.api.workcal;

import java.util.Date;

import javax.xml.datatype.Duration;

public interface WorkCalendarConnector {
    Date processDate(Date date);

    Date add(Date date, Duration duration);
}
