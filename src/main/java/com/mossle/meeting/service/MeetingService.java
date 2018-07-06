package com.mossle.meeting.service;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.user.UserConnector;
import com.mossle.api.user.UserDTO;

import com.mossle.core.util.TimeUtils;

import com.mossle.meeting.persistence.domain.MeetingAttendee;
import com.mossle.meeting.persistence.domain.MeetingInfo;
import com.mossle.meeting.persistence.domain.MeetingRoom;
import com.mossle.meeting.persistence.manager.MeetingInfoManager;
import com.mossle.meeting.persistence.manager.MeetingRoomManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

@Service
public class MeetingService {
    private static Logger logger = LoggerFactory
            .getLogger(MeetingService.class);
    private MeetingInfoManager meetingInfoManager;
    private MeetingRoomManager meetingRoomManager;
    private UserConnector userConnector;

    public void makeOrder(String userId, String code, String subject,
            String startTimeText, String endTimeText, String attendees)
            throws Exception {
        MeetingRoom meetingRoom = meetingRoomManager.get(Long.parseLong(code));

        Date startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm")
                .parse(startTimeText);
        Date endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm")
                .parse(endTimeText);

        if (this.notValid(meetingRoom, startTime, endTime)) {
            return;
        }

        if (StringUtils.isNotBlank(userId)) {
            userId = userId.replace("[", "").replace("]", "");
        }

        MeetingInfo meetingInfo = new MeetingInfo();
        meetingInfo.setOrganizer(userId);
        meetingInfo.setMeetingRoom(meetingRoom);
        meetingInfo.setSubject(subject);
        meetingInfo.setCreateTime(new Date());
        meetingInfo.setStartTime(startTime);
        meetingInfo.setEndTime(endTime);
        meetingInfo.setTenantId("1");

        meetingInfoManager.save(meetingInfo);

        if (StringUtils.isBlank(attendees)) {
            return;
        }

        meetingInfoManager.removeAll(meetingInfo.getMeetingAttendees());

        for (String attendee : attendees.split(",")) {
            MeetingAttendee meetingAttendee = new MeetingAttendee();
            UserDTO userDto = userConnector
                    .findByUsername(attendee.trim(), "1");

            if (userDto == null) {
                logger.info("cannot find attendee : {}", attendee.trim());

                continue;
            }

            meetingAttendee.setUserId(userDto.getId());
            meetingAttendee.setMeetingInfo(meetingInfo);
            meetingInfoManager.save(meetingAttendee);
        }
    }

    public boolean notValid(MeetingRoom meetingRoom, Date startTime,
            Date endTime) {
        List<MeetingInfo> meetingInfos = this.meetingInfoManager.findBy(
                "meetingRoom", meetingRoom);

        for (MeetingInfo meetingInfo : meetingInfos) {
            if (TimeUtils.timeCross(meetingInfo.getStartTime(),
                    meetingInfo.getEndTime(), startTime, endTime)) {
                logger.info("time cross {} {} {} {} {}",
                        meetingInfo.getSubject(), meetingInfo.getStartTime(),
                        meetingInfo.getEndTime(), startTime, endTime);

                return true;
            }
        }

        return false;
    }

    @Resource
    public void setMeetingInfoManager(MeetingInfoManager meetingInfoManager) {
        this.meetingInfoManager = meetingInfoManager;
    }

    @Resource
    public void setMeetingRoomManager(MeetingRoomManager meetingRoomManager) {
        this.meetingRoomManager = meetingRoomManager;
    }

    @Resource
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }
}
