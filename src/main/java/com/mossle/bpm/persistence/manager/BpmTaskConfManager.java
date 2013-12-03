package com.mossle.bpm.persistence.manager;

import com.mossle.bpm.persistence.domain.BpmTaskConf;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class BpmTaskConfManager extends HibernateEntityDao<BpmTaskConf> {
}
