package com.mossle.sale.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.sale.persistence.domain.SaleInfo;

import org.springframework.stereotype.Service;

@Service
public class SaleInfoManager extends HibernateEntityDao<SaleInfo> {
}
