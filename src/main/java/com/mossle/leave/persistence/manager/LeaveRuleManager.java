package com.mossle.leave.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.leave.persistence.domain.LeaveRule;

import org.springframework.stereotype.Service;

@Service
public class LeaveRuleManager extends HibernateEntityDao<LeaveRule> {
}
