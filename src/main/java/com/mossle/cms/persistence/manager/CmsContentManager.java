package com.mossle.cms.persistence.manager;

import com.mossle.cms.persistence.domain.CmsContent;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class CmsContentManager extends HibernateEntityDao<CmsContent> {
}
