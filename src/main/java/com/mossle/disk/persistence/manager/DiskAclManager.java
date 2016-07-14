package com.mossle.disk.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.disk.persistence.domain.DiskAcl;

import org.springframework.stereotype.Service;

@Service
public class DiskAclManager extends HibernateEntityDao<DiskAcl> {
}
