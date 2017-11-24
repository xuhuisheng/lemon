package com.mossle.pim.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.pim.persistence.domain.PimTask;

import org.springframework.stereotype.Service;

@Service
public class PimTaskManager extends HibernateEntityDao<PimTask> {
}
