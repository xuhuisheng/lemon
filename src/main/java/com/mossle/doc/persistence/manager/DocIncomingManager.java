package com.mossle.doc.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.doc.persistence.domain.DocIncoming;

import org.springframework.stereotype.Service;

@Service
public class DocIncomingManager extends HibernateEntityDao<DocIncoming> {
}
