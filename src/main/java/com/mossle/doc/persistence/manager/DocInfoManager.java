package com.mossle.doc.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.doc.persistence.domain.DocInfo;

import org.springframework.stereotype.Service;

@Service
public class DocInfoManager extends HibernateEntityDao<DocInfo> {
}
