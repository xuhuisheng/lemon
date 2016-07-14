package com.mossle.org.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.org.persistence.domain.JobTitle;

import org.springframework.stereotype.Service;

@Service
public class JobTitleManager extends HibernateEntityDao<JobTitle> {
}
