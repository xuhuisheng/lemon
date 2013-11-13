package com.mossle.core.util;

import java.lang.reflect.Method;

import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesUtils {
    private static Logger logger = LoggerFactory
            .getLogger(PropertiesUtils.class);

    protected PropertiesUtils() {
    }

    public static void bindProperties(Object object, Properties properties,
            String prefix) {
        if (properties == null) {
            throw new IllegalArgumentException(
                    "there is no properties setting.");
        }

        logger.debug("prefix : {}", prefix);

        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();

            if (!key.startsWith(prefix)) {
                continue;
            }

            String propertyName = key.substring(prefix.length());

            tryToSetProperty(object, propertyName, value);
        }
    }

    public static void tryToSetProperty(Object object, String propertyName,
            String propertyValue) {
        String setterName = ReflectUtils.getSetterMethodName(propertyName);
        Method[] methods = object.getClass().getMethods();

        for (Method method : methods) {
            if (!method.getName().equals(setterName)) {
                continue;
            }

            Class[] parameterTypes = method.getParameterTypes();

            if (parameterTypes.length != 1) {
                continue;
            }

            invokeMethod(object, method, parameterTypes[0], propertyValue);
        }
    }

    private static void invokeMethod(Object object, Method method,
            Class parameterType, String propertyValue) {
        logger.debug("match method : {}, {}", method, propertyValue);

        if (parameterType == String.class) {
            BeanUtils.safeInvokeMethod(object, method, propertyValue);
        } else if ((parameterType == Integer.class)
                || (parameterType == int.class)) {
            BeanUtils.safeInvokeMethod(object, method,
                    Integer.parseInt(propertyValue));
        } else if ((parameterType == Long.class)
                || (parameterType == long.class)) {
            BeanUtils.safeInvokeMethod(object, method,
                    Long.parseLong(propertyValue));
        } else if ((parameterType == Boolean.class)
                || (parameterType == boolean.class)) {
            BeanUtils.safeInvokeMethod(object, method,
                    Boolean.valueOf(propertyValue));
        } else {
            logger.info("cannot process parameterType : [" + parameterType
                    + "]");
        }
    }
}
