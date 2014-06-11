package com.mossle.form.engine.model;

import java.util.ArrayList;
import java.util.List;

public class FormModel {
    private String id;
    private String name;
    private List<FieldModel> fieldModels = new ArrayList<FieldModel>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FieldModel> getFieldModels() {
        return fieldModels;
    }

    public void setFieldModels(List<FieldModel> fieldModels) {
        this.fieldModels = fieldModels;
    }
}
