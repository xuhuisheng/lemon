package com.mossle.card.data;

import java.util.List;

import com.mossle.card.persistence.domain.CardInfo;
import com.mossle.card.persistence.manager.CardInfoManager;

import com.mossle.core.csv.CsvCallback;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CardInfoCallback implements CsvCallback {
    private static Logger logger = LoggerFactory
            .getLogger(CardInfoCallback.class);
    private CardInfoManager cardInfoManager;
    private String defaultTenantId = "1";

    public void process(List<String> list, int lineNo) throws Exception {
        String code = list.get(0);
        String userId = list.get(1);

        if (StringUtils.isBlank(code)) {
            logger.warn("code cannot be blank {} {}", lineNo, list);

            return;
        }

        code = code.trim().toLowerCase();

        this.createOrUpdateCardInfo(code, userId, lineNo);
    }

    public void createOrUpdateCardInfo(String code, String userId, int lineNo) {
        CardInfo cardInfo = cardInfoManager.findUniqueBy("code", code);

        if (cardInfo != null) {
            return;
        }

        // insert
        cardInfo = new CardInfo();
        cardInfo.setType("normal");
        cardInfo.setCode(code);
        cardInfo.setUserId(userId);
        cardInfo.setStatus("active");
        cardInfo.setTenantId(defaultTenantId);
        cardInfoManager.save(cardInfo);
    }

    // ~
    public void setCardInfoManager(CardInfoManager cardInfoManager) {
        this.cardInfoManager = cardInfoManager;
    }
}
