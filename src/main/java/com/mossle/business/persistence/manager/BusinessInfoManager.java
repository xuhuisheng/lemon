package com.mossle.business.persistence.manager;

import com.mossle.business.persistence.domain.BusinessInfo;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class BusinessInfoManager extends HibernateEntityDao<BusinessInfo> {
}
