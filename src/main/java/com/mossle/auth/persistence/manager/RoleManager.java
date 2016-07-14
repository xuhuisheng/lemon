package com.mossle.auth.persistence.manager;

import com.mossle.auth.persistence.domain.Role;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class RoleManager extends HibernateEntityDao<Role> {
}
