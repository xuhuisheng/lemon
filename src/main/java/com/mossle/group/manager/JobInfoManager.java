package com.mossle.group.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.group.domain.JobInfo;

import org.springframework.stereotype.Service;

@Service
public class JobInfoManager extends HibernateEntityDao<JobInfo> {
}
