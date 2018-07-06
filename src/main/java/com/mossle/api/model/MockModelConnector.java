package com.mossle.api.model;

import com.mossle.core.page.Page;

public class MockModelConnector implements ModelConnector {
    public ModelInfoDTO findByCode(String code) {
        return null;
    }

    public ModelInfoDTO save(ModelInfoDTO modelInfoDto) {
        return modelInfoDto;
    }

    public Page findDraft(int pageNo, int pageSize, String userId) {
        return null;
    }

    public void removeDraft(String code) {
    }
}
