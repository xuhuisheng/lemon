package com.mossle.notification.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.notification.persistence.domain.NotificationConfig;

import org.springframework.stereotype.Service;

@Service
public class NotificationConfigManager extends
        HibernateEntityDao<NotificationConfig> {
}
