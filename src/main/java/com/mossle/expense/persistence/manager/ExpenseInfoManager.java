package com.mossle.expense.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.expense.persistence.domain.ExpenseInfo;

import org.springframework.stereotype.Service;

@Service
public class ExpenseInfoManager extends HibernateEntityDao<ExpenseInfo> {
}
