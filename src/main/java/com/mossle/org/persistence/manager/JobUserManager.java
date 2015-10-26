package com.mossle.org.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.org.persistence.domain.JobUser;

import org.springframework.stereotype.Service;

@Service
public class JobUserManager extends HibernateEntityDao<JobUser> {
}
