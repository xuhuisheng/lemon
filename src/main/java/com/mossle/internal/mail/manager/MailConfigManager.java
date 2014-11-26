package com.mossle.internal.mail.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.internal.mail.domain.MailConfig;

import org.springframework.stereotype.Service;

@Service
public class MailConfigManager extends HibernateEntityDao<MailConfig> {
}
