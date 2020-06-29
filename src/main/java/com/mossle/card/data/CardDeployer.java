package com.mossle.card.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.Date;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mossle.card.persistence.manager.CardInfoManager;
import com.mossle.card.persistence.manager.DoorInfoManager;

import com.mossle.core.csv.CsvProcessor;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CardDeployer {
    private static Logger logger = LoggerFactory.getLogger(CardDeployer.class);
    private DoorInfoManager doorInfoManager;
    private CardInfoManager cardInfoManager;
    private String dataFilePath = "data/door.csv";
    private String dataFileEncoding = "GB2312";
    private String cardDataFilePath = "data/card.csv";
    private String cardDataFileEncoding = "GB2312";
    private String defaultTenantId = "1";
    private boolean enable = true;

    @PostConstruct
    public void process() throws Exception {
        if (!enable) {
            logger.info("skip init card data");

            return;
        }

        DoorInfoCallback doorInfoCallback = new DoorInfoCallback();
        doorInfoCallback.setDoorInfoManager(doorInfoManager);
        new CsvProcessor().process(dataFilePath, dataFileEncoding,
                doorInfoCallback);

        CardInfoCallback cardInfoCallback = new CardInfoCallback();
        cardInfoCallback.setCardInfoManager(cardInfoManager);
        new CsvProcessor().process(cardDataFilePath, cardDataFileEncoding,
                cardInfoCallback);
    }

    @Resource
    public void setDoorInfoManager(DoorInfoManager doorInfoManager) {
        this.doorInfoManager = doorInfoManager;
    }

    @Resource
    public void setCardInfoManager(CardInfoManager cardInfoManager) {
        this.cardInfoManager = cardInfoManager;
    }
}
