package com.mossle.asset.persistence.manager;

import com.mossle.asset.persistence.domain.AssetInfo;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class AssetInfoManager extends HibernateEntityDao<AssetInfo> {
}
