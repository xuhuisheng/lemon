package com.mossle.api.scope;

import java.util.List;

public interface ScopeConnector {
    ScopeDTO findById(String id);

    ScopeDTO findByRef(String ref);

    ScopeDTO findByCode(String code);

    List<ScopeDTO> findAll();

    List<ScopeDTO> findSharedScopes();
}
