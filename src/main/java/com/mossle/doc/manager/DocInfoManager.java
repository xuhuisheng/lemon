package com.mossle.doc.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.doc.domain.DocInfo;

import org.springframework.stereotype.Service;

@Service
public class DocInfoManager extends HibernateEntityDao<DocInfo> {
}
