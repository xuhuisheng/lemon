package com.mossle.humantask.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.humantask.persistence.domain.TaskDefNotification;

import org.springframework.stereotype.Service;

@Service
public class TaskDefNotificationManager extends
        HibernateEntityDao<TaskDefNotification> {
}
