package com.mossle.internal.delegate.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.internal.delegate.persistence.domain.DelegateHistory;

import org.springframework.stereotype.Service;

@Service
public class DelegateHistoryManager extends HibernateEntityDao<DelegateHistory> {
}
