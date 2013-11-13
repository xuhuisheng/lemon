package com.mossle.core.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generics的util类. 来自www.springside.org.cn
 * 
 * @author sshwsfc
 * @since 2007-03-14
 * @version 1.0
 */
public class GenericsUtils {
    /**
     * 日志.
     */
    private static Logger logger = LoggerFactory.getLogger(GenericsUtils.class);

    /**
     * 构造方法.
     */
    protected GenericsUtils() {
    }

    /**
     * 通过反射,获得定义Class时声明的父类的范型参数的类型. 如public BookManager extends GenricManager&lt;Book&gt;
     * 
     * @param clazz
     *            The class to introspect
     * @return the first generic declaration, or <code>Object.class</code> if cannot be determined
     */
    public static Class getSuperClassGenericType(Class clazz) {
        return getSuperClassGenericType(clazz, 0);
    }

    /**
     * 通过反射,获得定义Class时声明的父类的范型参数的类型. 如public BookManager extends GenricManager&lt;Book&gt;
     * 
     * @param clazz
     *            clazz The class to introspect
     * @param index
     *            the Index of the generic ddeclaration,start from 0.
     * @return the index generic declaration, or <code>Object.class</code> if cannot be determined
     */
    public static Class getSuperClassGenericType(Class clazz, int index) {
        Type genType = clazz.getGenericSuperclass();

        if (clazz.getSimpleName().indexOf("$$EnhancerByCGLIB$$") != -1) {
            genType = ((Class) genType).getGenericSuperclass();
        }

        if (!(genType instanceof ParameterizedType)) {
            logger.warn("{}'s superclass not ParameterizedType",
                    clazz.getSimpleName());

            return Object.class;
        }

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if ((index >= params.length) || (index < 0)) {
            logger.warn(
                    "Index: {}, Size of {}'s Parameterized Type: {}",
                    new Object[] { index, clazz.getSimpleName(), params.length });

            return Object.class;
        }

        if (!(params[index] instanceof Class)) {
            logger.warn(
                    "{} not set the actual class on superclass generic parameter",
                    clazz.getSimpleName());

            return Object.class;
        }

        return (Class) params[index];
    }
}
