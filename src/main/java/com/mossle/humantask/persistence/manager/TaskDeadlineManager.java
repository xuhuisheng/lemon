package com.mossle.humantask.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.humantask.persistence.domain.TaskDeadline;

import org.springframework.stereotype.Service;

@Service
public class TaskDeadlineManager extends HibernateEntityDao<TaskDeadline> {
}
