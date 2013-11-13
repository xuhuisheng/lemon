package com.mossle.core.hibernate;

import org.springframework.context.ApplicationEvent;

public class EntityEvent extends ApplicationEvent {
    private static final long serialVersionUID = 0L;
    private Object entity;

    public EntityEvent(Object entity) {
        super(entity);
        this.entity = entity;
    }

    public <T> T getEntity() {
        return (T) entity;
    }

    public boolean supportsEntityType(Class<?> clz) {
        return entity.getClass().isAssignableFrom(clz);
    }

    public boolean isCreated() {
        return false;
    }

    public boolean isUpdated() {
        return false;
    }

    public boolean isRemoved() {
        return false;
    }
}
