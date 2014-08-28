package com.mossle.ext.store;

import java.io.InputStream;

import org.springframework.core.io.Resource;

public class StoreDTO {
    private String model;
    private String key;
    private Resource resource;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }
}
