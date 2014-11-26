package com.mossle.internal.mail.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.internal.mail.domain.MailAttachment;

import org.springframework.stereotype.Service;

@Service
public class MailAttachmentManager extends HibernateEntityDao<MailAttachment> {
}
