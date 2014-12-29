package com.mossle.audit.manager;

import com.mossle.audit.domain.AuditBase;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class AuditBaseManager extends HibernateEntityDao<AuditBase> {
}
