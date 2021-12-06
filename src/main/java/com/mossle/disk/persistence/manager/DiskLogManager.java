package com.mossle.disk.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.disk.persistence.domain.DiskLog;

import org.springframework.stereotype.Service;

@Service
public class DiskLogManager extends HibernateEntityDao<DiskLog> {
}
