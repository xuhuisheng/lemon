package com.mossle.cms.manager;

import com.mossle.cms.domain.CmsArticle;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class CmsArticleManager extends HibernateEntityDao<CmsArticle> {
}
