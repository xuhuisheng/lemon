package com.mossle.meeting.web;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.auth.CurrentUserHolder;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;

import com.mossle.meeting.persistence.domain.MeetingAttendee;
import com.mossle.meeting.persistence.domain.MeetingInfo;
import com.mossle.meeting.persistence.domain.MeetingRoom;
import com.mossle.meeting.persistence.manager.MeetingAttendeeManager;
import com.mossle.meeting.persistence.manager.MeetingInfoManager;
import com.mossle.meeting.persistence.manager.MeetingRoomManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("meeting")
public class MeetingController {
    private static Logger logger = LoggerFactory
            .getLogger(MeetingController.class);
    private MeetingRoomManager meetingRoomManager;
    private MeetingInfoManager meetingInfoManager;
    private MeetingAttendeeManager meetingAttendeeManager;
    private CurrentUserHolder currentUserHolder;
    private TenantHolder tenantHolder;

    @RequestMapping("index")
    public String index(
            @RequestParam(value = "calendarDate", required = false) String calendarDate,
            Model model) throws Exception {
        if (calendarDate == null) {
            Date now = new Date();
            calendarDate = new SimpleDateFormat("yyyy-MM-dd").format(now);
        }

        model.addAttribute("calendarDate", calendarDate);

        return "meeting/index";
    }

    @RequestMapping("create")
    public String create(@RequestParam("roomId") Long roomId,
            @RequestParam("calendarDate") String calendarDate,
            @RequestParam("startTime") String startTime,
            @RequestParam("endTime") String endTime, Model model)
            throws Exception {
        MeetingRoom meetingRoom = meetingRoomManager.get(roomId);
        model.addAttribute("meetingRoom", meetingRoom);

        model.addAttribute("startTime", startTime);

        long startOffset = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(
                calendarDate + " " + startTime).getTime();
        long endOffset = new SimpleDateFormat("yyy-MM-dd HH:mm").parse(
                calendarDate + " " + endTime).getTime();

        if ((endOffset - startOffset) > (60 * 60 * 1000)) {
            Date endTimeDate = new Date(startOffset + (60 * 60 * 1000));
            endTime = new SimpleDateFormat("HH:mm").format(endTimeDate);
        }

        model.addAttribute("endTime", endTime);

        return "meeting/create";
    }

    @RequestMapping("save")
    public String save(@RequestParam("roomId") Long roomId,
            MeetingInfo meetingInfo,
            @RequestParam("startTime") String startTime,
            @RequestParam("endTime") String endTime,
            @RequestParam("attendees") String attendees) throws Exception {
        logger.info("calendarDate : {}", meetingInfo.getCalendarDate());

        String calendarDateText = new SimpleDateFormat("yyyy-MM-dd")
                .format(meetingInfo.getCalendarDate());

        MeetingRoom meetingRoom = meetingRoomManager.get(roomId);
        meetingInfo.setStartTime(new SimpleDateFormat("yyyy-MM-dd HH:mm")
                .parse(calendarDateText + " " + startTime));
        meetingInfo.setEndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm")
                .parse(calendarDateText + " " + endTime));
        meetingInfo.setMeetingRoom(meetingRoom);
        meetingInfo.setTenantId("1");
        meetingInfoManager.save(meetingInfo);

        MeetingInfo dest = meetingInfo;

        for (String attendee : attendees.split(",")) {
            MeetingAttendee meetingAttendee = new MeetingAttendee();
            /*
             * UserDTO userDto = userConnector .findByUsername(attendee.trim(), "1");
             * 
             * if (userDto == null) { logger.info("cannot find attendee : {}", attendee.trim());
             * 
             * continue; }
             * 
             * meetingAttendee.setUserId(userDto.getId());
             */
            meetingAttendee.setUserId(attendee);
            meetingAttendee.setMeetingInfo(dest);
            meetingInfoManager.save(meetingAttendee);
        }

        return "redirect:/meeting/index.do?calendarDate=" + calendarDateText;
    }

    @RequestMapping("list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        String tenantId = tenantHolder.getTenantId();
        String userId = currentUserHolder.getUserId();
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        propertyFilters.add(new PropertyFilter("EQS_tenantId", tenantId));
        propertyFilters.add(new PropertyFilter("EQS_organizer", userId));
        page = meetingInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "meeting/list";
    }

    @Resource
    public void setMeetingRoomManager(MeetingRoomManager meetingRoomManager) {
        this.meetingRoomManager = meetingRoomManager;
    }

    @Resource
    public void setMeetingInfoManager(MeetingInfoManager meetingInfoManager) {
        this.meetingInfoManager = meetingInfoManager;
    }

    @Resource
    public void setMeetingAttendeeManager(
            MeetingAttendeeManager meetingAttendeeManager) {
        this.meetingAttendeeManager = meetingAttendeeManager;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}
