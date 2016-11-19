package com.mossle.inventory.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.inventory.persistence.domain.InventoryInfo;

import org.springframework.stereotype.Service;

@Service
public class InventoryInfoManager extends HibernateEntityDao<InventoryInfo> {
}
