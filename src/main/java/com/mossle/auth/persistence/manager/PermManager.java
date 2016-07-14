package com.mossle.auth.persistence.manager;

import com.mossle.auth.persistence.domain.Perm;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class PermManager extends HibernateEntityDao<Perm> {
}
