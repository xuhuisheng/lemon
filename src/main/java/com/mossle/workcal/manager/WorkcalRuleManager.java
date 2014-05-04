package com.mossle.workcal.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.workcal.domain.WorkcalRule;

import org.springframework.stereotype.Service;

@Service
public class WorkcalRuleManager extends HibernateEntityDao<WorkcalRule> {
}
