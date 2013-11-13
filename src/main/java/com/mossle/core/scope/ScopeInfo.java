package com.mossle.core.scope;

public class ScopeInfo {
    private String globalCode;
    private String localCode;

    public ScopeInfo(String globalCode, String localCode) {
        this.globalCode = globalCode;
        this.localCode = localCode;
    }

    public String getGlobalCode() {
        return globalCode;
    }

    public void setGlobalCode(String globalCode) {
        this.globalCode = globalCode;
    }

    public String getLocalCode() {
        return localCode;
    }

    public void setLocalCode(String localCode) {
        this.localCode = localCode;
    }
}
