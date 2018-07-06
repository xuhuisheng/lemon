package com.mossle.asset.persistence.manager;

import com.mossle.asset.persistence.domain.AssetHistory;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class AssetHistoryManager extends HibernateEntityDao<AssetHistory> {
}
