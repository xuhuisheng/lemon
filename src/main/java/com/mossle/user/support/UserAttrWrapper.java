package com.mossle.user.support;

import com.mossle.user.persistence.domain.UserAttr;
import com.mossle.user.persistence.domain.UserSchema;

public class UserAttrWrapper {
    private UserAttr userAttr;
    private UserSchema userSchema;

    public UserAttrWrapper(UserAttr userAttr) {
        this.userAttr = userAttr;
        this.userSchema = userAttr.getUserSchema();
    }

    public UserAttrWrapper(UserSchema userSchema) {
        this.userSchema = userSchema;
    }

    public String getCode() {
        return userSchema.getCode();
    }

    public String getName() {
        return userSchema.getName();
    }

    public Object getValue() {
        if (userAttr == null) {
            return null;
        }

        String type = userSchema.getType();

        if ("boolean".equals(type)) {
            return userAttr.getBooleanValue();
        } else if ("date".equals(type)) {
            return userAttr.getDateValue();
        } else if ("long".equals(type)) {
            return userAttr.getLongValue();
        } else if ("double".equals(type)) {
            return userAttr.getDoubleValue();
        } else if ("string".equals(type)) {
            return userAttr.getStringValue();
        } else {
            throw new IllegalStateException("illegal type: "
                    + userSchema.getType());
        }
    }
}
