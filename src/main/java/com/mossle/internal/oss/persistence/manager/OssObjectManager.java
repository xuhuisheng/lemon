package com.mossle.internal.oss.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.internal.oss.persistence.domain.OssObject;

import org.springframework.stereotype.Service;

@Service
public class OssObjectManager extends HibernateEntityDao<OssObject> {
}
