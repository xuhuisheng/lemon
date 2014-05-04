package com.mossle.api.scope;

public interface ScopeCache {
    ScopeDTO findById(String id);

    ScopeDTO findByRef(String ref);

    ScopeDTO findByCode(String code);

    void updateScope(ScopeDTO scopeDto);

    void removeScope(ScopeDTO scopeDto);
}
