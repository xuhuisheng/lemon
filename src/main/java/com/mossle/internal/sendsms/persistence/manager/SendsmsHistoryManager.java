package com.mossle.internal.sendsms.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.internal.sendsms.persistence.domain.SendsmsHistory;

import org.springframework.stereotype.Service;

@Service
public class SendsmsHistoryManager extends HibernateEntityDao<SendsmsHistory> {
}
