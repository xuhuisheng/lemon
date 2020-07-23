package com.mossle.internal.open.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.internal.open.persistence.domain.SysEntry;

import org.springframework.stereotype.Service;

@Service
public class SysEntryManager extends HibernateEntityDao<SysEntry> {
}
