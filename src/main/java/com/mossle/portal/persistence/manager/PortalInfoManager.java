package com.mossle.portal.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.portal.persistence.domain.PortalInfo;

import org.springframework.stereotype.Service;

@Service
public class PortalInfoManager extends HibernateEntityDao<PortalInfo> {
}
