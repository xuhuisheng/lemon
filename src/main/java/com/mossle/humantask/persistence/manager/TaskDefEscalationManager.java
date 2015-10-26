package com.mossle.humantask.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.humantask.persistence.domain.TaskDefEscalation;

import org.springframework.stereotype.Service;

@Service
public class TaskDefEscalationManager extends
        HibernateEntityDao<TaskDefEscalation> {
}
