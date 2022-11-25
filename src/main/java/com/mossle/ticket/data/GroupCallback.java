package com.mossle.ticket.data;

import java.util.List;

import com.mossle.core.csv.CsvCallback;

import com.mossle.ticket.persistence.domain.TicketGroup;
import com.mossle.ticket.persistence.manager.TicketGroupManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupCallback implements CsvCallback {
    private static Logger logger = LoggerFactory.getLogger(GroupCallback.class);
    private TicketGroupManager ticketGroupManager;
    private String defaultTenantId = "1";

    public void process(List<String> list, int lineNo) throws Exception {
        logger.debug("default tenant id : {}", defaultTenantId);

        String code = list.get(0);
        String name = list.get(1);

        if (StringUtils.isBlank(code)) {
            logger.warn("code cannot be blank {} {}", lineNo, list);

            return;
        }

        code = code.toLowerCase();

        this.createOrUpdateTicketGroup(code, name, lineNo);
    }

    public void createOrUpdateTicketGroup(String code, String name, int lineNo) {
        TicketGroup ticketGroup = ticketGroupManager.findUniqueBy("code", code);

        if (ticketGroup != null) {
            return;
        }

        // insert
        ticketGroup = new TicketGroup();
        ticketGroup.setCode(code);
        ticketGroup.setName(name);
        ticketGroupManager.save(ticketGroup);
    }

    public void setTicketGroupManager(TicketGroupManager ticketGroupManager) {
        this.ticketGroupManager = ticketGroupManager;
    }
}
