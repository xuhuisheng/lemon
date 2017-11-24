package com.mossle.core.id;

public class NullIdGenerator implements IdGenerator {
    public Long generateId() {
        return null;
    }

    public Long generateId(String name) {
        return this.generateId();
    }

    public Long generateId(Class<?> clz) {
        return this.generateId();
    }
}
