package com.mossle.user.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.user.persistence.domain.UserAttr;

import org.springframework.stereotype.Service;

@Service
public class UserAttrManager extends HibernateEntityDao<UserAttr> {
}
