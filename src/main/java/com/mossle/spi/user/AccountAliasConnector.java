package com.mossle.spi.user;

public interface AccountAliasConnector {
    String findUsernameByAlias(String username);

    void updateAlias(String username, String type, String alias);
}
