package com.mossle.asset.persistence.manager;

import com.mossle.asset.persistence.domain.SkuInfo;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class SkuInfoManager extends HibernateEntityDao<SkuInfo> {
}
