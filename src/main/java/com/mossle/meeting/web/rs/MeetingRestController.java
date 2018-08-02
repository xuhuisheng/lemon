package com.mossle.meeting.web.rs;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.auth.CurrentUserHolder;

import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.util.BaseDTO;

import com.mossle.meeting.persistence.domain.MeetingInfo;
import com.mossle.meeting.persistence.domain.MeetingRoom;
import com.mossle.meeting.persistence.manager.MeetingInfoManager;
import com.mossle.meeting.persistence.manager.MeetingRoomManager;
import com.mossle.meeting.support.DurationHelper;
import com.mossle.meeting.support.MeetingInfoDTO;
import com.mossle.meeting.support.MeetingRoomDTO;

import org.apache.commons.lang3.StringUtils;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequestMapping("meeting/rs")
public class MeetingRestController {
    private CurrentUserHolder currentUserHolder;
    private MeetingRoomManager meetingRoomManager;
    private MeetingInfoManager meetingInfoManager;
    private BeanMapper beanMapper = new BeanMapper();
    private String startTime = "9:00";
    private String endTime = "18:00";

    @RequestMapping("rooms")
    public BaseDTO rooms(
            @RequestParam("building") String building,
            @RequestParam("floor") String floor,
            @RequestParam(value = "calendarDate", required = false) String calendarDate,
            Model model) throws Exception {
        if (calendarDate == null) {
            Date now = new Date();
            calendarDate = new SimpleDateFormat("yyyy-MM-dd").format(now);
        }

        String userId = currentUserHolder.getUserId();

        String hql = "from MeetingRoom where building=? and floor=?";
        List<MeetingRoom> meetingRooms = meetingRoomManager.find(hql, building,
                floor);
        List<MeetingRoomDTO> meetingRoomDtos = this
                .convertMeetingRooms(meetingRooms);

        for (MeetingRoomDTO meetingRoomDto : meetingRoomDtos) {
            this.processMeetingRoom(meetingRoomDto, calendarDate);
        }

        BaseDTO baseDto = new BaseDTO();
        baseDto.setData(meetingRoomDtos);

        return baseDto;
    }

    // ~
    public void processMeetingRoom(MeetingRoomDTO meetingRoomDto,
            String calendarDateText) throws Exception {
        Date calendarDate = new SimpleDateFormat("yyyy-MM-dd")
                .parse(calendarDateText);
        String hql = "from MeetingInfo where meetingRoom.id=? and calendarDate=? order by startTime";
        List<MeetingInfo> meetingInfos = this.meetingInfoManager.find(hql,
                meetingRoomDto.getId(), calendarDate);
        DurationHelper durationHelper = new DurationHelper();
        durationHelper.process(calendarDateText, meetingInfos);

        meetingRoomDto.getInfos().addAll(durationHelper.getMeetingInfoDtos());
    }

    public List<MeetingRoomDTO> convertMeetingRooms(
            List<MeetingRoom> meetingRooms) {
        List<MeetingRoomDTO> meetingRoomDtos = new ArrayList<MeetingRoomDTO>();

        for (MeetingRoom meetingRoom : meetingRooms) {
            meetingRoomDtos.add(this.convertMeetingRoom(meetingRoom));
        }

        return meetingRoomDtos;
    }

    public MeetingRoomDTO convertMeetingRoom(MeetingRoom meetingRoom) {
        MeetingRoomDTO meetingRoomDto = new MeetingRoomDTO();
        beanMapper.copy(meetingRoom, meetingRoomDto);

        if (StringUtils.isNotBlank(meetingRoom.getDevice())) {
            String[] array = meetingRoom.getDevice().split(",");
            meetingRoomDto.setDevices(Arrays.asList(array));
        }

        return meetingRoomDto;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setMeetingRoomManager(MeetingRoomManager meetingRoomManager) {
        this.meetingRoomManager = meetingRoomManager;
    }

    @Resource
    public void setMeetingInfoManager(MeetingInfoManager meetingInfoManager) {
        this.meetingInfoManager = meetingInfoManager;
    }
}
