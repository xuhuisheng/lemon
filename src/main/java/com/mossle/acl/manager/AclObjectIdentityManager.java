package com.mossle.acl.manager;

import com.mossle.acl.domain.AclObjectIdentity;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class AclObjectIdentityManager extends
        HibernateEntityDao<AclObjectIdentity> {
}
