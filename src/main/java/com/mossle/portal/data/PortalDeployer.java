package com.mossle.portal.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.core.csv.CsvProcessor;

import com.mossle.portal.persistence.domain.PortalInfo;
import com.mossle.portal.persistence.domain.PortalItem;
import com.mossle.portal.persistence.domain.PortalWidget;
import com.mossle.portal.persistence.manager.PortalInfoManager;
import com.mossle.portal.persistence.manager.PortalItemManager;
import com.mossle.portal.persistence.manager.PortalWidgetManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PortalDeployer {
    private static Logger logger = LoggerFactory
            .getLogger(PortalDeployer.class);
    private PortalWidgetManager portalWidgetManager;
    private String dataFilePath = "data/portal-widget.csv";
    private String dataFileEncoding = "GB2312";
    private String dataItemFilePath = "data/portal-item.csv";
    private String defaultTenantId = "1";
    private boolean enable = true;
    private PortalWidgetCallback portalWidgetCallback;
    private PortalItemCallback portalItemCallback;
    private PortalInfoManager portalInfoManager;
    private PortalItemManager portalItemManager;

    public void init() {
        portalWidgetCallback = new PortalWidgetCallback();
        portalWidgetCallback.setPortalWidgetManager(portalWidgetManager);
        portalItemCallback = new PortalItemCallback();
        portalItemCallback.setPortalInfoManager(portalInfoManager);
        portalItemCallback.setPortalItemManager(portalItemManager);
        portalItemCallback.setPortalWidgetManager(portalWidgetManager);
    }

    @PostConstruct
    public void process() throws Exception {
        if (!enable) {
            logger.info("skip init {}", PortalDeployer.class);

            return;
        }

        this.init();

        new CsvProcessor().process(dataFilePath, dataFileEncoding,
                portalWidgetCallback);

        new CsvProcessor().process(dataItemFilePath, dataFileEncoding,
                portalItemCallback);
    }

    @Resource
    public void setPortalWidgetManager(PortalWidgetManager portalWidgetManager) {
        this.portalWidgetManager = portalWidgetManager;
    }

    @Resource
    public void setPortalInfoManager(PortalInfoManager portalInfoManager) {
        this.portalInfoManager = portalInfoManager;
    }

    @Resource
    public void setPortalItemManager(PortalItemManager portalItemManager) {
        this.portalItemManager = portalItemManager;
    }
}
