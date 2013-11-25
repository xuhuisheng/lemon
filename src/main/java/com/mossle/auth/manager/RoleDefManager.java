package com.mossle.auth.manager;

import com.mossle.auth.domain.RoleDef;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class RoleDefManager extends HibernateEntityDao<RoleDef> {
}
