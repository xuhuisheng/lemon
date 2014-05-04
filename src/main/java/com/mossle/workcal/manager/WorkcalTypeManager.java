package com.mossle.workcal.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.workcal.domain.WorkcalType;

import org.springframework.stereotype.Service;

@Service
public class WorkcalTypeManager extends HibernateEntityDao<WorkcalType> {
}
