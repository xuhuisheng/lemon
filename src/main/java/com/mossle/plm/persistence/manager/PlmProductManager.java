package com.mossle.plm.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.plm.persistence.domain.PlmProduct;

import org.springframework.stereotype.Service;

@Service
public class PlmProductManager extends HibernateEntityDao<PlmProduct> {
}
