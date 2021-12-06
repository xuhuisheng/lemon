package com.mossle.cms.persistence.manager;

import com.mossle.cms.persistence.domain.CmsCount;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class CmsCountManager extends HibernateEntityDao<CmsCount> {
}
