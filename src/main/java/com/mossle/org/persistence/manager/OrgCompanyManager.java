package com.mossle.org.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.org.persistence.domain.OrgCompany;

import org.springframework.stereotype.Service;

@Service
public class OrgCompanyManager extends HibernateEntityDao<OrgCompany> {
}
