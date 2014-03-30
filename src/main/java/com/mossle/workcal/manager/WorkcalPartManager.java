package com.mossle.workcal.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.workcal.domain.WorkcalPart;

import org.springframework.stereotype.Service;

@Service
public class WorkcalPartManager extends HibernateEntityDao<WorkcalPart> {
}
