package com.mossle.client.calendar;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.Collections;
import java.util.List;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.util.BaseDTO;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

public class HttpCalendarClient implements CalendarClient {
    private static Logger logger = LoggerFactory
            .getLogger(HttpCalendarClient.class);
    private JsonMapper jsonMapper = new JsonMapper();
    private String baseUrl;

    public CalendarEventDTO create(CalendarEventDTO calendarEventDto) {
        try {
            String url = baseUrl + "/calendar/rs/calendar/create.do";
            HttpURLConnection conn = (HttpURLConnection) new URL(url)
                    .openConnection();
            conn.addRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String payload = jsonMapper.toJson(calendarEventDto);
            conn.getOutputStream().write(payload.getBytes("utf-8"));

            String text = IOUtils.toString(conn.getInputStream(), "UTF-8");
            BaseDTO baseDto = jsonMapper.fromJson(text, BaseDTO.class);

            return null;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return null;
        }
    }

    public CalendarEventDTO update(CalendarEventDTO calendarEventDto) {
        try {
            String url = baseUrl + "/calendar/rs/calendar/update.do";
            HttpURLConnection conn = (HttpURLConnection) new URL(url)
                    .openConnection();
            conn.addRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String payload = jsonMapper.toJson(calendarEventDto);
            conn.getOutputStream().write(payload.getBytes("utf-8"));

            String text = IOUtils.toString(conn.getInputStream(), "UTF-8");
            BaseDTO baseDto = jsonMapper.fromJson(text, BaseDTO.class);

            return null;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return null;
        }
    }

    public CalendarEventDTO remove(String eventId) {
        try {
            String url = baseUrl + "/calendar/rs/calendar/remove.do?id="
                    + eventId;
            HttpURLConnection conn = (HttpURLConnection) new URL(url)
                    .openConnection();

            String text = IOUtils.toString(conn.getInputStream(), "UTF-8");
            BaseDTO baseDto = jsonMapper.fromJson(text, BaseDTO.class);

            return null;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return null;
        }
    }

    public CalendarEventDTO findEventById(String eventId) {
        return null;
    }

    public List<CalendarEventDTO> findEvents(String calendarId) {
        return Collections.emptyList();
    }

    // ~
    @Value("${client.calendar.url}")
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
