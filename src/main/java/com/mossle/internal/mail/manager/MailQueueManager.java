package com.mossle.internal.mail.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.internal.mail.domain.MailQueue;

import org.springframework.stereotype.Service;

@Service
public class MailQueueManager extends HibernateEntityDao<MailQueue> {
}
