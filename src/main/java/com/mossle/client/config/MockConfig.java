package com.mossle.client.config;

import java.time.Duration;

import java.util.Date;
import java.util.Set;

public class MockConfig implements Config {
    public String getProperty(String key) {
        return null;
    }

    public String getProperty(String key, String defaultValue) {
        return null;
    }

    public Integer getIntProperty(String key) {
        return null;
    }

    public Integer getIntProperty(String key, Integer defaultValue) {
        return null;
    }

    public Long getLongProperty(String key) {
        return null;
    }

    public Long getLongProperty(String key, Long defaultValue) {
        return null;
    }

    public Short getShortProperty(String key) {
        return null;
    }

    public Short getShortProperty(String key, Short defaultValue) {
        return null;
    }

    public Float getFloatProperty(String key) {
        return null;
    }

    public Float getFloatProperty(String key, Float defaultValue) {
        return null;
    }

    public Double getDoubleProperty(String key) {
        return null;
    }

    public Double getDoubleProperty(String key, Double defaultValue) {
        return null;
    }

    public Byte getByteProperty(String key) {
        return null;
    }

    public Byte getByteProperty(String key, Byte defaultValue) {
        return null;
    }

    public Boolean getBooleanProperty(String key) {
        return null;
    }

    public Boolean getBooleanProperty(String key, Boolean defaultValue) {
        return null;
    }

    public String[] getArrayProperty(String key, String delimiter) {
        return null;
    }

    public String[] getArrayProperty(String key, String delimiter,
            String[] defaultValue) {
        return null;
    }

    public Date getDateProperty(String key) {
        return null;
    }

    public Date getDateProperty(String key, Date defaultValue) {
        return null;
    }

    public Date getDateProperty(String key, String format, Date defaultValue) {
        return null;
    }

    public <T extends Enum<T>> T getEnumProperty(String key, Class<T> enumType) {
        return null;
    }

    public <T extends Enum<T>> T getEnumProperty(String key, Class<T> enumType,
            T defaultValue) {
        return null;
    }

    public Duration getDurationProperty(String key) {
        return null;
    }

    public Duration getDurationProperty(String key, Duration defaultValue) {
        return null;
    }

    public void addChangeListener(ConfigChangeListener listener) {
    }

    public void addChangeListener(ConfigChangeListener listener,
            Set<String> interestedKeys) {
    }

    public void addChangeListener(ConfigChangeListener listener,
            Set<String> interestedKeys, Set<String> interestedKeyPrefixes) {
    }

    public boolean removeChangeListener(ConfigChangeListener listener) {
        return false;
    }

    public Set<String> getPropertyNames() {
        return null;
    }

    public boolean exists(String key) {
        return false;
    }

    public String getContent() {
        return null;
    }

    public int getVersion() {
        return 0;
    }
}
