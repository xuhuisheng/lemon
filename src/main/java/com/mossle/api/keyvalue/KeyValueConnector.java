package com.mossle.api.keyvalue;

import java.util.List;
import java.util.Map;

import com.mossle.core.page.Page;

public interface KeyValueConnector {
    /**
     * 根据code查询数据. 认为code是业务主键.
     */
    Record findByCode(String code);

    /**
     * 根据ref查询数据. 认为ref是流程实例id.
     */
    Record findByRef(String ref);

    /**
     * 保存数据.
     */
    void save(Record record);

    /**
     * 根据code删除数据.
     */
    void removeByCode(String code);

    /**
     * 查询对应状态的，某人发起的数据，主要用来查询草稿.
     */
    List<Record> findByStatus(int status, String userId, String tenantId);

    /**
     * 分页查询.
     */
    Page pagedQuery(Page page, int status, String userId, String tenantId);

    /**
     * 查询总数.
     */
    long findTotalCount(String category, String q, String tenantId);

    /**
     * 分页查询数据.
     */
    List<Map<String, Object>> findResult(Page page, String category,
            String tenantId, Map<String, String> headers, String q);

    /**
     * 复制数据.
     */
    Record copyRecord(Record original, List<String> fields);
}
