package com.mossle.asset.persistence.manager;

import com.mossle.asset.persistence.domain.AssetRequest;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class AssetRequestManager extends HibernateEntityDao<AssetRequest> {
}
