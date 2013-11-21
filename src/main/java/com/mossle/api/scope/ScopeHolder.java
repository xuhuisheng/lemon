package com.mossle.api.scope;

public class ScopeHolder {
    private static ThreadLocal<ScopeInfo> scopeThreadLocal = new ThreadLocal<ScopeInfo>();

    protected ScopeHolder() {
    }

    public static String getScopeId() {
        return getScopeInfo().getId();
    }

    public static String getScopeCode() {
        return getScopeInfo().getCode();
    }

    public static String getUserRepoRef() {
        return getScopeInfo().getUserRepoRef();
    }

    public static ScopeInfo getScopeInfo() {
        ScopeInfo scopeInfo = scopeThreadLocal.get();

        if (scopeInfo == null) {
            throw new IllegalStateException("cannot find scope");
        }

        return scopeInfo;
    }

    public static void setScopeInfo(ScopeInfo scopeInfo) {
        scopeThreadLocal.set(scopeInfo);
    }

    public static void clear() {
        scopeThreadLocal.remove();
    }
}
