package com.mossle.internal.delegate.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.internal.delegate.persistence.domain.DelegateInfo;

import org.springframework.stereotype.Service;

@Service
public class DelegateInfoManager extends HibernateEntityDao<DelegateInfo> {
}
