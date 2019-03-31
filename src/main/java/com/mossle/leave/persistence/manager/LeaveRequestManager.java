package com.mossle.leave.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.leave.persistence.domain.LeaveRequest;

import org.springframework.stereotype.Service;

@Service
public class LeaveRequestManager extends HibernateEntityDao<LeaveRequest> {
}
