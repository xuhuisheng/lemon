package com.mossle.meeting.data;

import java.util.List;

import com.mossle.core.csv.CsvCallback;

import com.mossle.meeting.persistence.domain.MeetingRoom;
import com.mossle.meeting.persistence.manager.MeetingRoomManager;

public class MeetingCallback implements CsvCallback {
    private MeetingRoomManager meetingRoomManager;
    private String defaultTenantId = "1";

    public void process(List<String> list, int lineNo) throws Exception {
        String code = list.get(0);
        String name = list.get(1);
        String num = list.get(2);
        String device = list.get(3);
        String type = list.get(4);
        String building = list.get(5);
        String floor = list.get(6);

        code = code.toLowerCase();

        MeetingRoom meetingRoom = meetingRoomManager.findUniqueBy("code", code);

        if (meetingRoom != null) {
            return;
        }

        meetingRoom = new MeetingRoom();
        meetingRoom.setCode(code);
        meetingRoom.setName(name);
        meetingRoom.setNum(Integer.parseInt(num));
        meetingRoom.setDevice(device);
        meetingRoom.setType(type);
        meetingRoom.setBuilding(building);
        meetingRoom.setFloor(floor);
        meetingRoom.setTenantId(defaultTenantId);
        meetingRoomManager.save(meetingRoom);
    }

    public void setMeetingRoomManager(MeetingRoomManager meetingRoomManager) {
        this.meetingRoomManager = meetingRoomManager;
    }
}
