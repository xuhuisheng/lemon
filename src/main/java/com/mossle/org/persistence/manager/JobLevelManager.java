package com.mossle.org.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.org.persistence.domain.JobLevel;

import org.springframework.stereotype.Service;

@Service
public class JobLevelManager extends HibernateEntityDao<JobLevel> {
}
