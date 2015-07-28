package com.mossle.api.scope;

import java.util.List;

public class MockScopeConnector implements ScopeConnector {
    public ScopeDTO findById(String id) {
        return null;
    }

    public ScopeDTO findByRef(String ref) {
        return null;
    }

    public ScopeDTO findByCode(String code) {
        return null;
    }

    public List<ScopeDTO> findAll() {
        return null;
    }

    public List<ScopeDTO> findSharedScopes() {
        return null;
    }
}
