package com.mossle.product.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.product.persistence.domain.ProductInfo;

import org.springframework.stereotype.Service;

@Service
public class ProductInfoManager extends HibernateEntityDao<ProductInfo> {
}
