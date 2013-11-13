package com.mossle.core.logback;

import java.net.URL;

import ch.qos.logback.core.PropertyDefinerBase;
import ch.qos.logback.core.util.Loader;

public class ResourceExistsPropertyDefiner extends PropertyDefinerBase {
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPropertyValue() {
        if (path == null) {
            return "false";
        }

        URL resourceURL = Loader.getResourceBySelfClassLoader(path);

        return (resourceURL != null) ? "true" : "false";
    }
}
