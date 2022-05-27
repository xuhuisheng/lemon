package com.mossle.notification.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.notification.persistence.domain.NotificationMessage;

import org.springframework.stereotype.Service;

@Service
public class NotificationMessageManager extends
        HibernateEntityDao<NotificationMessage> {
}
