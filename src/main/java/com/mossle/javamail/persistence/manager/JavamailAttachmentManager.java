package com.mossle.javamail.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.javamail.persistence.domain.JavamailAttachment;

import org.springframework.stereotype.Service;

@Service
public class JavamailAttachmentManager extends
        HibernateEntityDao<JavamailAttachment> {
}
