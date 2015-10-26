package com.mossle.auth.persistence.manager;

import com.mossle.auth.persistence.domain.PermType;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class PermTypeManager extends HibernateEntityDao<PermType> {
}
