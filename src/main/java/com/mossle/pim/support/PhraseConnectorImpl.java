package com.mossle.pim.support;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.phrase.PhraseConnector;

import com.mossle.pim.persistence.manager.PimPhraseManager;

public class PhraseConnectorImpl implements PhraseConnector {
    private PimPhraseManager pimPhraseManager;

    public List<String> findByUserId(String userId) {
        return pimPhraseManager.find(
                "select content from PimPhrase where userId=?", userId);
    }

    @Resource
    public void setPimPhraseManager(PimPhraseManager pimPhraseManager) {
        this.pimPhraseManager = pimPhraseManager;
    }
}
