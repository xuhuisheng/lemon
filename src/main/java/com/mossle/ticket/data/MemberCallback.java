package com.mossle.ticket.data;

import java.util.List;

import com.mossle.core.csv.CsvCallback;

import com.mossle.ticket.persistence.domain.TicketGroup;
import com.mossle.ticket.persistence.domain.TicketMember;
import com.mossle.ticket.persistence.manager.TicketGroupManager;
import com.mossle.ticket.persistence.manager.TicketMemberManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemberCallback implements CsvCallback {
    private static Logger logger = LoggerFactory
            .getLogger(MemberCallback.class);
    private TicketMemberManager ticketMemberManager;
    private TicketGroupManager ticketGroupManager;
    private String defaultTenantId = "1";

    public void process(List<String> list, int lineNo) throws Exception {
        logger.debug("default tenant id : {}", defaultTenantId);

        String userId = list.get(0);
        String groupCode = list.get(1);

        if (StringUtils.isBlank(userId)) {
            logger.warn("userId cannot be blank {} {}", lineNo, list);

            return;
        }

        userId = userId.toLowerCase();

        this.createOrUpdateTicketMember(userId, groupCode, lineNo);
    }

    public void createOrUpdateTicketMember(String userId, String groupCode,
            int lineNo) {
        TicketGroup ticketGroup = ticketGroupManager.findUniqueBy("code",
                groupCode);
        String hql = "from TicketMember where userId=? and ticketGroup=?";
        TicketMember ticketMember = ticketMemberManager.findUnique(hql, userId,
                ticketGroup);

        if (ticketMember != null) {
            return;
        }

        // insert
        ticketMember = new TicketMember();
        ticketMember.setUserId(userId);
        ticketMember.setTicketGroup(ticketGroup);
        ticketMemberManager.save(ticketMember);
    }

    public void setTicketMemberManager(TicketMemberManager ticketMemberManager) {
        this.ticketMemberManager = ticketMemberManager;
    }

    public void setTicketGroupManager(TicketGroupManager ticketGroupManager) {
        this.ticketGroupManager = ticketGroupManager;
    }
}
