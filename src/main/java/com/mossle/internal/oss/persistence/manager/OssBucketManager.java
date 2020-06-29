package com.mossle.internal.oss.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.internal.oss.persistence.domain.OssBucket;

import org.springframework.stereotype.Service;

@Service
public class OssBucketManager extends HibernateEntityDao<OssBucket> {
}
