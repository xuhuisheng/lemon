package com.mossle.acl.manager;

import com.mossle.acl.domain.AclEntry;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class AclEntryManager extends HibernateEntityDao<AclEntry> {
}
