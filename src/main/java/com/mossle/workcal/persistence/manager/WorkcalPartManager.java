package com.mossle.workcal.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.workcal.persistence.domain.WorkcalPart;

import org.springframework.stereotype.Service;

@Service
public class WorkcalPartManager extends HibernateEntityDao<WorkcalPart> {
}
