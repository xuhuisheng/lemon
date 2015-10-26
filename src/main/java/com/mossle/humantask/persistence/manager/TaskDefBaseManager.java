package com.mossle.humantask.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.humantask.persistence.domain.TaskDefBase;

import org.springframework.stereotype.Service;

@Service
public class TaskDefBaseManager extends HibernateEntityDao<TaskDefBase> {
}
