package com.mossle.meeting.support;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mossle.meeting.persistence.domain.MeetingInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DurationHelper {
    private static Logger logger = LoggerFactory
            .getLogger(DurationHelper.class);
    private String defaultStartTime = "9:00";
    private String defaultEndTime = "18:00";
    private long defaultStart;
    private long defaultEnd;
    private List<TimeRange> timeRanges = new ArrayList<TimeRange>();

    public void process(String calendarDate, List<MeetingInfo> meetingInfos)
            throws Exception {
        defaultStart = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(
                calendarDate + " " + defaultStartTime).getTime();
        defaultEnd = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(
                calendarDate + " " + defaultEndTime).getTime();

        // empty
        if (meetingInfos.isEmpty()) {
            this.addTimeRange(defaultStart, defaultEnd, "idle", null);

            return;
        }

        // first
        MeetingInfo first = meetingInfos.get(0);

        if (first.getStartTime().getTime() > defaultStart) {
            this.addTimeRange(defaultStart, first.getStartTime().getTime(),
                    "idle", null);
        }

        for (int i = 0; i < meetingInfos.size(); i++) {
            MeetingInfo current = meetingInfos.get(i);

            if (i == 0) {
                this.addTimeRange(current.getStartTime().getTime(), current
                        .getEndTime().getTime(), "busy", current.getOrganizer());

                continue;
            }

            MeetingInfo previous = meetingInfos.get(i - 1);

            if (previous.getEndTime().getTime() < current.getStartTime()
                    .getTime()) {
                this.addTimeRange(previous.getEndTime().getTime(), current
                        .getStartTime().getTime(), "idle", null);
            }

            this.addTimeRange(current.getStartTime().getTime(), current
                    .getEndTime().getTime(), "busy", current.getOrganizer());
        }

        // last
        MeetingInfo last = meetingInfos.get(meetingInfos.size() - 1);

        if (last.getEndTime().getTime() < defaultEnd) {
            this.addTimeRange(last.getEndTime().getTime(), defaultEnd, "idle",
                    null);
        }
    }

    public void addTimeRange(Long start, Long end, String status, String userId) {
        TimeRange timeRange = new TimeRange();
        timeRange.setStart(start);
        timeRange.setEnd(end);
        timeRange.setStatus(status);
        timeRange.setUserId(userId);
        logger.debug("{} {} {}", status, new Date(start), new Date(end));
        timeRanges.add(timeRange);
    }

    public List<MeetingInfoDTO> getMeetingInfoDtos() throws Exception {
        List<MeetingInfoDTO> infos = new ArrayList<MeetingInfoDTO>();

        for (TimeRange timeRange : timeRanges) {
            if ("idle".equals(timeRange.getStatus())) {
                MeetingInfoDTO meetingInfoDto = new MeetingInfoDTO();
                meetingInfoDto.setStartTime(this.formatDate(timeRange
                        .getStart()));
                meetingInfoDto.setEndTime(this.formatDate(timeRange.getEnd()));
                // meetingInfoDto.setUserId(timeRange.getUserId());
                meetingInfoDto.setStatus("idle");
                infos.add(meetingInfoDto);
            }
        }

        for (TimeRange timeRange : timeRanges) {
            if ("busy".equals(timeRange.getStatus())) {
                MeetingInfoDTO meetingInfoDto = new MeetingInfoDTO();
                meetingInfoDto.setStartTime(this.formatDate(timeRange
                        .getStart()));
                meetingInfoDto.setEndTime(this.formatDate(timeRange.getEnd()));
                meetingInfoDto.setUserId(timeRange.getUserId());
                meetingInfoDto.setStatus("busy");
                infos.add(meetingInfoDto);
            }
        }

        return infos;
    }

    public String formatDate(long time) throws Exception {
        return new SimpleDateFormat("HH:mm").format(new Date(time));
    }
}
