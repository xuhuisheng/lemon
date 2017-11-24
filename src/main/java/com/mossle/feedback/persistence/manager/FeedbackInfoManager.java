package com.mossle.feedback.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.feedback.persistence.domain.FeedbackInfo;

import org.springframework.stereotype.Service;

@Service
public class FeedbackInfoManager extends HibernateEntityDao<FeedbackInfo> {
}
