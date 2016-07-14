package com.mossle.pim.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.pim.persistence.domain.PimSchedule;

import org.springframework.stereotype.Service;

@Service
public class PimScheduleManager extends HibernateEntityDao<PimSchedule> {
}
