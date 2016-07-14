package com.mossle.workcal.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.workcal.persistence.domain.WorkcalType;

import org.springframework.stereotype.Service;

@Service
public class WorkcalTypeManager extends HibernateEntityDao<WorkcalType> {
}
