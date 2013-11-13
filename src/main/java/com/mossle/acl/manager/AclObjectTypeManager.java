package com.mossle.acl.manager;

import com.mossle.acl.domain.AclObjectType;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class AclObjectTypeManager extends HibernateEntityDao<AclObjectType> {
}
