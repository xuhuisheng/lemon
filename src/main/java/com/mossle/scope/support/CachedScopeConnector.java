package com.mossle.scope.support;

import java.util.List;

import com.mossle.api.GlobalScopeDTO;
import com.mossle.api.LocalScopeDTO;
import com.mossle.api.ScopeConnector;

import com.mossle.scope.component.ScopeCache;

// TODO: 重构scopeConnector
public class CachedScopeConnector implements ScopeConnector {
    private ScopeCache scopeCache;

    public Long findGlobalId(String globalCode) {
        return scopeCache.getScopeGlobal(globalCode).getId();
    }

    public Long findLocalId(String globalCode, String localCode) {
        String key = globalCode + ":" + localCode;

        return scopeCache.getScopeGlobal(key).getId();
    }

    public List<GlobalScopeDTO> findGlobalScopes() {
        return null;
    }

    public List<LocalScopeDTO> findLocalScopes() {
        return null;
    }

    public List<LocalScopeDTO> findSharedLocalScopes() {
        return null;
    }

    public LocalScopeDTO getLocalScope(Long localScopeId) {
        return null;
    }

    public GlobalScopeDTO getGlobalScope(Long globalScopeId) {
        return null;
    }

    public void setScopeCache(ScopeCache scopeCache) {
        this.scopeCache = scopeCache;
    }
}
