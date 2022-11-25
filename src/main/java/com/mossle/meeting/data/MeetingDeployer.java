package com.mossle.meeting.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.core.csv.CsvProcessor;

import com.mossle.meeting.persistence.domain.MeetingRoom;
import com.mossle.meeting.persistence.manager.MeetingRoomManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

@Component("com.mossle.meeting.data.MeetingDeployer")
public class MeetingDeployer {
    private static Logger logger = LoggerFactory
            .getLogger(MeetingDeployer.class);
    private MeetingRoomManager meetingRoomManager;
    private String dataFilePath = "data/meeting/meeting-room.csv";
    private String dataFileEncoding = "GB2312";
    private String defaultTenantId = "1";
    private boolean enable = true;

    @PostConstruct
    public void process() throws Exception {
        if (!enable) {
            logger.info("skip meeting init data");

            return;
        }

        logger.info("start meeting init data");

        MeetingCallback meetingCallback = new MeetingCallback();
        meetingCallback.setMeetingRoomManager(meetingRoomManager);
        new CsvProcessor().process(dataFilePath, dataFileEncoding,
                meetingCallback);
        logger.info("end meeting init data");
    }

    @Resource
    public void setMeetingRoomManager(MeetingRoomManager meetingRoomManager) {
        this.meetingRoomManager = meetingRoomManager;
    }

    @Value("${meeting.data.init.enable:false}")
    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
