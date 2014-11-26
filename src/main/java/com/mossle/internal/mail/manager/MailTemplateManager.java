package com.mossle.internal.mail.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.internal.mail.domain.MailTemplate;

import org.springframework.stereotype.Service;

@Service
public class MailTemplateManager extends HibernateEntityDao<MailTemplate> {
}
