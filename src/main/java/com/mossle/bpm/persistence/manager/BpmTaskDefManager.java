package com.mossle.bpm.persistence.manager;

import com.mossle.bpm.persistence.domain.BpmTaskDef;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class BpmTaskDefManager extends HibernateEntityDao<BpmTaskDef> {
}
