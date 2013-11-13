package com.mossle.acl.manager;

import com.mossle.acl.domain.AclSid;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class AclSidManager extends HibernateEntityDao<AclSid> {
}
