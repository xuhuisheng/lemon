package com.mossle.group.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.group.domain.JobUser;

import org.springframework.stereotype.Service;

@Service
public class JobUserManager extends HibernateEntityDao<JobUser> {
}
