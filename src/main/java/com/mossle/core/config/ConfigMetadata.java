package com.mossle.core.config;

// properties, yaml, json
// application, cluster, environment
// /opt/data/config/application/namespace-cluster-envrionment.properties
public class ConfigMetadata {
    private String application = "application";
    private String cluster = "default";

    // local, dev, test, stage, prod
    private String environment = "test";

    // application
    private String namespace = "application";

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
