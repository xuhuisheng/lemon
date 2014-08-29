package com.mossle.api.whitelist;

import java.util.ArrayList;
import java.util.List;

public class WhitelistDTO {
    private String name;
    private String description;
    private List<String> hosts = new ArrayList<String>();
    private List<String> ips = new ArrayList<String>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getHosts() {
        return hosts;
    }

    public void setHosts(List<String> hosts) {
        this.hosts = hosts;
    }

    public List<String> getIps() {
        return ips;
    }

    public void setIps(List<String> ips) {
        this.ips = ips;
    }

    // ~ ==================================================
    public boolean notValidHost(String host) {
        return !hosts.contains(host);
    }

    public boolean notValidIp(String ip) {
        return !ips.contains(ip);
    }

    public void addHosts(List<String> list) {
        this.hosts.addAll(list);
    }

    public void addIps(List<String> list) {
        this.ips.addAll(list);
    }
}
