package com.mossle.asset.persistence.manager;

import com.mossle.asset.persistence.domain.AssetRequestItem;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class AssetRequestItemManager extends
        HibernateEntityDao<AssetRequestItem> {
}
