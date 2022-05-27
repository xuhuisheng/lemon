package com.mossle.user.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.user.persistence.domain.UserAttr;

import org.springframework.stereotype.Repository;

@Repository
public class UserAttrManager extends HibernateEntityDao<UserAttr> {
}
