package com.mossle.forum.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.forum.domain.ForumPost;

import org.springframework.stereotype.Service;

@Service
public class ForumPostManager extends HibernateEntityDao<ForumPost> {
}
