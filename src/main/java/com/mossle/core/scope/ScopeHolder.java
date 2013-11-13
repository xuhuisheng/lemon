package com.mossle.core.scope;

public class ScopeHolder {
    private static ThreadLocal<ScopeInfo> scopeThreadLocal = new ThreadLocal<ScopeInfo>();

    protected ScopeHolder() {
    }

    public static String getGlobalCode() {
        ScopeInfo scopeInfo = scopeThreadLocal.get();

        if (scopeInfo == null) {
            return null;
        } else {
            return scopeInfo.getGlobalCode();
        }
    }

    public static String getLocalCode() {
        ScopeInfo scopeInfo = scopeThreadLocal.get();

        if (scopeInfo == null) {
            return null;
        } else {
            return scopeInfo.getLocalCode();
        }
    }

    public static void setScope(String globalCode, String localCode) {
        scopeThreadLocal.set(new ScopeInfo(globalCode, localCode));
    }

    public static void clear() {
        scopeThreadLocal.remove();
    }
}
