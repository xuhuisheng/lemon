package com.mossle.scope.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.scope.domain.ScopeInfo;

import org.springframework.stereotype.Service;

@Service
public class ScopeInfoManager extends HibernateEntityDao<ScopeInfo> {
}
