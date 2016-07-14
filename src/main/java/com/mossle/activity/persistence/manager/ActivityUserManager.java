package com.mossle.activity.persistence.manager;

import com.mossle.activity.persistence.domain.ActivityUser;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class ActivityUserManager extends HibernateEntityDao<ActivityUser> {
}
