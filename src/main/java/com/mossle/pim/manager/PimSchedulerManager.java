package com.mossle.pim.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.pim.domain.PimScheduler;

import org.springframework.stereotype.Service;

@Service
public class PimSchedulerManager extends HibernateEntityDao<PimScheduler> {
}
