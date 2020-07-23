package com.mossle.internal.oss.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.internal.oss.persistence.domain.OssAccess;

import org.springframework.stereotype.Service;

@Service
public class OssAccessManager extends HibernateEntityDao<OssAccess> {
}
