package com.mossle.expense.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.expense.persistence.domain.ExpenseRequest;

import org.springframework.stereotype.Service;

@Service
public class ExpenseRequestManager extends HibernateEntityDao<ExpenseRequest> {
}
