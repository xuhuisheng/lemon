package com.mossle.api.user;

public class MockAccountStatusHelper implements AccountStatusHelper {
    public boolean isLocked(String username, String application) {
        return false;
    }

    public String getAccountStatus(String username, String application) {
        return AccountStatus.ENABLED;
    }
}
