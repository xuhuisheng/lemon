package com.mossle.ticket.data;

import java.util.List;

import com.mossle.core.csv.CsvCallback;

import com.mossle.ticket.persistence.domain.TicketApp;
import com.mossle.ticket.persistence.manager.TicketAppManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppCallback implements CsvCallback {
    private static Logger logger = LoggerFactory.getLogger(AppCallback.class);
    private TicketAppManager ticketAppManager;
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

        this.createOrUpdateTicketApp(code, name, lineNo);
    }

    public void createOrUpdateTicketApp(String code, String name, int lineNo) {
        TicketApp ticketApp = ticketAppManager.findUniqueBy("code", code);

        if (ticketApp != null) {
            return;
        }

        // insert
        ticketApp = new TicketApp();
        ticketApp.setCode(code);
        ticketApp.setName(name);
        ticketAppManager.save(ticketApp);
    }

    public void setTicketAppManager(TicketAppManager ticketAppManager) {
        this.ticketAppManager = ticketAppManager;
    }

    public void setDefaultTenantId(String defaultTenantId) {
        this.defaultTenantId = defaultTenantId;
    }
}
