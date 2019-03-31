package com.mossle.portal.data;

import java.util.List;

import com.mossle.core.csv.CsvCallback;

import com.mossle.portal.persistence.domain.PortalWidget;
import com.mossle.portal.persistence.manager.PortalWidgetManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PortalWidgetCallback implements CsvCallback {
    private static Logger logger = LoggerFactory
            .getLogger(PortalWidgetCallback.class);
    private PortalWidgetManager portalWidgetManager;
    private String defaultTenantId = "1";

    public void process(List<String> list, int lineNo) throws Exception {
        String name = list.get(0);
        String url = list.get(1);

        name = name.toLowerCase();

        PortalWidget portalWidget = portalWidgetManager.findUniqueBy("name",
                name);

        if (portalWidget != null) {
            return;
        }

        portalWidget = new PortalWidget();
        portalWidget.setName(name);
        portalWidget.setUrl(url);
        portalWidgetManager.save(portalWidget);
    }

    public void setPortalWidgetManager(PortalWidgetManager portalWidgetManager) {
        this.portalWidgetManager = portalWidgetManager;
    }
}
