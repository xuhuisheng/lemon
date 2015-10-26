package com.mossle.humantask.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.humantask.persistence.domain.TaskLog;

import org.springframework.stereotype.Service;

@Service
public class TaskLogManager extends HibernateEntityDao<TaskLog> {
}
