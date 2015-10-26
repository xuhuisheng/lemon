package com.mossle.api.keyvalue;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class Record {
    private String name;
    private String formTemplateCode;
    private String code;
    private String category;
    private int status;
    private String ref;
    private Date createTime;
    private String userId;
    private String tenantId;
    private Map<String, Prop> props = new LinkedHashMap<String, Prop>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormTemplateCode() {
        return formTemplateCode;
    }

    public void setFormTemplateCode(String formTemplateCode) {
        this.formTemplateCode = formTemplateCode;
    }

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Map<String, Prop> getProps() {
        return props;
    }

    public void setProps(Map<String, Prop> props) {
        this.props = props;
    }
}
