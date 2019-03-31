package com.mossle.doc.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.doc.persistence.domain.DocDispatch;

import org.springframework.stereotype.Service;

@Service
public class DocDispatchManager extends HibernateEntityDao<DocDispatch> {
}
