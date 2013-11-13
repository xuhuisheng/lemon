package com.mossle.api;

import java.util.List;

public interface ScopeConnector {
    Long findGlobalId(String globalCode);

    Long findLocalId(String globalCode, String localCode);

    List<GlobalScopeDTO> findGlobalScopes();

    List<LocalScopeDTO> findLocalScopes();

    List<LocalScopeDTO> findSharedLocalScopes();

	LocalScopeDTO getLocalScope(Long localScopeId);

	GlobalScopeDTO getGlobalScope(Long globalScopeId);
}
