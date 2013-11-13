package com.mossle.scope.component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mossle.api.GlobalScopeDTO;
import com.mossle.api.LocalScopeDTO;

import com.mossle.scope.domain.ScopeGlobal;
import com.mossle.scope.domain.ScopeLocal;

import org.springframework.stereotype.Component;

@Component
public class ScopeCache {
    private Map<String, GlobalScopeDTO> globalMap = new HashMap<String, GlobalScopeDTO>();
    private Map<String, LocalScopeDTO> localMap = new HashMap<String, LocalScopeDTO>();

    public GlobalScopeDTO getScopeGlobal(String key) {
        return globalMap.get(key);
    }

    public LocalScopeDTO getScopeLocal(String key) {
        return localMap.get(key);
    }

    public void updateScopeGlobal(ScopeGlobal scopeGlobal, boolean updateLocal) {
        GlobalScopeDTO globalScopeDto = new GlobalScopeDTO();
        globalScopeDto.setId(globalScopeDto.getId());
        globalScopeDto.setName(globalScopeDto.getName());
        this.globalMap.put(scopeGlobal.getName(), globalScopeDto);

        if (updateLocal) {
            for (ScopeLocal scopeLocal : scopeGlobal.getScopeLocals()) {
                updateScopeLocal(scopeLocal);
            }
        }
    }

    public void updateScopeLocal(ScopeLocal scopeLocal) {
        ScopeGlobal scopeGlobal = scopeLocal.getScopeGlobal();
        String key = scopeGlobal.getName() + ":" + scopeLocal.getName();
        LocalScopeDTO localScopeDto = new LocalScopeDTO();
        localScopeDto.setId(scopeLocal.getId());
        localScopeDto.setName(scopeLocal.getName());
        localScopeDto.setGlobalId(scopeGlobal.getId());
        localScopeDto.setGlobalName(scopeGlobal.getName());
        localScopeDto.setShared(scopeLocal.getShared() == 1);
        this.localMap.put(key, localScopeDto);
    }

    public void refresh(List<ScopeGlobal> scopeGlobals) {
        for (ScopeGlobal scopeGlobal : scopeGlobals) {
            this.updateScopeGlobal(scopeGlobal, true);
        }
    }
}
