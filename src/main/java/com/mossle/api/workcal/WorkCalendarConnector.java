package com.mossle.api.workcal;

import java.util.Date;

import javax.xml.datatype.Duration;

public interface WorkCalendarConnector {
    Date processDate(Date date, String tenantId);

    Date add(Date date, Duration duration, String tenantId);
}
