package com.mossle.cms.persistence.manager;

import com.mossle.cms.persistence.domain.CmsTemplateCatalog;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class CmsTemplateCatalogManager extends
        HibernateEntityDao<CmsTemplateCatalog> {
}
