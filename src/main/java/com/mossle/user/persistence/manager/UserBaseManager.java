package com.mossle.user.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.user.persistence.domain.UserBase;

import org.springframework.stereotype.Repository;

@Repository
public class UserBaseManager extends HibernateEntityDao<UserBase> {
}
