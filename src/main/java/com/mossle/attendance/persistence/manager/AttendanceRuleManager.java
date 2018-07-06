package com.mossle.attendance.persistence.manager;

import com.mossle.attendance.persistence.domain.AttendanceRule;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class AttendanceRuleManager extends HibernateEntityDao<AttendanceRule> {
}
