package com.mossle.notification.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.notification.persistence.domain.NotificationTemplate;

import org.springframework.stereotype.Service;

@Service
public class NotificationTemplateManager extends
        HibernateEntityDao<NotificationTemplate> {
}
