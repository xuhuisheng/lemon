package com.mossle.org.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.org.persistence.domain.JobType;

import org.springframework.stereotype.Service;

@Service
public class JobTypeManager extends HibernateEntityDao<JobType> {
}
