package com.mossle.group.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.group.domain.JobType;

import org.springframework.stereotype.Service;

@Service
public class JobTypeManager extends HibernateEntityDao<JobType> {
}
