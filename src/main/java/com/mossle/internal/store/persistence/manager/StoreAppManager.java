package com.mossle.internal.store.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.internal.store.persistence.domain.StoreApp;

import org.springframework.stereotype.Service;

@Service
public class StoreAppManager extends HibernateEntityDao<StoreApp> {
}
