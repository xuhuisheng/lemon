package com.mossle.asset.persistence.manager;

import com.mossle.asset.persistence.domain.StockItem;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class StockItemManager extends HibernateEntityDao<StockItem> {
}
