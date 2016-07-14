package com.mossle.visitor.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.visitor.persistence.domain.VisitorInfo;

import org.springframework.stereotype.Service;

@Service
public class VisitorInfoManager extends HibernateEntityDao<VisitorInfo> {
}
