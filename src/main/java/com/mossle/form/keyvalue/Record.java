package com.mossle.form.keyvalue;

import java.util.LinkedHashMap;
import java.util.Map;

public class Record {
    private String code;
    private String category;
    private int status;
    private String ref;
    private Map<String, Prop> props = new LinkedHashMap<String, Prop>();

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public Map<String, Prop> getProps() {
        return props;
    }

    public void setProps(Map<String, Prop> props) {
        this.props = props;
    }
}
