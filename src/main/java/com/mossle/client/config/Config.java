package com.mossle.client.config;

import java.time.Duration;

import java.util.Date;
import java.util.Set;

public interface Config {
    String getProperty(String key);

    String getProperty(String key, String defaultValue);

    Integer getIntProperty(String key);

    Integer getIntProperty(String key, Integer defaultValue);

    Long getLongProperty(String key);

    Long getLongProperty(String key, Long defaultValue);

    Short getShortProperty(String key);

    Short getShortProperty(String key, Short defaultValue);

    Float getFloatProperty(String key);

    Float getFloatProperty(String key, Float defaultValue);

    Double getDoubleProperty(String key);

    Double getDoubleProperty(String key, Double defaultValue);

    Byte getByteProperty(String key);

    Byte getByteProperty(String key, Byte defaultValue);

    Boolean getBooleanProperty(String key);

    Boolean getBooleanProperty(String key, Boolean defaultValue);

    String[] getArrayProperty(String key, String delimiter);

    String[] getArrayProperty(String key, String delimiter,
            String[] defaultValue);

    Date getDateProperty(String key);

    Date getDateProperty(String key, Date defaultValue);

    Date getDateProperty(String key, String format, Date defaultValue);

    <T extends Enum<T>> T getEnumProperty(String key, Class<T> enumType,
            T defaultValue);

    <T extends Enum<T>> T getEnumProperty(String key, Class<T> enumType);

    Duration getDurationProperty(String key);

    Duration getDurationProperty(String key, Duration defaultValue);

    void addChangeListener(ConfigChangeListener listener);

    void addChangeListener(ConfigChangeListener listener,
            Set<String> interestedKeys);

    void addChangeListener(ConfigChangeListener listener,
            Set<String> interestedKeys, Set<String> interestedKeyPrefixes);

    boolean removeChangeListener(ConfigChangeListener listener);

    Set<String> getPropertyNames();

    boolean exists(String key);

    // <T> T getProperty(String key, Function<String, T> function, T defaultValue);
    // ConfigSourceType getSourceType();
    String getContent();

    int getVersion();
}
