package com.mossle.meeting.support;

import java.util.ArrayList;
import java.util.List;

public class MeetingRoomDTO {
    private Long id;
    private String code;
    private String name;
    private int num;
    private List<String> devices = new ArrayList<String>();
    private List<MeetingInfoDTO> infos = new ArrayList<MeetingInfoDTO>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public List<String> getDevices() {
        return devices;
    }

    public void setDevices(List<String> devices) {
        this.devices = devices;
    }

    public List<MeetingInfoDTO> getInfos() {
        return infos;
    }

    public void setInfos(List<MeetingInfoDTO> infos) {
        this.infos = infos;
    }
}
