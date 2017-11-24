package com.mossle.feedback.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.feedback.persistence.domain.FeedbackCatalog;

import org.springframework.stereotype.Service;

@Service
public class FeedbackCatalogManager extends HibernateEntityDao<FeedbackCatalog> {
}
