package com.mossle.scope.support;

import com.mossle.scope.domain.ScopeInfo;

public class ScopeInfoDTO extends ScopeInfo {
    private static final long serialVersionUID = 0L;
    private String userRepoCode;

    public String getUserRepoCode() {
        return userRepoCode;
    }

    public void setUserRepoCode(String userRepoCode) {
        this.userRepoCode = userRepoCode;
    }
}
