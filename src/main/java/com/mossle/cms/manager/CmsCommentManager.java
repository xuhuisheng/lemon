package com.mossle.cms.manager;

import com.mossle.cms.domain.CmsComment;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class CmsCommentManager extends HibernateEntityDao<CmsComment> {
}
