package com.mossle.customer.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.customer.persistence.domain.CustomerInfo;

import org.springframework.stereotype.Service;

@Service
public class CustomerInfoManager extends HibernateEntityDao<CustomerInfo> {
}
