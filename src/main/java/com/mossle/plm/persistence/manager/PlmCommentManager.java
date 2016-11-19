package com.mossle.plm.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.plm.persistence.domain.PlmComment;

import org.springframework.stereotype.Service;

@Service
public class PlmCommentManager extends HibernateEntityDao<PlmComment> {
}
