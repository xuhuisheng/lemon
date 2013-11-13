package com.mossle.core.hibernate;

public class EntityCreatedEvent extends EntityEvent {
    private static final long serialVersionUID = 0L;

    public EntityCreatedEvent(Object entity) {
        super(entity);
    }

    public boolean isCreated() {
        return true;
    }
}
