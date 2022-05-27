package com.mossle.spi.device;

public class UserAgentDTO {
    private String type = "unknown";
    private String os = "unknown";
    private String client = "unknown";

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }
}
