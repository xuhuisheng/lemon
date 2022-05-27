package com.mossle.notification.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.notification.persistence.domain.NotificationQueue;

import org.springframework.stereotype.Service;

@Service
public class NotificationQueueManager extends
        HibernateEntityDao<NotificationQueue> {
}
