package com.mossle.api.template;

import java.util.HashMap;
import java.util.Map;

public class TemplateDTO {
    private String code;
    private String name;
    private Map<String, String> fields = new HashMap<String, String>();

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    public String getField(String fieldName) {
        return fields.get(fieldName);
    }
}
