package com.mossle.bridge.scope;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mossle.api.scope.ScopeCache;
import com.mossle.api.scope.ScopeInfo;

import org.springframework.jdbc.core.JdbcTemplate;

public class DatabaseScopeCache implements ScopeCache {
    private Map<String, ScopeInfo> scopeInfoRefMap = new HashMap<String, ScopeInfo>();
    private Map<String, ScopeInfo> scopeInfoCodeMap = new HashMap<String, ScopeInfo>();
    private JdbcTemplate jdbcTemplate;

    public ScopeInfo getByRef(String ref) {
        return scopeInfoRefMap.get(ref);
    }

    public ScopeInfo getByCode(String code) {
        return scopeInfoCodeMap.get(code);
    }

    public void updateScopeInfo(ScopeInfo scopeInfo) {
        scopeInfoRefMap.put(scopeInfo.getRef(), scopeInfo);
        scopeInfoCodeMap.put(scopeInfo.getCode(), scopeInfo);
    }

    public void refresh() {
        String sql = "select si.id as scopeId,si.name as scopeName,si.code as scopeCode,"
                + " si.ref as scopeRef,si.shared as shared,si.user_repo_ref as userRepoRef"
                + " from scope_info si";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);

        for (Map<String, Object> map : list) {
            ScopeInfo scopeInfo = new ScopeInfo();
            scopeInfo.setId(map.get("scopeId").toString());
            scopeInfo.setName(map.get("scopeName").toString());
            scopeInfo.setCode(map.get("scopeCode").toString());
            scopeInfo.setRef(map.get("scopeRef").toString());
            scopeInfo.setShared(Integer.valueOf(1).equals(map.get("shared")));
            scopeInfo.setUserRepoRef(map.get("userRepoRef").toString());
            this.updateScopeInfo(scopeInfo);
        }
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
