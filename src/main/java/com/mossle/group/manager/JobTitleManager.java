package com.mossle.group.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.group.domain.JobTitle;

import org.springframework.stereotype.Service;

@Service
public class JobTitleManager extends HibernateEntityDao<JobTitle> {
}
