package com.mossle.api.scope;

public class ScopeHolder {
    private static ThreadLocal<ScopeDTO> scopeThreadLocal = new ThreadLocal<ScopeDTO>();

    protected ScopeHolder() {
    }

    public static String getScopeId() {
        return getScopeDto().getId();
    }

    public static String getScopeCode() {
        return getScopeDto().getCode();
    }

    public static String getUserRepoRef() {
        return getScopeDto().getUserRepoRef();
    }

    public static ScopeDTO getScopeDto() {
        ScopeDTO scopeDto = scopeThreadLocal.get();

        if (scopeDto == null) {
            throw new IllegalStateException("cannot find scope");
        }

        return scopeDto;
    }

    public static void setScopeDto(ScopeDTO scopeDto) {
        scopeThreadLocal.set(scopeDto);
    }

    public static void clear() {
        scopeThreadLocal.remove();
    }
}
