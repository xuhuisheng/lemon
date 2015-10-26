package com.mossle.javamail.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.javamail.persistence.domain.JavamailMessage;

import org.springframework.stereotype.Service;

@Service
public class JavamailMessageManager extends HibernateEntityDao<JavamailMessage> {
}
