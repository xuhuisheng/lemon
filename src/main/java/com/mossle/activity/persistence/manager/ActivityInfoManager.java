package com.mossle.activity.persistence.manager;

import com.mossle.activity.persistence.domain.ActivityInfo;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class ActivityInfoManager extends HibernateEntityDao<ActivityInfo> {
}
