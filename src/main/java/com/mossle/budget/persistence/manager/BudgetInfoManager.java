package com.mossle.budget.persistence.manager;

import com.mossle.budget.persistence.domain.BudgetInfo;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class BudgetInfoManager extends HibernateEntityDao<BudgetInfo> {
}
