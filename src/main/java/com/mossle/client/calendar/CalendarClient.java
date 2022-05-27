package com.mossle.client.calendar;

import java.util.List;

public interface CalendarClient {
    CalendarEventDTO create(CalendarEventDTO calendarEventDto);

    CalendarEventDTO update(CalendarEventDTO calendarEventDto);

    CalendarEventDTO remove(String eventId);

    CalendarEventDTO findEventById(String eventId);

    List<CalendarEventDTO> findEvents(String calendarId);
}
