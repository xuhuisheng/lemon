package com.mossle.core.id;

public interface IdGenerator {
    Long generateId();

    Long generateId(String name);

    Long generateId(Class<?> clz);
}
