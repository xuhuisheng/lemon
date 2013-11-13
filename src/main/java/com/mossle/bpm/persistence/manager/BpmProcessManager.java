package com.mossle.bpm.persistence.manager;

import com.mossle.bpm.persistence.domain.BpmProcess;

import com.mossle.core.hibernate.HibernateEntityDao;

import org.springframework.stereotype.Service;

@Service
public class BpmProcessManager extends HibernateEntityDao<BpmProcess> {
}
