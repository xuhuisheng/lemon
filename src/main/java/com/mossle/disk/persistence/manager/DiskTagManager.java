package com.mossle.disk.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.disk.persistence.domain.DiskTag;

import org.springframework.stereotype.Service;

@Service
public class DiskTagManager extends HibernateEntityDao<DiskTag> {
}
