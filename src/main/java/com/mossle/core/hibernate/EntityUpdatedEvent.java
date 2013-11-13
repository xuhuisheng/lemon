package com.mossle.core.hibernate;

public class EntityUpdatedEvent extends EntityEvent {
    private static final long serialVersionUID = 0L;

    public EntityUpdatedEvent(Object entity) {
        super(entity);
    }

    public boolean isUpdated() {
        return true;
    }
}
