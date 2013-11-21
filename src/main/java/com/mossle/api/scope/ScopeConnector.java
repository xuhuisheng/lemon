package com.mossle.api.scope;

import java.util.List;

public interface ScopeConnector {
    ScopeInfo findById(String id);

    ScopeInfo findByRef(String ref);

    ScopeInfo findByCode(String code);

    List<ScopeInfo> findAll();

    List<ScopeInfo> findSharedScopes();
}
