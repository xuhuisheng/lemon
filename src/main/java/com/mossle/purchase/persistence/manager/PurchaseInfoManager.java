package com.mossle.purchase.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.purchase.persistence.domain.PurchaseInfo;

import org.springframework.stereotype.Service;

@Service
public class PurchaseInfoManager extends HibernateEntityDao<PurchaseInfo> {
}
