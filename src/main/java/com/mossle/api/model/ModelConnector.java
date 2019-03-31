package com.mossle.api.model;

import java.util.List;
import java.util.Map;

import com.mossle.core.page.Page;

public interface ModelConnector {
    // 根据code查询模型
    ModelInfoDTO findByCode(String code);

    // 保存模型
    ModelInfoDTO save(ModelInfoDTO modelInfoDto);

    // 查询个人的所有草稿
    Page findDraft(int pageNo, int pageSize, String userId);

    long findDraftCount(String userId);

    // 删除草稿
    void removeDraft(String code);

    // 复制模型
    ModelInfoDTO copyModel(ModelInfoDTO original, List<String> fields);

    /**
     * 查询总数.
     */
    long findTotalCount(String category, String q, String tenantId);

    /**
     * 分页查询数据.
     */
    List<Map<String, Object>> findResult(Page page, String category,
            String tenantId, Map<String, String> headers, String q);
}
