package com.mossle.disk.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.disk.persistence.domain.DiskSpace;

import org.springframework.stereotype.Service;

@Service
public class DiskSpaceManager extends HibernateEntityDao<DiskSpace> {
}
