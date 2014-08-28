package com.mossle.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * reflect utils.
 * 
 * @author Lingo
 */
public class ReflectUtils {
    /** logger. */
    private static Logger logger = LoggerFactory.getLogger(ReflectUtils.class);

    /** protected constructor. */
    protected ReflectUtils() {
    }

    public static String getGetterMethodName(Object target, String fieldName)
            throws NoSuchMethodException {
        String methodName = "get" + StringUtils.capitalize(fieldName);

        try {
            target.getClass().getDeclaredMethod(methodName);
        } catch (NoSuchMethodException ex) {
            logger.trace(ex.getMessage(), ex);
            methodName = "is" + StringUtils.capitalize(fieldName);

            target.getClass().getDeclaredMethod(methodName);
        }

        return methodName;
    }

    public static String getSetterMethodName(String fieldName) {
        return "set" + StringUtils.capitalize(fieldName);
    }

    /**
     * get method value by name.
     * 
     * @param target
     *            Object
     * @param methodName
     *            method name
     * @return object
     * @throws Exception
     *             ex
     */
    public static Object getMethodValue(Object target, String methodName)
            throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        Method method = target.getClass().getDeclaredMethod(methodName);

        return method.invoke(target);
    }

    /**
     * set method value by name.
     * 
     * @param target
     *            Object
     * @param methodName
     *            method name
     * @param methodValue
     *            method value
     * @throws Exception
     *             ex
     */
    public static void setMethodValue(Object target, String methodName,
            Object methodValue) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        Method method = target.getClass().getDeclaredMethod(methodName,
                methodValue.getClass());

        method.invoke(target, methodValue);
    }

    public static Object getFieldValue(Object target, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        return getFieldValue(target, fieldName, true);
    }

    public static Object getFieldValue(Object target, String fieldName,
            boolean isForce) throws NoSuchFieldException,
            IllegalAccessException {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(isForce);

        return field.get(target);
    }

    /**
     * convert reflection exception to unchecked.
     * 
     * @param e
     *            Exception
     * @return RuntimeException;
     */
    public static RuntimeException convertReflectionExceptionToUnchecked(
            Exception e) {
        if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        } else if (e instanceof IllegalAccessException
                || e instanceof NoSuchMethodException
                || e instanceof NoSuchFieldException) {
            return new IllegalArgumentException("Reflection Exception.", e);
        } else if (e instanceof InvocationTargetException) {
            Throwable targetException = ((InvocationTargetException) e)
                    .getTargetException();

            if (targetException instanceof RuntimeException) {
                return (RuntimeException) targetException;
            } else {
                return new RuntimeException("Reflection Exception.",
                        targetException);
            }
        }

        return new RuntimeException("Unexpected Checked Exception.", e);
    }

    public static Class<?> getOriginalClass(Class<?> clz) {
        Class<?> superclass = clz;

        while (superclass.getName().indexOf("_$$_jvst") != -1) {
            superclass = superclass.getSuperclass();

            if (superclass == null) {
                return superclass;
            }
        }

        return superclass;
    }
}
