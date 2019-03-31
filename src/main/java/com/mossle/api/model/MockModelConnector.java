package com.mossle.api.model;

import java.util.List;
import java.util.Map;

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

    public long findDraftCount(String userId) {
        return 0L;
    }

    public void removeDraft(String code) {
    }

    public ModelInfoDTO copyModel(ModelInfoDTO original, List<String> fields) {
        return null;
    }

    public long findTotalCount(String category, String q, String tenantId) {
        return 0;
    }

    public List<Map<String, Object>> findResult(Page page, String category,
            String tenantId, Map<String, String> headers, String q) {
        return null;
    }
}
