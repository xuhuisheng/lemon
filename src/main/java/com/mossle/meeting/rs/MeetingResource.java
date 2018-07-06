package com.mossle.meeting.rs;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.mossle.meeting.persistence.domain.MeetingRoom;
import com.mossle.meeting.persistence.manager.MeetingRoomManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
@Path("meeting")
public class MeetingResource {
    private static Logger logger = LoggerFactory
            .getLogger(MeetingResource.class);
    private MeetingRoomManager meetingRoomManager;

    @GET
    @Path("list")
    public List<Map<String, Object>> list() throws Exception {
        List<MeetingRoom> meetingRooms = meetingRoomManager.findBy("status",
                "0");
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (MeetingRoom meetingRoom : meetingRooms) {
            Map<String, Object> map = new HashMap<String, Object>();
            list.add(map);
            map.put("key", meetingRoom.getId());
            map.put("label", meetingRoom.getName());
        }

        return list;
    }

    // ~ ======================================================================
    @Resource
    public void setMeetingRoomManager(MeetingRoomManager meetingRoomManager) {
        this.meetingRoomManager = meetingRoomManager;
    }
}
