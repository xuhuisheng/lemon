package com.mossle.portal.data;

import java.util.List;

import com.mossle.core.csv.CsvCallback;

import com.mossle.portal.persistence.domain.PortalInfo;
import com.mossle.portal.persistence.domain.PortalItem;
import com.mossle.portal.persistence.domain.PortalWidget;
import com.mossle.portal.persistence.manager.PortalInfoManager;
import com.mossle.portal.persistence.manager.PortalItemManager;
import com.mossle.portal.persistence.manager.PortalWidgetManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PortalItemCallback implements CsvCallback {
    private static Logger logger = LoggerFactory
            .getLogger(PortalItemCallback.class);
    private PortalInfoManager portalInfoManager;
    private PortalItemManager portalItemManager;
    private PortalWidgetManager portalWidgetManager;
    private String defaultTenantId = "1";

    public void process(List<String> list, int lineNo) throws Exception {
        String name = list.get(0);
        String columnIndex = list.get(1);
        String rowIndex = list.get(2);

        name = name.toLowerCase();

        PortalInfo portalInfo = this.createOrGetPortalInfo();

        PortalWidget portalWidget = portalWidgetManager.findUniqueBy("name",
                name);

        PortalItem portalItem = portalItemManager.findUnique(
                "from PortalItem where portalInfo=? and portalWidget=?",
                portalInfo, portalWidget);

        if (portalItem != null) {
            return;
        }

        portalItem = new PortalItem();
        portalItem.setPortalInfo(portalInfo);
        portalItem.setPortalWidget(portalWidget);
        portalItem.setName(name);
        portalItem.setColumnIndex(Integer.parseInt(columnIndex));
        portalItem.setRowIndex(Integer.parseInt(rowIndex));
        portalItemManager.save(portalItem);
    }

    public PortalInfo createOrGetPortalInfo() {
        PortalInfo portalInfo = portalInfoManager
                .findUnique("from PortalInfo where userId=null");

        if (portalInfo != null) {
            return portalInfo;
        }

        String columnLayout = "4-4-4";
        String sharedStatus = "true";
        String globalStatus = "true";
        portalInfo = new PortalInfo();
        portalInfo.setUserId(null);
        portalInfo.setColumnLayout(columnLayout);
        portalInfo.setSharedStatus(sharedStatus);
        portalInfo.setGlobalStatus(globalStatus);
        portalInfoManager.save(portalInfo);

        return portalInfo;
    }

    public void setPortalInfoManager(PortalInfoManager portalInfoManager) {
        this.portalInfoManager = portalInfoManager;
    }

    public void setPortalItemManager(PortalItemManager portalItemManager) {
        this.portalItemManager = portalItemManager;
    }

    public void setPortalWidgetManager(PortalWidgetManager portalWidgetManager) {
        this.portalWidgetManager = portalWidgetManager;
    }
}
