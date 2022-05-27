package com.mossle.notification.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.notification.persistence.domain.NotificationProvider;

import org.springframework.stereotype.Service;

@Service
public class NotificationProviderManager extends
        HibernateEntityDao<NotificationProvider> {
}
