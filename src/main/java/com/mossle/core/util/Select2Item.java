package com.mossle.core.util;

public class Select2Item {
    private String id;
    private String text;

    public Select2Item() {
    }

    public Select2Item(String id, String text) {
        this.id = id;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
