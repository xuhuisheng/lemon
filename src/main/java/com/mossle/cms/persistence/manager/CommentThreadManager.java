package com.mossle.cms.persistence.manager;

import com.mossle.cms.persistence.domain.CommentThread;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class CommentThreadManager extends HibernateEntityDao<CommentThread> {
}
