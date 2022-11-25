package com.mossle.ticket.data;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.core.csv.CsvProcessor;

import com.mossle.ticket.persistence.manager.TicketAppManager;
import com.mossle.ticket.persistence.manager.TicketGroupManager;
import com.mossle.ticket.persistence.manager.TicketMemberManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

@Component("com.mossle.ticket.data.TicketDeployer")
public class TicketDeployer {
    private static Logger logger = LoggerFactory
            .getLogger(TicketDeployer.class);
    private TicketAppManager ticketAppManager;
    private TicketGroupManager ticketGroupManager;
    private TicketMemberManager ticketMemberManager;
    private String dataFilePathApp = "data/ticket/app.csv";
    private String dataFileEncodingApp = "GB2312";
    private String dataFilePathGroup = "data/ticket/group.csv";
    private String dataFileEncodingGroup = "GB2312";
    private String dataFilePathMember = "data/ticket/member.csv";
    private String dataFileEncodingMember = "GB2312";
    private String defaultTenantId = "1";
    private boolean enable = true;

    @PostConstruct
    public void process() throws Exception {
        if (!enable) {
            logger.info("skip ticket init data");

            return;
        }

        logger.info("start ticket init data");

        // app
        AppCallback appCallback = new AppCallback();
        appCallback.setDefaultTenantId(defaultTenantId);
        appCallback.setTicketAppManager(ticketAppManager);
        new CsvProcessor().process(dataFilePathApp, dataFileEncodingApp,
                appCallback);

        // group
        GroupCallback groupCallback = new GroupCallback();
        groupCallback.setTicketGroupManager(ticketGroupManager);
        new CsvProcessor().process(dataFilePathGroup, dataFileEncodingGroup,
                groupCallback);

        // member
        MemberCallback memberCallback = new MemberCallback();
        memberCallback.setTicketMemberManager(ticketMemberManager);
        memberCallback.setTicketGroupManager(ticketGroupManager);
        new CsvProcessor().process(dataFilePathMember, dataFileEncodingMember,
                memberCallback);

        logger.info("end ticket init data");
    }

    @Resource
    public void setTicketAppManager(TicketAppManager ticketAppManager) {
        this.ticketAppManager = ticketAppManager;
    }

    @Resource
    public void setTicketGroupManager(TicketGroupManager ticketGroupManager) {
        this.ticketGroupManager = ticketGroupManager;
    }

    @Resource
    public void setTicketMemberManager(TicketMemberManager ticketMemberManager) {
        this.ticketMemberManager = ticketMemberManager;
    }

    @Value("${ticket.data.init.enable:false}")
    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
