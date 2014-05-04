package com.mossle.bpm.persistence.manager;

import com.mossle.bpm.persistence.domain.BpmConfCountersign;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class BpmConfCountersignManager extends
        HibernateEntityDao<BpmConfCountersign> {
}
