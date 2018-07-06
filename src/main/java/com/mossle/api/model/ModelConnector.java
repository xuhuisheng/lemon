package com.mossle.api.model;

import com.mossle.core.page.Page;

public interface ModelConnector {
    ModelInfoDTO findByCode(String code);

    ModelInfoDTO save(ModelInfoDTO modelInfoDto);

    Page findDraft(int pageNo, int pageSize, String userId);

    void removeDraft(String code);
}
