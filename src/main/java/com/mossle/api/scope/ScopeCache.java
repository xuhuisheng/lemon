package com.mossle.api.scope;

public interface ScopeCache {
    ScopeInfo getByRef(String ref);

    ScopeInfo getByCode(String code);

    void updateScopeInfo(ScopeInfo scopeInfo);

    void refresh();
}
