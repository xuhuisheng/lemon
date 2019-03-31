package com.mossle.leave.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.leave.persistence.domain.LeaveLog;

import org.springframework.stereotype.Service;

@Service
public class LeaveLogManager extends HibernateEntityDao<LeaveLog> {
}
