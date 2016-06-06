package com.mossle.internal.sendsms.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.internal.sendsms.persistence.domain.SendsmsQueue;

import org.springframework.stereotype.Service;

@Service
public class SendsmsQueueManager extends HibernateEntityDao<SendsmsQueue> {
}
