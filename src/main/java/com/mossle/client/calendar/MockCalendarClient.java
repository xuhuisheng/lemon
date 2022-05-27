package com.mossle.client.calendar;

import java.util.Collections;
import java.util.List;

public class MockCalendarClient implements CalendarClient {
    public CalendarEventDTO create(CalendarEventDTO calendarEventDto) {
        return null;
    }

    public CalendarEventDTO update(CalendarEventDTO calendarEventDto) {
        return null;
    }

    public CalendarEventDTO remove(String eventId) {
        return null;
    }

    public CalendarEventDTO findEventById(String eventId) {
        return null;
    }

    public List<CalendarEventDTO> findEvents(String calendarId) {
        return Collections.emptyList();
    }
}
