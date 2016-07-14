package com.mossle.disk.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.disk.persistence.domain.DiskShare;

import org.springframework.stereotype.Service;

@Service
public class DiskShareManager extends HibernateEntityDao<DiskShare> {
}
