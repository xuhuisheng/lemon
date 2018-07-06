package com.mossle.core.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import com.mossle.core.util.ConvertUtils;
import com.mossle.core.util.ServletUtils;

import org.apache.commons.lang3.StringUtils;

import org.springframework.util.Assert;

/**
 * property filter.
 * 
 * @author Lingo
 */
public class PropertyFilter {
    /** or seperator. */
    public static final String OR_SEPARATOR = "_OR_";

    /** match type. */
    private MatchType matchType;

    /** match value. */
    private Object matchValue;

    /** property class. */
    private Class<?> propertyClass;

    /** property names. */
    private String[] propertyNames;

    /**
     * default constructor.
     */
    public PropertyFilter() {
    }

    /**
     * constructor.
     * 
     * @param filterName
     *            String
     * @param value
     *            String
     */
    public PropertyFilter(final String filterName, final String value) {
        String firstPart = StringUtils.substringBefore(filterName, "_");
        String matchTypeCode = StringUtils.substring(firstPart, 0,
                firstPart.length() - 1);
        String propertyTypeCode = StringUtils.substring(firstPart,
                firstPart.length() - 1, firstPart.length());

        try {
            matchType = Enum.valueOf(MatchType.class, matchTypeCode);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("filter名称" + filterName
                    + "没有按规则编写,无法得到属性比较类型.", e);
        }

        try {
            propertyClass = Enum.valueOf(PropertyType.class, propertyTypeCode)
                    .getValue();
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("filter名称" + filterName
                    + "没有按规则编写,无法得到属性值类型.", e);
        }

        String propertyNameStr = StringUtils.substringAfter(filterName, "_");
        Assert.isTrue(StringUtils.isNotBlank(propertyNameStr), "filter名称"
                + filterName + "没有按规则编写,无法得到属性名称.");
        propertyNames = StringUtils.splitByWholeSeparator(propertyNameStr,
                PropertyFilter.OR_SEPARATOR);

        if (matchType == MatchType.IN) {
            this.matchValue = convertStringToCollection(value, propertyClass);
        } else {
            this.matchValue = ConvertUtils.convertStringToObject(value,
                    propertyClass);
        }
    }

    private <T> Collection<T> convertStringToCollection(String text,
            Class<T> propertyClass) {
        List<T> list = new ArrayList<T>();

        for (String value : StringUtils.split(text, " ,;")) {
            list.add((T) ConvertUtils.convertStringToObject(value,
                    propertyClass));
        }

        return list;
    }

    /**
     * build from request.
     * 
     * @param request
     *            HttpServletRequest
     * @return list
     */
    public static List<PropertyFilter> buildFromHttpRequest(
            final HttpServletRequest request) {
        return buildFromHttpRequest(request, "filter_");
    }

    /**
     * build from request.
     * 
     * @param request
     *            HttpServletRequest
     * @param filterPrefix
     *            String
     * @return list
     */
    public static List<PropertyFilter> buildFromHttpRequest(
            final HttpServletRequest request, final String filterPrefix) {
        // 从request中获取含属性前缀名的参数,构造去除前缀名后的参数Map.
        Map<String, Object> filterParamMap = ServletUtils
                .getParametersStartingWith(request, filterPrefix);

        return build(filterParamMap);
    }

    public static List<PropertyFilter> buildFromMap(
            Map<String, Object> parameterMap) {
        return buildFromMap(parameterMap, "filter_");
    }

    public static List<PropertyFilter> buildFromMap(
            Map<String, Object> parameterMap, String filterPrefix) {
        Map<String, Object> filterParamMap = new TreeMap<String, Object>();

        for (Map.Entry<String, Object> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (key.startsWith(filterPrefix)) {
                filterParamMap.put(key.substring(filterPrefix.length()), value);
            }
        }

        return build(filterParamMap);
    }

    public static List<PropertyFilter> build(Map<String, Object> filterParamMap) {
        List<PropertyFilter> filterList = new ArrayList<PropertyFilter>();

        // 分析参数Map,构造PropertyFilter列表
        for (Map.Entry<String, Object> entry : filterParamMap.entrySet()) {
            String filterName = entry.getKey();
            Object filterValue = entry.getValue();
            String value = null;

            if (filterValue instanceof String[]) {
                value = ((String[]) filterValue)[0];
            } else {
                value = (String) filterValue;
            }

            // 如果value值为空,则忽略此filter.
            if (StringUtils.isNotBlank(value)) {
                PropertyFilter filter = new PropertyFilter(filterName, value);
                filterList.add(filter);
            }
        }

        return filterList;
    }

    /** @return property class. */
    public Class<?> getPropertyClass() {
        return propertyClass;
    }

    /** @return MatchType. */
    public MatchType getMatchType() {
        return matchType;
    }

    /** @return MatchValue. */
    public Object getMatchValue() {
        return matchValue;
    }

    /** @return property names. */
    public String[] getPropertyNames() {
        return propertyNames;
    }

    /** @return property name. */
    public String getPropertyName() {
        Assert.isTrue(propertyNames.length == 1,
                "There are not only one property in this filter.");

        return propertyNames[0];
    }

    /** @return has multi properties. */
    public boolean hasMultiProperties() {
        return (propertyNames.length > 1);
    }
}
