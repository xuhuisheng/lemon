package com.mossle.audit.persistence.manager;

import com.mossle.audit.persistence.domain.AuditBase;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class AuditBaseManager extends HibernateEntityDao<AuditBase> {
}
