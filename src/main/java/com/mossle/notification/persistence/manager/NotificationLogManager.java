package com.mossle.notification.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.notification.persistence.domain.NotificationLog;

import org.springframework.stereotype.Service;

@Service
public class NotificationLogManager extends HibernateEntityDao<NotificationLog> {
}
