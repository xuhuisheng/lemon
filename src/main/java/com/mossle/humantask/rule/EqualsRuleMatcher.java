package com.mossle.humantask.rule;

public class EqualsRuleMatcher implements RuleMatcher {
    private String text;

    public EqualsRuleMatcher(String text) {
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null");
        }

        if ("".equals(text.trim())) {
            throw new IllegalArgumentException("text cannot be blank");
        }

        this.text = text;
    }

    public boolean matches(String text) {
        return this.text.equals(text);
    }

    public String getValue(String text) {
        return text;
    }
}
