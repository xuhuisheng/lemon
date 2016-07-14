package com.mossle.internal.sendsms.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.internal.sendsms.persistence.domain.SendsmsConfig;

import org.springframework.stereotype.Service;

@Service
public class SendsmsConfigManager extends HibernateEntityDao<SendsmsConfig> {
}
