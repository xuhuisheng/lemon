package com.mossle.user.authenticate;

public class AuthnConfig {
    public static final String STRATEGY_DEFAULT = "default";
    public static final String STRATEGY_SIMPLE = "simple";
    private String strategy = STRATEGY_SIMPLE;

    public String getStrategy() {
        return strategy;
    }
}
