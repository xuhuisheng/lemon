package com.mossle.cms.persistence.manager;

import com.mossle.cms.persistence.domain.CmsArticle;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class CmsArticleManager extends HibernateEntityDao<CmsArticle> {
}
