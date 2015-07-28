package com.mossle.api.user;

public class MockApplicationAliasConverter implements ApplicationAliasConverter {
    public String convertAlias(String type, String ip) {
        return type;
    }
}
