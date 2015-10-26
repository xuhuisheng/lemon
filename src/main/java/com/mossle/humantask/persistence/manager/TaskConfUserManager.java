package com.mossle.humantask.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.humantask.persistence.domain.TaskConfUser;

import org.springframework.stereotype.Service;

@Service
public class TaskConfUserManager extends HibernateEntityDao<TaskConfUser> {
}
