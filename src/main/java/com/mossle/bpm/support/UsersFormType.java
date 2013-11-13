package com.mossle.bpm.support;

import java.util.Arrays;

import org.activiti.engine.form.AbstractFormType;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 用户表单字段类型
 * 
 * @author henryyan
 */
public class UsersFormType extends AbstractFormType {
    @Override
    public String getName() {
        return "users";
    }

    @Override
    public Object convertFormValueToModelValue(String propertyValue) {
        String[] split = StringUtils.split(propertyValue, ",");

        return Arrays.asList(split);
    }

    @Override
    public String convertModelValueToFormValue(Object modelValue) {
        return ObjectUtils.toString(modelValue);
    }
}
