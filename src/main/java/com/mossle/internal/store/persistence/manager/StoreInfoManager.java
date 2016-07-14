package com.mossle.internal.store.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.internal.store.persistence.domain.StoreInfo;

import org.springframework.stereotype.Service;

@Service
public class StoreInfoManager extends HibernateEntityDao<StoreInfo> {
}
