package com.mossle.bridge.scope;

import java.util.List;

import com.mossle.api.scope.ScopeCache;
import com.mossle.api.scope.ScopeConnector;
import com.mossle.api.scope.ScopeDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScopeConnectorWrapper implements ScopeConnector {
    private static Logger logger = LoggerFactory
            .getLogger(ScopeConnectorWrapper.class);
    private ScopeConnector scopeConnector;
    private ScopeCache scopeCache;

    public ScopeDTO findById(String id) {
        ScopeDTO scopeDto = scopeCache.findById(id);

        if (scopeDto == null) {
            synchronized (scopeCache) {
                scopeDto = scopeCache.findById(id);

                if (scopeDto == null) {
                    scopeDto = scopeConnector.findById(id);

                    if (scopeDto != null) {
                        scopeCache.updateScope(scopeDto);
                    }
                }
            }
        }

        return scopeDto;
    }

    public ScopeDTO findByRef(String ref) {
        ScopeDTO scopeDto = scopeCache.findByRef(ref);

        if (scopeDto == null) {
            synchronized (scopeCache) {
                scopeDto = scopeCache.findByRef(ref);

                if (scopeDto == null) {
                    scopeDto = scopeConnector.findByRef(ref);

                    if (scopeDto != null) {
                        scopeCache.updateScope(scopeDto);
                    }
                }
            }
        }

        return scopeDto;
    }

    public ScopeDTO findByCode(String code) {
        ScopeDTO scopeDto = scopeCache.findByCode(code);

        if (scopeDto == null) {
            synchronized (scopeCache) {
                scopeDto = scopeCache.findByCode(code);

                if (scopeDto == null) {
                    scopeDto = scopeConnector.findByCode(code);

                    if (scopeDto != null) {
                        scopeCache.updateScope(scopeDto);
                    }
                }
            }
        }

        return scopeDto;
    }

    public List<ScopeDTO> findAll() {
        return scopeConnector.findAll();
    }

    public List<ScopeDTO> findSharedScopes() {
        return scopeConnector.findSharedScopes();
    }

    public void setScopeConnector(ScopeConnector scopeConnector) {
        this.scopeConnector = scopeConnector;
    }

    public void setScopeCache(ScopeCache scopeCache) {
        this.scopeCache = scopeCache;
    }
}
