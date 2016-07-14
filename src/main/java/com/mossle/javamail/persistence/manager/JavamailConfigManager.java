package com.mossle.javamail.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.javamail.persistence.domain.JavamailConfig;

import org.springframework.stereotype.Service;

@Service
public class JavamailConfigManager extends HibernateEntityDao<JavamailConfig> {
}
