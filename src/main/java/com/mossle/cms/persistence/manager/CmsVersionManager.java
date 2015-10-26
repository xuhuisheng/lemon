package com.mossle.cms.persistence.manager;

import com.mossle.cms.persistence.domain.CmsVersion;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class CmsVersionManager extends HibernateEntityDao<CmsVersion> {
}
