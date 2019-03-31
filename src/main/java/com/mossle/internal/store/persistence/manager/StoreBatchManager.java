package com.mossle.internal.store.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.internal.store.persistence.domain.StoreBatch;

import org.springframework.stereotype.Service;

@Service
public class StoreBatchManager extends HibernateEntityDao<StoreBatch> {
}
