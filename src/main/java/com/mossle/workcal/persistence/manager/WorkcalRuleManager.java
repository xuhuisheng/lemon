package com.mossle.workcal.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.workcal.persistence.domain.WorkcalRule;

import org.springframework.stereotype.Service;

@Service
public class WorkcalRuleManager extends HibernateEntityDao<WorkcalRule> {
}
