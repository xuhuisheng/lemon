package com.mossle.user.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.user.persistence.domain.UserSchema;

import org.springframework.stereotype.Service;

@Service
public class UserSchemaManager extends HibernateEntityDao<UserSchema> {
}
