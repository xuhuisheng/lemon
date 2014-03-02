package com.mossle.bpm.persistence.manager;

import com.mossle.bpm.persistence.domain.BpmConfUser;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class BpmConfUserManager extends HibernateEntityDao<BpmConfUser> {
}
