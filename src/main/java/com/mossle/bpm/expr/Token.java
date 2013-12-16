package com.mossle.bpm.expr;

public class Token {
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isOper() {
        return false;
    }

    public String toString() {
        return value;
    }
}
