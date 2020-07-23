package com.mossle.asset.persistence.manager;

import com.mossle.asset.persistence.domain.AssetRequestInfo;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class AssetRequestInfoManager extends
        HibernateEntityDao<AssetRequestInfo> {
}
