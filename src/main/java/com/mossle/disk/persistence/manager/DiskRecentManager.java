package com.mossle.disk.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.disk.persistence.domain.DiskRecent;

import org.springframework.stereotype.Service;

@Service
public class DiskRecentManager extends HibernateEntityDao<DiskRecent> {
}
