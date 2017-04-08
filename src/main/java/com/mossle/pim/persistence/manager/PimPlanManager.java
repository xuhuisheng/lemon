package com.mossle.pim.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.pim.persistence.domain.PimPlan;

import org.springframework.stereotype.Service;

@Service
public class PimPlanManager extends HibernateEntityDao<PimPlan> {
}
