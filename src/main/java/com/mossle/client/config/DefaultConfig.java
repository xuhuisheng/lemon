package com.mossle.client.config;

import java.io.StringReader;

import java.text.SimpleDateFormat;

import java.time.Duration;

import java.util.Date;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class DefaultConfig implements Config {
    private String content;
    private int version;
    private Properties properties = new Properties();

    public DefaultConfig() {
    }

    public DefaultConfig(String content) {
        this.content = content;
        this.properties = new Properties();

        try {
            properties.load(new StringReader(content));
        } catch (Exception ex) {
        }
    }

    public String getProperty(String key) {
        return getProperty(key, null);
    }

    public String getProperty(String key, String defaultValue) {
        String value = properties.getProperty(key);

        if (value != null) {
            return value;
        }

        return defaultValue;
    }

    public Integer getIntProperty(String key) {
        return getIntProperty(key, null);
    }

    public Integer getIntProperty(String key, Integer defaultValue) {
        String value = properties.getProperty(key);

        if (value != null) {
            try {
                return Integer.parseInt(value, 10);
            } catch (Exception ex) {
            }
        }

        return defaultValue;
    }

    public Long getLongProperty(String key) {
        return getLongProperty(key, null);
    }

    public Long getLongProperty(String key, Long defaultValue) {
        String value = properties.getProperty(key);

        if (value != null) {
            try {
                return Long.parseLong(value);
            } catch (Exception ex) {
            }
        }

        return defaultValue;
    }

    public Short getShortProperty(String key) {
        return getShortProperty(key, null);
    }

    public Short getShortProperty(String key, Short defaultValue) {
        String value = properties.getProperty(key);

        if (value != null) {
            try {
                return Short.parseShort(value);
            } catch (Exception ex) {
            }
        }

        return defaultValue;
    }

    public Float getFloatProperty(String key) {
        return getFloatProperty(key, null);
    }

    public Float getFloatProperty(String key, Float defaultValue) {
        String value = properties.getProperty(key);

        if (value != null) {
            try {
                return Float.parseFloat(value);
            } catch (Exception ex) {
            }
        }

        return defaultValue;
    }

    public Double getDoubleProperty(String key) {
        return getDoubleProperty(key, null);
    }

    public Double getDoubleProperty(String key, Double defaultValue) {
        String value = properties.getProperty(key);

        if (value != null) {
            try {
                return Double.parseDouble(value);
            } catch (Exception ex) {
            }
        }

        return defaultValue;
    }

    public Byte getByteProperty(String key) {
        return getByteProperty(key, null);
    }

    public Byte getByteProperty(String key, Byte defaultValue) {
        String value = properties.getProperty(key);

        if (value != null) {
            try {
                return Byte.parseByte(value);
            } catch (Exception ex) {
            }
        }

        return defaultValue;
    }

    public Boolean getBooleanProperty(String key) {
        return getBooleanProperty(key, null);
    }

    public Boolean getBooleanProperty(String key, Boolean defaultValue) {
        String value = properties.getProperty(key);

        if (value != null) {
            try {
                return Boolean.valueOf(value);
            } catch (Exception ex) {
            }
        }

        return defaultValue;
    }

    public String[] getArrayProperty(String key, String delimiter) {
        return getArrayProperty(key, delimiter, null);
    }

    public String[] getArrayProperty(String key, String delimiter,
            String[] defaultValue) {
        String value = properties.getProperty(key);

        if (value != null) {
            return StringUtils.split(value, delimiter);
        }

        return defaultValue;
    }

    public Date getDateProperty(String key) {
        return getDateProperty(key, null);
    }

    public Date getDateProperty(String key, Date defaultValue) {
        return getDateProperty(key, "yyyy-MM-dd HH:mm:ss", null);
    }

    public Date getDateProperty(String key, String format, Date defaultValue) {
        String value = properties.getProperty(key);

        if (value != null) {
            try {
                return new SimpleDateFormat(format).parse(value);
            } catch (Exception ex) {
            }
        }

        return defaultValue;
    }

    public <T extends Enum<T>> T getEnumProperty(String key, Class<T> enumType) {
        return getEnumProperty(key, enumType, null);
    }

    public <T extends Enum<T>> T getEnumProperty(String key, Class<T> enumType,
            T defaultValue) {
        String value = properties.getProperty(key);

        if (value != null) {
            try {
                return Enum.valueOf(enumType, value);
            } catch (Exception ex) {
            }
        }

        return defaultValue;
    }

    public Duration getDurationProperty(String key) {
        return getDurationProperty(key, null);
    }

    public Duration getDurationProperty(String key, Duration defaultValue) {
        String value = properties.getProperty(key);

        if (value != null) {
            try {
                return Duration.parse(value);
            } catch (Exception ex) {
            }
        }

        return defaultValue;
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
        return properties.stringPropertyNames();
    }

    public boolean exists(String key) {
        return properties.containsKey(key);
    }

    public String getContent() {
        return content;
    }

    public int getVersion() {
        return version;
    }

    //
    public void setContent(String content) {
        this.content = content;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
