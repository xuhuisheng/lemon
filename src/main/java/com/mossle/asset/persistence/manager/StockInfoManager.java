package com.mossle.asset.persistence.manager;

import com.mossle.asset.persistence.domain.StockInfo;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class StockInfoManager extends HibernateEntityDao<StockInfo> {
}
