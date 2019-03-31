package com.mossle.core.config;

// properties, yaml, json
public class ConfigClient {
    private String name = "application";

    // local, dev, test, stage, prod
    private String env = "local";
    private String configInitPath = "classpath:application.properties";
    private String configServerUrl = "http://localhost:8000/config";
    private String configLocalPath = "classpath:application.properties";
    private String configCachePath = "/opt/";
}
