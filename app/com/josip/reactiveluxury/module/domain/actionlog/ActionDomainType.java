package com.josip.reactiveluxury.module.domain.actionlog;

import com.josip.reactiveluxury.core.Asserts;
import com.josip.reactiveluxury.module.domain.user.User;

public enum ActionDomainType {
    USER(User.class);

    private <T> ActionDomainType(Class<T> entityClass) {
        this.entityClass = entityClass;
    }
    private final Class<?> entityClass;

    public Class<?> getEntityClass() {
        return this.entityClass;
    }

    public static ActionDomainType getByEntityClass(Class<?> entityClass) {
        Asserts.argumentIsNotNull(entityClass);

        for(ActionDomainType at: ActionDomainType.values()) {
            if(at.getEntityClass().equals(entityClass)) {
                return at;
            }
        }

        throw new IllegalArgumentException(String.format("There is not ActionDomainType with provided entityClass: '%s'", entityClass.getName()));
    }
}
