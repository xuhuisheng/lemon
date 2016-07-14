package com.mossle.humantask.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.humantask.persistence.domain.TaskDefOperation;

import org.springframework.stereotype.Service;

@Service
public class TaskDefOperationManager extends
        HibernateEntityDao<TaskDefOperation> {
}
