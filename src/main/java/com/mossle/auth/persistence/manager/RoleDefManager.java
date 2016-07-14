package com.mossle.auth.persistence.manager;

import com.mossle.auth.persistence.domain.RoleDef;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class RoleDefManager extends HibernateEntityDao<RoleDef> {
}
