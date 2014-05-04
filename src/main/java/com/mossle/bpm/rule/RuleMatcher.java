package com.mossle.bpm.rule;

public class RuleMatcher {
    private String prefix;
    private int prefixLength;
    private String separator = ":";

    public RuleMatcher(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("prefix cannot be null");
        }

        if ("".equals(prefix.trim())) {
            throw new IllegalArgumentException("prefix cannot be blank");
        }

        if (!prefix.endsWith(separator)) {
            this.prefix = prefix + separator;
        } else {
            this.prefix = prefix;
        }

        prefixLength = this.prefix.length();
    }

    public boolean matches(String text) {
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null");
        }

        return text.startsWith(prefix);
    }

    public String getValue(String text) {
        return text.substring(prefixLength);
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSeparator() {
        return separator;
    }
}
