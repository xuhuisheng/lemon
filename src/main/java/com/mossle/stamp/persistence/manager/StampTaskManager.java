package com.mossle.stamp.persistence.manager;

import com.mossle.core.hibernate.HibernateEntityDao;

import com.mossle.stamp.persistence.domain.StampTask;

import org.springframework.stereotype.Service;

@Service
public class StampTaskManager extends HibernateEntityDao<StampTask> {
}
