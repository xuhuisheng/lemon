package com.mossle.disk.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.disk.persistence.domain.DiskInfo;

import org.springframework.stereotype.Service;

@Service
public class DiskInfoManager extends HibernateEntityDao<DiskInfo> {
}
