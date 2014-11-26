package com.mossle.internal.mail.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.internal.mail.domain.MailHistory;

import org.springframework.stereotype.Service;

@Service
public class MailHistoryManager extends HibernateEntityDao<MailHistory> {
}
