package com.mossle.api.user;

public interface AccountStatusHelper {
    boolean isLocked(String username, String application);

    String getAccountStatus(String username, String application);
}
