package com.mossle.api.dict;

import java.util.LinkedHashMap;
import java.util.Map;

public class DictDTO {
    private String name;
    private String value;
    private String type;
    private Map<String, DictDataDTO> data = new LinkedHashMap<String, DictDataDTO>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, DictDataDTO> getData() {
        return data;
    }

    public void setData(Map<String, DictDataDTO> data) {
        this.data = data;
    }

    public void addData(String name, DictDataDTO value) {
        data.put(name, value);
    }

    // ~
    public boolean getBoolean() {
        return Boolean.parseBoolean(value);
    }

    public int getInt() {
        return Integer.parseInt(value);
    }

    public double getDouble() {
        return Double.parseDouble(value);
    }

    // ~
    public boolean getBoolean(String dataName) {
        return data.get(dataName).getBoolean();
    }

    public int getInt(String dataName) {
        return data.get(dataName).getInt();
    }

    public double getDouble(String dataName) {
        return data.get(dataName).getDouble();
    }
}
