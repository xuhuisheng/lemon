package com.mossle.performance.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.performance.persistence.domain.PerformanceInfo;

import org.springframework.stereotype.Service;

@Service
public class PerformanceInfoManager extends HibernateEntityDao<PerformanceInfo> {
}
