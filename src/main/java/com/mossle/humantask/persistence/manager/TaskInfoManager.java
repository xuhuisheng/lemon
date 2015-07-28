package com.mossle.humantask.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.humantask.persistence.domain.TaskInfo;

import org.springframework.stereotype.Service;

@Service
public class TaskInfoManager extends HibernateEntityDao<TaskInfo> {
}
