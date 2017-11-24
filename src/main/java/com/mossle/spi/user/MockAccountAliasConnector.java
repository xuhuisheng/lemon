package com.mossle.spi.user;

public class MockAccountAliasConnector implements AccountAliasConnector {
    public String findUsernameByAlias(String username) {
        if (username == null) {
            return username;
        }

        return username.trim().toLowerCase();
    }

    public void updateAlias(String username, String type, String alias) {
    }
}
