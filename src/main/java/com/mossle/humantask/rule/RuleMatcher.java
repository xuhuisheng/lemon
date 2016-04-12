package com.mossle.humantask.rule;

public interface RuleMatcher {
    boolean matches(String text);

    String getValue(String text);
}
