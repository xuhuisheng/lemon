package com.mossle.notification.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.notification.persistence.domain.NotificationCatalog;

import org.springframework.stereotype.Service;

@Service
public class NotificationCatalogManager extends
        HibernateEntityDao<NotificationCatalog> {
}
