package com.mossle.api.avatar;

import javax.activation.DataSource;

public class AvatarDTO {
    /** userId. */
    private String userId;

    /** 类型，默认default. */
    private String type;

    /** 附件key. */
    private String code;

    /** 附件. */
    private DataSource dataSource;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
