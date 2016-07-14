package com.mossle.org.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.org.persistence.domain.JobInfo;

import org.springframework.stereotype.Service;

@Service
public class JobInfoManager extends HibernateEntityDao<JobInfo> {
}
