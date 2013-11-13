package com.mossle.forum.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.forum.domain.ForumTopic;

import org.springframework.stereotype.Service;

@Service
public class ForumTopicManager extends HibernateEntityDao<ForumTopic> {
}
