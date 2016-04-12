package com.mossle.leave.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.leave.persistence.domain.LeaveInfo;

import org.springframework.stereotype.Service;

@Service
public class LeaveInfoManager extends HibernateEntityDao<LeaveInfo> {
}
