package com.mossle.core.mapper;

import org.dozer.DozerBeanMapperSingletonWrapper;
import org.dozer.Mapper;

public class BeanMapper {
    private static Mapper mapper;

    static {
        mapper = DozerBeanMapperSingletonWrapper.getInstance();
    }

    public void copy(Object src, Object dest) {
        mapper.map(src, dest);
    }
}
