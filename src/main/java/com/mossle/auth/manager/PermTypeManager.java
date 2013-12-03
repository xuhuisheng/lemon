package com.mossle.auth.manager;

import com.mossle.auth.domain.PermType;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class PermTypeManager extends HibernateEntityDao<PermType> {
}
