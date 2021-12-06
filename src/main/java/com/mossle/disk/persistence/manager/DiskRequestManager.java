package com.mossle.disk.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.disk.persistence.domain.DiskRequest;

import org.springframework.stereotype.Service;

@Service
public class DiskRequestManager extends HibernateEntityDao<DiskRequest> {
}
