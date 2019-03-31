package com.mossle.leave.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.leave.persistence.domain.LeaveUser;

import org.springframework.stereotype.Service;

@Service
public class LeaveUserManager extends HibernateEntityDao<LeaveUser> {
}
